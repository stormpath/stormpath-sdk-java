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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @since 1.0.RC4
 */
public class ChangePasswordController extends FormController {
    private String forgotPasswordUri;
    private String loginUri;
    private String loginNextUri;
    private String errorUri;
    private boolean autoLogin;
    private ErrorModelFactory errorModelFactory;
    private AccountModelFactory accountModelFactory;
    private Saver<AuthenticationResult> authenticationResultSaver;

    public ChangePasswordController() {
        super();
    }

    public ChangePasswordController(Config config) {
        super(config.getChangePasswordControllerConfig(), config.getProducesMediaTypes());
        this.forgotPasswordUri = config.getForgotPasswordControllerConfig().getUri();
        this.loginUri = config.getLoginControllerConfig().getUri();
        this.loginNextUri = config.getLoginControllerConfig().getNextUri();
        this.errorUri = config.getChangePasswordControllerConfig().getErrorUri();
        this.autoLogin = config.getChangePasswordControllerConfig().isAutoLogin();
        this.accountModelFactory = new DefaultAccountModelFactory();
        this.errorModelFactory = new ChangePasswordErrorModelFactory(this.messageSource);
        this.authenticationResultSaver = config.getAuthenticationResultSaver();


        Assert.hasText(forgotPasswordUri, "forgotPasswordUri cannot be null or empty.");
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.hasText(loginNextUri, "loginNextUri cannot be null or empty.");
        Assert.hasText(errorUri, "errorUri cannot be null or empty.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (isJsonPreferred(request, response)) {
            Map<String, Object> model = new HashMap<String, Object>(1);
            if (sptoken == null) {
                model.put("status", HttpServletResponse.SC_BAD_REQUEST);
                model.put("message", i18n(request, "stormpath.web.changePassword.form.errors.no_token"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            else {
                try {
                    Application application = (Application) request.getAttribute(Application.class.getName());
                    application.verifyPasswordResetToken(sptoken);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    return null;
                }
                catch (Exception e) {
                    model = errorModelFactory.toError(request, e).toMap();
                    response.setStatus(errorModelFactory.toError(request, e).getStatus());
                }
            }
            return new DefaultViewModel("stormpathJsonView", model);
        }

        if (sptoken == null) {
            return new DefaultViewModel(forgotPasswordUri).setRedirect(true);
        }
        else {
            try {
                Application application = (Application) request.getAttribute(Application.class.getName());
                application.verifyPasswordResetToken(sptoken);
            }
            catch (ResourceException re) {
                return new DefaultViewModel(errorUri).setRedirect(true);
            }
            return super.doGet(request, response);
        }
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(3);

        String value = Strings.clean(fieldValueResolver.getValue(request, "sptoken"));

        if (value != null) {
            DefaultField field = new DefaultField();
            field.setName("sptoken");
            field.setType("hidden");
            field.setValue(value);
            fields.add(field);
        }

        String fieldName = "password";
        DefaultField field = new DefaultField();
        field.setName(fieldName);
        field.setLabel(i18n(request, "stormpath.web.changePassword.form.fields." + fieldName + ".label"));
        field.setPlaceholder(i18n(request, "stormpath.web.changePassword.form.fields." + fieldName + ".placeholder"));
        field.setRequired(true);
        field.setType("password");
        String val = fieldValueResolver.getValue(request, fieldName);
        field.setValue(retainPassword && val != null ? val : "");

        fields.add(field);

        return fields;
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        List<ErrorModel> errors = new ArrayList<ErrorModel>(1);
        String errorMsg = i18n(request, "stormpath.web.changePassword.form.errors.default");
        int status = 400;

        if (e instanceof IllegalArgumentException || e instanceof MismatchedPasswordException || e instanceof ValidationException) {
            errorMsg = e.getMessage();
        } else if (e instanceof ResourceException && ((ResourceException) e).getStatus() == 400) {
            //TODO: update this with a specific message that tells the exact password requirements.
            //This can only be done when this functionality is available via the Stormpath REST API
            //(currently being implemented in the Stormpath server-side REST API, not yet complete)
            errorMsg = i18n(request, "stormpath.web.changePassword.form.errors.strength");
        } else if (e instanceof ResourceException && ((ResourceException) e).getStatus() == 404) {
            String url = request.getContextPath() + forgotPasswordUri;
            errorMsg = i18n(request, "stormpath.web.changePassword.form.errors.invalid", url);
        }

        errors.add(ErrorModel.builder().setMessage(errorMsg).setStatus(status).build());
        return errors;
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) throws Exception {

        String password = form.getFieldValue("password");

        Application application = (Application) request.getAttribute(Application.class.getName());
        String sptoken = form.getFieldValue("sptoken");

        if (isJsonPreferred(request, response)) {
            Map<String, Object> model = new HashMap<String, Object>();
            try {
                Account account = application.resetPassword(sptoken, password);
                if (autoLogin){
                    final AuthenticationResult result = new TransientAuthenticationResult(account);
                    this.authenticationResultSaver.set(request, response, result);
                    model.put("account", accountModelFactory.toMap(account, Collections.EMPTY_LIST));                }
                else {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    return null;
                }
            }
            catch (Exception e) {
                model = errorModelFactory.toError(request, e).toMap();
                response.setStatus(errorModelFactory.toError(request, e).getStatus());
            }

            return new DefaultViewModel("stormpathJsonView", model);
        }

        String next;
        try {
            Account account = application.resetPassword(sptoken, password);
            if (autoLogin){
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(request, response, result);
                next = loginNextUri;
            }
            else{
                next = this.nextUri;
            }
        }
        catch (Exception e) {
            next = errorUri;
        }

        return new DefaultViewModel(next).setRedirect(true);
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {
        if (isJsonPreferred(request, response)) {
            String password = form.getFieldValue("password");
            if (password == null || password.isEmpty()) {
                String key = "stormpath.web.changePassword.form.fields.password.required";
                String msg = i18n(request, key);
                throw new ValidationException(msg);
            }
            String sptoken = form.getFieldValue("sptoken");
            if (sptoken == null || sptoken.isEmpty()) {
                String key = "stormpath.web.changePassword.form.errors.no_token";
                String msg = i18n(request, key);
                throw new ValidationException(msg);
            }
        }
        else {
            super.validate(request, response, form);
        }
    }
}
