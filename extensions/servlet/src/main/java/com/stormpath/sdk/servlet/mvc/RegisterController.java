/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultAccountStoreModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class RegisterController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private Client client;
    //only used if account does not need email verification:
    private Saver<AuthenticationResult> authenticationResultSaver;
    private AccountModelFactory accountModelFactory;
    private AccountStoreModelFactory accountStoreModelFactory;
    private ErrorModelFactory errorModelFactory;
    private WebHandler preRegisterHandler;
    private WebHandler postRegisterHandler;

    private boolean autoLogin;

    private String loginUri;
    private String verifyViewName;

    public static final List<String> ACCOUNT_PROPERTIES = Collections.unmodifiableList(Arrays.asList("email", "username", "password", "givenName", "middleName", "surname"));

    public RegisterController() {
        super();
    }

    public RegisterController(Config config, Client client) {
        super(config.getRegisterControllerConfig(), config.getProducesMediaTypes());

        this.client = client;
        this.authenticationResultSaver = config.getAuthenticationResultSaver();
        this.loginUri = config.getLoginControllerConfig().getUri();
        this.verifyViewName = config.getVerifyControllerConfig().getView();
        this.autoLogin = config.isRegisterAutoLoginEnabled();

        this.preRegisterHandler = config.getRegisterPreHandler();
        this.postRegisterHandler = config.getRegisterPostHandler();

        this.accountModelFactory = new DefaultAccountModelFactory();
        this.accountStoreModelFactory = new DefaultAccountStoreModelFactory();
        this.errorModelFactory = new RegisterErrorModelFactory(this.messageSource);

        Assert.notNull(this.client, "client cannot be null.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver cannot be null.");
        Assert.hasText(this.loginUri, "loginUri cannot be null or empty.");
        Assert.hasText(this.verifyViewName, "verifyViewName cannot be null or empty.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        if (!isJsonPreferred(request, response)) {
            model.put("loginUri", loginUri);
        } else {
            model.put("accountStores", accountStoreModelFactory.getAccountStores(request));
        }
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        log.debug("Unable to register account.", e);

        return Arrays.asList(errorModelFactory.toError(request, e));
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {
        super.validate(request, response, form);

        Field confirmPasswordField = form.getField("confirmPassword");

        if (confirmPasswordField != null && confirmPasswordField.isEnabled()) {
            //ensure passwords match:
            String password = form.getFieldValue("password");
            String confirmPassword = form.getFieldValue("confirmPassword");

            if (!password.equals(confirmPassword) && confirmPasswordField.isRequired()) {
                String key = "stormpath.web.register.form.errors.passwordMismatch";
                String msg = i18n(request, key);
                throw new MismatchedPasswordException(msg);
            }
        }
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest req, HttpServletResponse resp, Form form) throws Exception {

        //Create a new Account instance that will represent the submitted user information:
        Account account = client.instantiate(Account.class);

        String value = form.getFieldValue("email");
        if (value != null) {
            account.setEmail(value);
        }

        value = form.getFieldValue("username");
        if (value != null) {
            account.setUsername(value);
        }

        value = form.getFieldValue("password");
        if (value != null) {
            account.setPassword(value);
        }

        value = form.getFieldValue("givenName");
        account.setGivenName(value != null ? value : "UNKNOWN");

        value = form.getFieldValue("middleName");
        if (value != null) {
            account.setMiddleName(value);
        }

        value = form.getFieldValue("surname");
        account.setSurname(value != null ? value : "UNKNOWN");

        account.getCustomData().putAll(getCustomData(req, form));

        //Get the Stormpath Application instance corresponding to this web app:
        Application app = (Application) req.getAttribute(Application.class.getName());

        if (preRegisterHandler != null) {
            if (!preRegisterHandler.handle(req, resp, account)) {
                return null;
            }
        }

        //now persist the new account, and ensure our account reference points to the newly created/returned instance:
        account = app.createAccount(account);

        publishRequestEvent(new DefaultRegisteredAccountRequestEvent(req, resp, account));

        if (postRegisterHandler != null) {
            if (!postRegisterHandler.handle(req, resp, account)) {
                return null;
            }
        }

        AccountStatus status = account.getStatus();

        if (isJsonPreferred(req, resp)) {
            //noinspection unchecked
            return new DefaultViewModel("stormpathJsonView", java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, Collections.EMPTY_LIST)));
        }

        if (status == AccountStatus.ENABLED) {
            if (autoLogin) {
                //the user does not need to verify their email address, so just assume they are authenticated
                //(since they specified their password during registration):
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(req, resp, result);
            } else {
                return new DefaultViewModel(loginUri + "?status=created").setRedirect(true);
            }
        } else if (status == AccountStatus.UNVERIFIED) {
            return new DefaultViewModel(loginUri + "?status=unverified").setRedirect(true);
        }
        return new DefaultViewModel(nextUri).setRedirect(true);
    }

    private Map<String, Object> getCustomData(HttpServletRequest request, Form form) {
        //Custom fields are either declared as form fields which shouldn't not be account fields or through a customField attribute
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        for (Field field : form.getFields()) {
            //Field is not part of the default account properties then is a custom field
            if (!field.getName().equals(csrfTokenManager.getTokenName()) && !ACCOUNT_PROPERTIES.contains(field.getName())) {
                result.put(field.getName(), field.getValue());
            }
        }

        Object customData = fieldValueResolver.getAllFields(request).get("customData");
        if (customData instanceof Map) {
            //noinspection unchecked
            result.putAll((Map<? extends String, ?>) customData);
        } //If not a map ignore, the spec doesn't cover this case

        return result;
    }
}
