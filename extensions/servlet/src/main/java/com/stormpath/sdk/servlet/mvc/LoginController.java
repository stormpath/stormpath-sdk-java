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
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultAccountStoreModelFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
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

    public LoginController(Config config, ErrorModelFactory errorModelFactory) {
        super(config.getLoginControllerConfig());

        this.forgotLoginUri = config.getForgotPasswordControllerConfig().getUri();
        this.verifyUri = config.getVerifyControllerConfig().getUri();
        this.registerUri = config.getRegisterControllerConfig().getUri();
        this.logoutUri = config.getLogoutControllerConfig().getUri();
        this.verifyEnabled = config.getVerifyControllerConfig().isEnabled();
        this.authenticationResultSaver = config.getAuthenticationResultSaver();
        this.errorModelFactory = errorModelFactory;
        this.formFields = config.getLoginControllerConfig().getFormFields();

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
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    public String getForgotLoginUri() {
        return forgotLoginUri;
    }

    public void setForgotLoginUri(String forgotLoginUri) {
        this.forgotLoginUri = forgotLoginUri;
    }

    /* @since 1.0.RC8.3 */
    public String getVerifyUri() {
        return verifyUri;
    }

    /* @since 1.0.RC8.3 */
    public void setVerifyUri(String verifyUri) {
        this.verifyUri = verifyUri;
    }

    public String getRegisterUri() {
        return registerUri;
    }

    public void setRegisterUri(String registerUri) {
        Assert.hasText(registerUri, "registerUri property cannot be null or empty.");
        this.registerUri = registerUri;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    /* @since 1.0.RC8.3 */
    public Boolean isVerifyEnabled() {
        return verifyEnabled;
    }

    public void setVerifyEnabled(Boolean verifyEnabled) {
        this.verifyEnabled = verifyEnabled;
    }

    public AccountStoreModelFactory getAccountStoreModelFactory() {
        return accountStoreModelFactory;
    }

    public void setAccountStoreModelFactory(AccountStoreModelFactory accountStoreModelFactory) {
        this.accountStoreModelFactory = accountStoreModelFactory;
    }

    public ErrorModelFactory getErrorModelFactory() {
        return errorModelFactory;
    }

    public void setErrorModelFactory(ErrorModelFactory errorModelFactory) {
        this.errorModelFactory = errorModelFactory;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return this.authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public void setLoginFormStatusResolver(LoginFormStatusResolver loginFormStatusResolver) {
        this.loginFormStatusResolver = loginFormStatusResolver;
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("accountStores", getAccountStoreModelFactory().getAccountStores(request));

        if (UserAgents.get(request).isHtmlPreferred()) {
            model.put("forgotLoginUri", getForgotLoginUri());
            model.put("verifyUri", getVerifyUri());
            model.put("verifyEnabled", isVerifyEnabled());
            model.put("registerUri", getRegisterUri());
            model.put("oauthStateToken", UUID.randomUUID().toString());
            String status = request.getParameter("status");
            if (status != null) {
                model.put("status", loginFormStatusResolver.getStatusMessage(request, status));
            }
        }
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {
        List<Field> fields = new ArrayList<Field>();

        for (Field templateField : formFields) {
            Field clone = templateField.copy();

            String val = getFieldValueResolver().getValue(request, clone.getName());

            if (clone.getName() == "password" && retainPassword) {
                clone.setValue(val);
            } else {
                clone.setValue(val);
            }

            fields.add(clone);
        }

        return fields;
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
        saveResult(req, resp, result);

        if (UserAgents.get(req).isJsonPreferred()) {
            return new DefaultViewModel(getView(), java.util.Collections.singletonMap("account", accountModelFactory.toMap(account)));
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri()).setRedirect(true);
    }

    protected Account getAccount(HttpServletRequest req) {
        return AccountResolver.INSTANCE.getRequiredAccount(req);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }
}
