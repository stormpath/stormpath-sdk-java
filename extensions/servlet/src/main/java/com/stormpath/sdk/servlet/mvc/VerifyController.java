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
import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @since 1.0.RC4
 */
public class VerifyController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(VerifyController.class);

    private String loginUri;
    private String loginNextUri;
    private Client client;
    private boolean autoLogin;
    private AccountModelFactory accountModelFactory;
    private ErrorModelFactory errorModelFactory;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private AccountStoreResolver accountStoreResolver;

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public void setLoginNextUri(String loginNextUri) {
        this.loginNextUri = loginNextUri;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public void setAccountModelFactory(AccountModelFactory accountModelFactory) {
        this.accountModelFactory = accountModelFactory;
    }

    public void setErrorModelFactory(ErrorModelFactory errorModelFactory) {
        this.errorModelFactory = errorModelFactory;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public void setAccountStoreResolver(AccountStoreResolver accountStoreResolver) {
        this.accountStoreResolver = accountStoreResolver;
    }

    @Override
    public void init() throws Exception {

        super.init();

        if (this.accountModelFactory == null) {
            this.accountModelFactory = new DefaultAccountModelFactory();
        }
        if (this.errorModelFactory == null) {
            this.errorModelFactory = new VerifyErrorModelFactory(this.messageSource);
        }

        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.hasText(loginNextUri, "logoutUri cannot be null or empty.");
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(accountModelFactory, "accountModelFactory cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            if (isJsonPreferred(request, response)) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("status", HttpServletResponse.SC_BAD_REQUEST);
                model.put("message", i18n(request, "stormpath.web.verifyEmail.form.errors.noToken"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, model);
            }
            Map<String, ?> model = createModel(request, response);
            return new DefaultViewModel(view, model).setRedirect(false);
        }

        try {
            return verify(request, response, sptoken);
        } catch (Exception e) {
            if (isJsonPreferred(request, response)) {
                ErrorModel error = errorModelFactory.toError(request, e);
                response.setStatus(error.getStatus());
                Map<String, Object> model = error.toMap();
                return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, model);
            }
            List<ErrorModel> errors = new ArrayList<ErrorModel>();
            ErrorModel error = ErrorModel.builder()
                .setStatus(HttpServletResponse.SC_BAD_REQUEST)
                .setMessage(i18n(request, "stormpath.web.verifyEmail.form.errors.invalidLink"))
                .build();
            errors.add(error);
            Map<String, ?> model = createModel(request, response, null, errors);
            return new DefaultViewModel(view, model);
        }
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        return com.stormpath.sdk.lang.Collections.toList(errorModelFactory.toError(request, e));
    }

    protected ViewModel verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
        throws ServletException, IOException {

        Account account = client.verifyAccountEmail(sptoken);
        //The sptoken is valid

        RequestEvent e = createVerifiedEvent(request, response, account);
        publish(e);

        if (isJsonPreferred(request, response)) {
            if (autoLogin) {
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(request, response, result);

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("account", accountModelFactory.toMap(account, Collections.EMPTY_LIST));
                return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, model);
            } else {
                return null;
            }
        } else {
            if (autoLogin) {
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(request, response, result);

                return new DefaultViewModel(loginNextUri).setRedirect(true);
            } else {

                return new DefaultViewModel(nextUri).setRedirect(true);
            }
        }
    }

    protected VerifiedAccountRequestEvent createVerifiedEvent(HttpServletRequest request, HttpServletResponse response,
                                                              Account account) {
        return new DefaultVerifiedAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {
        try {
            eventPublisher.publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish verified account request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
    }

    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) {

        Application application = (Application) request.getAttribute(Application.class.getName());

        String login = getFieldValueResolver().getValue(request, "login");

        /*Fallback to email for backwards compatibility*/
        if (!StringUtils.hasText(login)) {
            login = getFieldValueResolver().getValue(request, "email");
        }

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            request.setAttribute("form", form);
            AccountStore accountStore = accountStoreResolver.getAccountStore(request, response);

            VerificationEmailRequest verificationEmailRequest = Applications.verificationEmailBuilder()
                    .setLogin(login)
                    .setAccountStore(accountStore)
                    .build();

            application.sendVerificationEmail(verificationEmailRequest);
        } finally {
            if (isJsonPreferred(request, response)) {
                return null;
            }

            return new DefaultViewModel(nextUri.replace("status=verified", "status=unverified")).setRedirect(true);
        }

    }

    @Override
    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {
        super.validate(request, response, form);

        String loginValue = form.getFieldValue("login");
        String emailValue = form.getFieldValue("email");

        if (StringUtils.hasText(loginValue) && StringUtils.hasText(emailValue)) {
            String key = "stormpath.web." + getControllerKey() + ".form.errors.fieldConflict";
            String msg = i18n(request, key);
            throw new ValidationException(msg);
        } else if (!StringUtils.hasText(loginValue) && !StringUtils.hasText(emailValue)) {
            String key = "stormpath.web." + getControllerKey() + ".form.fields.login.required";
            String msg = i18n(request, key);
            throw new ValidationException(msg);
        }
    }
}
