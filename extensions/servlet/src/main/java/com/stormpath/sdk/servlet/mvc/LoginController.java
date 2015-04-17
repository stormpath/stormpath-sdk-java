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
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class LoginController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private String nextUri;
    private String forgotLoginUri;
    private String registerUri;
    private String logoutUri;
    private Saver<AuthenticationResult> authenticationResultSaver;

    public void init() {
        super.init();
        Assert.hasText(this.nextUri, "nextUri property cannot be null or empty.");
        Assert.hasText(this.forgotLoginUri, "forgotLoginUri property cannot be null or empty.");
        Assert.hasText(this.registerUri, "registerUri property cannot be null or empty.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public String getForgotLoginUri() {
        return forgotLoginUri;
    }

    public void setForgotLoginUri(String forgotLoginUri) {
        this.forgotLoginUri = forgotLoginUri;
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

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return this.authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        this.authenticationResultSaver = authenticationResultSaver;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (AccountResolver.INSTANCE.hasAccount(request)) {
            //already logged in:
            return new DefaultViewModel(getNextUri()).setRedirect(true);
        }
        //otherwise should be able to visit:
        return super.doGet(request, response);
    }

    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //logged in users should not be trying to login again - could be malicious activity - log out the user:
        //TODO: should do this logout immediately and not redirect
        if (AccountResolver.INSTANCE.hasAccount(request)) {
            return new DefaultViewModel(getLogoutUri()).setRedirect(true);
        }
        return super.doPost(request, response);
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<String> errors,
                               Map<String, Object> model) {
        model.put("forgotLoginUri", getForgotLoginUri());
        model.put("registerUri", getRegisterUri());
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(2);

        String[] fieldNames = new String[]{ "login", "password" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setLabel("stormpath.web.login.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.login.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("text");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

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
        log.debug("Unable to login user.", e);
        List<String> errors = new ArrayList<String>(1);
        errors.add("Invalid username or password.");
        return errors;
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest req, HttpServletResponse resp, Form form) throws Exception {

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        final Account account = getAccount(req);

        //simulate a result for the benefit of the 'saveResult' method signature:
        AuthenticationResult result = new TransientAuthenticationResult(account);
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
