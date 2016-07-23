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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.RegisterEnabledPredicate;
import com.stormpath.sdk.servlet.config.RegisterEnabledResolver;
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
    private String forgotLoginUri;
    private String verifyUri;
    private String registerUri;
    private String logoutUri;
    private boolean verifyEnabled = true;
    private boolean forgotPasswordEnabled = true;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private ErrorModelFactory errorModelFactory;
    private LoginFormStatusResolver loginFormStatusResolver;
    private AccountStoreModelFactory accountStoreModelFactory;
    private AccountModelFactory accountModelFactory;
    private WebHandler preLoginHandler;
    private WebHandler postLoginHandler;
    private boolean idSiteEnabled;
    private boolean callbackEnabled;
    private Resolver<Boolean> registerEnabledResolver =
        new RegisterEnabledResolver(true, ApplicationResolver.INSTANCE, new RegisterEnabledPredicate());

    public LoginController() {
        super();
    }

    public LoginController(Config config, ErrorModelFactory errorModelFactory) {
        super(config.getLoginControllerConfig(), config.getProducesMediaTypes());

        this.forgotLoginUri = config.getForgotPasswordControllerConfig().getUri();
        this.forgotPasswordEnabled = config.getForgotPasswordControllerConfig().isEnabled();
        this.verifyUri = config.getVerifyControllerConfig().getUri();
        this.registerUri = config.getRegisterControllerConfig().getUri();
        this.registerEnabledResolver = config.getRegisterEnabledResolver();
        this.logoutUri = config.getLogoutControllerConfig().getUri();
        this.verifyEnabled = config.getVerifyControllerConfig().isEnabled();
        this.authenticationResultSaver = config.getAuthenticationResultSaver();
        this.errorModelFactory = errorModelFactory;
        this.formFields = config.getLoginControllerConfig().getFormFields();

        this.preLoginHandler = config.getLoginPreHandler();
        this.postLoginHandler = config.getLoginPostHandler();

        this.loginFormStatusResolver = new DefaultLoginFormStatusResolver(this.messageSource, this.verifyUri);
        this.accountStoreModelFactory = new ExternalAccountStoreModelFactory();
        this.accountModelFactory = new DefaultAccountModelFactory();
        this.idSiteEnabled = config.isIdSiteEnabled();
        this.callbackEnabled = config.isCallbackEnabled();

        if (this.errorModelFactory == null) {
            this.errorModelFactory = new LoginErrorModelFactory(this.messageSource);
        }

        Assert.hasText(this.forgotLoginUri, "forgotLoginUri property cannot be null or empty.");
        Assert.hasText(this.verifyUri, "verifyUri property cannot be null or empty.");
        Assert.hasText(this.registerUri, "registerUri property cannot be null or empty.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
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
            model.put("forgotLoginUri", forgotLoginUri);
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
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/748">Isseue 748</a>
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/771">Isseue 771</a>
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

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        AccessTokenResult result = (AccessTokenResult) req.getAttribute(OAuthTokenResolver.REQUEST_ATTR_NAME);
        saveResult(req, resp, result);

        if (postLoginHandler != null) {
            if (!postLoginHandler.handle(req, resp, result.getAccount())) {
                return null;
            }
        }

        if (isJsonPreferred(req, resp)) {
            return new DefaultViewModel(view, java.util.Collections.singletonMap("account", accountModelFactory.toMap(result.getAccount(), java.util.Collections.<String>emptyList())));
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri(req)).setRedirect(true);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        authenticationResultSaver.set(request, response, result);
    }
}
