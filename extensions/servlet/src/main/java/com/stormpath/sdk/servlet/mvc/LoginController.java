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
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultAccountStoreModelFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @since 1.0.RC4
 */
public class LoginController extends FormController {

    private String forgotLoginUri;
    private String verifyUri;
    private String registerUri;
    private String logoutUri;
    private Boolean verifyEnabled;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private ErrorModelFactory errorModelFactory;
    private LoginFormStatusResolver loginFormStatusResolver;
    private AccountStoreModelFactory accountStoreModelFactory;
    private AccountModelFactory accountModelFactory;

    public LoginController() {
        super();
    }

    public LoginController(ControllerConfigResolver controllerConfigResolver,
                           ControllerConfigResolver verifyControllerConfigResolver,
                           String forgotLoginUri,
                           String registerUri,
                           String logoutUri,
                           Saver<AuthenticationResult> authenticationResultSaver,
                           ErrorModelFactory errorModelFactory) {
        super(controllerConfigResolver);

        this.forgotLoginUri = forgotLoginUri;
        this.verifyUri = verifyControllerConfigResolver.getUri();
        this.verifyEnabled = verifyControllerConfigResolver.isEnabled();
        this.registerUri = registerUri;
        this.logoutUri = logoutUri;
        this.authenticationResultSaver = authenticationResultSaver;
        this.errorModelFactory = errorModelFactory;
        this.formFields = controllerConfigResolver.getFormFields();

        this.loginFormStatusResolver = new DefaultLoginFormStatusResolver(this.messageSource, this.verifyUri);
        this.accountStoreModelFactory = new DefaultAccountStoreModelFactory();
        this.accountModelFactory = new DefaultAccountModelFactory();

        if (this.errorModelFactory == null) {
            this.errorModelFactory = new LoginErrorModelFactory(this.messageSource);
        }

        Assert.hasText(this.forgotLoginUri, "forgotLoginUri property cannot be null or empty.");
        Assert.hasText(this.verifyUri, "verifyUri property cannot be null or empty.");
        Assert.hasText(this.registerUri, "registerUri property cannot be null or empty.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.verifyEnabled, "verifyEnabled property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
        Assert.notNull(this.accountStoreModelFactory, "accountStoreModelFactory cannot be null.");
        Assert.notNull(this.errorModelFactory, "errorModelFactory cannot be null.");
        Assert.notNull(this.formFields, "loginFormFields cannot be null.");
        Assert.notNull(this.accountModelFactory, "accountModelFactory cannot be null.");
        Assert.notNull(this.loginFormStatusResolver, "loginFormStatusResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("accountStores", accountStoreModelFactory.getAccountStores(request));

        if (UserAgents.get(request).isHtmlPreferred()) {
            model.put("forgotLoginUri", forgotLoginUri);
            model.put("verifyUri", verifyUri);
            model.put("verifyEnabled", verifyEnabled);
            model.put("registerUri", registerUri);
            model.put("oauthStateToken", UUID.randomUUID().toString());
            String status = request.getParameter("status");
            if (status != null) {
                model.put("status", loginFormStatusResolver.getStatusMessage(request, status));
            }
        }
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        return Arrays.asList(errorModelFactory.toError(request, e));
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest req, HttpServletResponse resp, Form form) throws Exception {

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        final Account account = getAccount(req);

        //simulate a result for the benefit of the 'saveResult' method signature:
        final AuthenticationResult result = new TransientAuthenticationResult(account);
        authenticationResultSaver.set(req, resp, result);

        if (UserAgents.get(req).isJsonPreferred()) {
            //noinspection unchecked
            return new DefaultViewModel(view, java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, Collections.EMPTY_LIST)));
        }

        //otherwise HTML view:
        return new DefaultViewModel(nextUri).setRedirect(true);
    }

    protected Account getAccount(HttpServletRequest req) {
        return AccountResolver.INSTANCE.getRequiredAccount(req);
    }
}
