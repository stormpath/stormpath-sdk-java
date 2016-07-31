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
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModel;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.ExternalAccountStoreModelFactory;
import com.stormpath.sdk.servlet.oauth.OAuthTokenResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @since 1.0.RC4
 */
public class LoginController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private String forgotPasswordUri;
    private String verifyUri;
    private String registerUri;
    private String logoutUri;
    private boolean verifyEnabled = true;
    private boolean forgotPasswordEnabled = true;
    private boolean idSiteEnabled;
    private boolean callbackEnabled;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private ErrorModelFactory errorModelFactory;
    private LoginFormStatusResolver loginFormStatusResolver;
    private AccountStoreModelFactory accountStoreModelFactory = new ExternalAccountStoreModelFactory();
    private AccountModelFactory accountModelFactory = new DefaultAccountModelFactory();
    private WebHandler preLoginHandler;
    private WebHandler postLoginHandler;
    private Resolver<Boolean> registerEnabledResolver;

    public void setForgotPasswordUri(String forgotPasswordUri) {
        this.forgotPasswordUri = forgotPasswordUri;
    }

    public void setVerifyUri(String verifyUri) {
        this.verifyUri = verifyUri;
    }

    public void setRegisterUri(String registerUri) {
        this.registerUri = registerUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    public void setVerifyEnabled(boolean verifyEnabled) {
        this.verifyEnabled = verifyEnabled;
    }

    public void setForgotPasswordEnabled(boolean forgotPasswordEnabled) {
        this.forgotPasswordEnabled = forgotPasswordEnabled;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public void setErrorModelFactory(ErrorModelFactory errorModelFactory) {
        this.errorModelFactory = errorModelFactory;
    }

    public void setLoginFormStatusResolver(LoginFormStatusResolver loginFormStatusResolver) {
        this.loginFormStatusResolver = loginFormStatusResolver;
    }

    public void setAccountStoreModelFactory(AccountStoreModelFactory accountStoreModelFactory) {
        this.accountStoreModelFactory = accountStoreModelFactory;
    }

    public void setAccountModelFactory(AccountModelFactory accountModelFactory) {
        this.accountModelFactory = accountModelFactory;
    }

    public void setPreLoginHandler(WebHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    public void setPostLoginHandler(WebHandler postLoginHandler) {
        this.postLoginHandler = postLoginHandler;
    }

    public void setIdSiteEnabled(boolean idSiteEnabled) {
        this.idSiteEnabled = idSiteEnabled;
    }

    public void setCallbackEnabled(boolean callbackEnabled) {
        this.callbackEnabled = callbackEnabled;
    }

    public void setRegisterEnabledResolver(Resolver<Boolean> registerEnabledResolver) {
        this.registerEnabledResolver = registerEnabledResolver;
    }

    @Override
    public void init() throws Exception {
        super.init();
        Assert.hasText(this.verifyUri, "verifyUri property cannot be null or empty.");

        if (this.loginFormStatusResolver == null) {
            this.loginFormStatusResolver = new DefaultLoginFormStatusResolver(this.messageSource, this.verifyUri);
        }
        if (this.errorModelFactory == null) {
            this.errorModelFactory = new LoginErrorModelFactory(this.messageSource);
        }
        if (this.accountStoreModelFactory == null) {
            this.accountStoreModelFactory = new ExternalAccountStoreModelFactory();
        }
        if (this.accountModelFactory == null) {
            this.accountModelFactory = new DefaultAccountModelFactory();
        }

        Assert.hasText(this.forgotPasswordUri, "forgotPasswordUri property cannot be null or empty.");
        Assert.hasText(this.registerUri, "registerUri property cannot be null or empty.");
        Assert.notNull(this.registerEnabledResolver, "registerEnabledResolver cannot be null.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
        Assert.notNull(this.errorModelFactory, "errorModelFactory cannot be null.");
        Assert.notNull(this.preLoginHandler, "preLoginHandler cannot be null.");
        Assert.notNull(this.postLoginHandler, "postLoginHandler cannot be null.");
        Assert.notNull(this.loginFormStatusResolver, "loginFormStatusResolver cannot be null.");
        Assert.notNull(this.accountStoreModelFactory, "accountStoreModelFactory cannot be null.");
        Assert.notNull(this.accountModelFactory, "accountModelFactory cannot be null.");
        Assert.notNull(this.applicationResolver, "applicationResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {

        final List<AccountStoreModel> accountStores = accountStoreModelFactory.getAccountStores(request);

        // 748: If stormpath.web.idSite.enabled is false and stormpath.web.callback.enabled is false AND
        // there are SAML directories mapped to the application, that is a configuration error.
        if (!idSiteEnabled && !callbackEnabled && containsSaml(accountStores)) {
            String errorMsg = "ID Site is disabled and callbacks are disabled, yet this application has SAML directories. Please enable callbacks or remove SAML directories.";
            log.warn(errorMsg);
            if (errors == null) {
                errors = new ArrayList<>();
            }
            // only add to errors on GET, not POST
            if (request.getMethod().equals(HttpMethod.GET.name())) {
                errors.add(ErrorModel.builder().setStatus(HttpServletResponse.SC_OK).setMessage(errorMsg).build());
            }
        }

        model.put("accountStores", accountStores);

        if (isHtmlPreferred(request, response)) {
            model.put("forgotPasswordEnabled", forgotPasswordEnabled);
            model.put("forgotPasswordUri", forgotPasswordUri);
            model.put("verifyEnabled", verifyEnabled);
            model.put("verifyUri", verifyUri);
            model.put("registerEnabled", registerEnabledResolver.get(request, response));
            model.put("registerUri", registerUri);
            model.put("oauthStateToken", UUID.randomUUID().toString());
            String status = request.getParameter("status");
            if (status != null) {
                model.put("status", loginFormStatusResolver.getStatusMessage(request, status));
            }
        }
    }

    /**
     * Returns {@code true} if the specified list represents a SAML-based Account Provider, {@code false} otherwise.
     *
     * @param accountStores the list of account store models to check
     * @return {@code true} if the specified list represents a SAML-based Account Provider, {@code false} otherwise.
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/748">Issue 748</a>
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/771">Issue 771</a>
     * @since 1.0.0
     */
    private boolean containsSaml(List<AccountStoreModel> accountStores) {
        for (AccountStoreModel accountStore : accountStores) {
            if ("saml".equalsIgnoreCase(accountStore.getProvider().getProviderId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        return Collections.toList(errorModelFactory.toError(request, e));
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest req, HttpServletResponse resp, Form form) throws Exception {

        if (preLoginHandler != null) {
            if (!preLoginHandler.handle(req, resp, null)) {
                return null;
            }
        }

        // check to see if account already exists in request
        Account account = (Account) req.getAttribute(Account.class.getName());
        if (account != null) {
            AuthenticationResult authcResult = new TransientAuthenticationResult(account);
            authenticationResultSaver.set(req, resp, authcResult);
            eventPublisher.publish(new DefaultSuccessfulAuthenticationRequestEvent(req, resp, null, authcResult));
        } else {
            String usernameOrEmail = form.getFieldValue("login");
            String password = form.getFieldValue("password");

            req.login(usernameOrEmail, password);

            AccessTokenResult result = (AccessTokenResult) req.getAttribute(OAuthTokenResolver.REQUEST_ATTR_NAME);
            account = result.getAccount();
            saveResult(req, resp, result);
        }

        if (postLoginHandler != null) {
            if (!postLoginHandler.handle(req, resp, account)) {
                return null;
            }
        }

        if (isJsonPreferred(req, resp)) {
            return new DefaultViewModel(view, java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, java.util.Collections.<String>emptyList())));
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri(req)).setRedirect(true);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        authenticationResultSaver.set(request, response, result);
    }
}
