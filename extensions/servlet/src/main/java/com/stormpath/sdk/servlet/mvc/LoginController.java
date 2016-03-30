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
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultAccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultProviderModelFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ErrorModelFactory errorModelFactory = new LoginErrorModelFactory();
    private AccountStoreModelFactory accountStoreModelFactory = new DefaultAccountStoreModelFactory();
    private AccountStoreModelFactory providerModelFactory = new DefaultProviderModelFactory();
    private boolean samlEnabled = false;
    private boolean socialEnabled = false;

    public void init() {
        super.init();
        Assert.hasText(this.nextUri, "nextUri property cannot be null or empty.");
        Assert.hasText(this.forgotLoginUri, "forgotLoginUri property cannot be null or empty.");
        Assert.hasText(this.verifyUri, "verifyUri property cannot be null or empty.");
        Assert.hasText(this.registerUri, "registerUri property cannot be null or empty.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.verifyEnabled, "verifyEnabled property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
        Assert.notNull(this.accountStoreModelFactory, "accountStoreModelFactory cannot be null.");
        Assert.notNull(this.errorModelFactory, "errorModelFactory cannot be null.");
    }

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return true;
    }

    public boolean isSamlEnabled() {
        return this.samlEnabled;
    }

    public void setSamlEnabled(boolean samlEnabled) {
        this.samlEnabled = samlEnabled;
    }

    public boolean isSocialEnabled() {
        return this.socialEnabled;
    }

    public void setSocialEnabled(boolean socialEnabled) {
        this.socialEnabled = socialEnabled;
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

    public AccountStoreModelFactory getAccountStoreModelFactory() {
        return accountStoreModelFactory;
    }

    public void setAccountStoreModelFactory(AccountStoreModelFactory accountStoreModelFactory) {
        this.accountStoreModelFactory = accountStoreModelFactory;
    }

    public AccountStoreModelFactory getProviderModelFactory() {
        return providerModelFactory;
    }

    public void setProviderModelFactory(AccountStoreModelFactory providerModelFactory) {
        this.providerModelFactory = providerModelFactory;
    }

    /* @since 1.0.RC8.3 */
    public Boolean isVerifyEnabled() {
        return verifyEnabled;
    }

    public void setVerifyEnabled(Boolean verifyEnabled) {
        this.verifyEnabled = verifyEnabled;
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

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<String> errors,
                               Map<String, Object> model) {
        if (Collections.isEmpty(errors)) {
            //allow factory to populate if necessary:
            errors = errorModelFactory.toErrors(request, form, null);
            if (!Collections.isEmpty(errors)) {
                model.put("errors", errors);
            }
        }
        if (isSamlEnabled()) {
            model.put("providers", getProviderModelFactory().getAccountStores(request));
        }
        if (isSocialEnabled()) {
            model.put("accountStores", getAccountStoreModelFactory().getAccountStores(request));
        }
        model.put("forgotLoginUri", getForgotLoginUri());
        model.put("verifyUri", getVerifyUri());
        model.put("verifyEnabled", isVerifyEnabled());
        model.put("registerUri", getRegisterUri());
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(2);

        String[] fieldNames = new String[]{"login", "password"};

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setLabel("stormpath.web.login.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.login.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("text");
//            String param = request.getParameter(fieldName);
//            field.setValue(param != null ? param : "");

            String val = getFieldValueResolver().getValue(request, fieldName);
            field.setValue(val != null ? val : "");

            if ("password".equals(fieldName)) {
                field.setType("password");
                if (!retainPassword) {
                    field.setValue("");
                }
            }

            fields.add(field);
        }

        return fields;
    }

    @Override
    protected List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        //return errorModelFactory.toErrors(request, form, e);
        return getErrorModelFactory().toErrors(request, form, e);
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

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        return new DefaultViewModel(next).setRedirect(true);
    }

    protected Account getAccount(HttpServletRequest req) {
        return AccountResolver.INSTANCE.getRequiredAccount(req);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }
}
