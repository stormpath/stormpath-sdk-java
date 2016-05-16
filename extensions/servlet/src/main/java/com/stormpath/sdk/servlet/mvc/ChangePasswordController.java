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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @since 1.0.RC4
 */
public class ChangePasswordController extends FormController {
    private String forgotPasswordUri;
    private String loginUri;

    public ChangePasswordController(ControllerConfigResolver controllerConfigResolver,
                                    String forgotPasswordUri,
                                    String loginUri) {
        super(controllerConfigResolver);
        this.forgotPasswordUri = forgotPasswordUri;
        this.loginUri = loginUri;

        Assert.hasText(forgotPasswordUri, "forgotPasswordUri cannot be null or empty.");
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.notNull(localeResolver, "localeResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    public String getForgotPasswordUri() {
        return forgotPasswordUri;
    }

    public void setForgotPasswordUri(String forgotPasswordUri) {
        this.forgotPasswordUri = forgotPasswordUri;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public Resolver<Locale> getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(Resolver<Locale> localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            Map<String, String> queryParams = new HashMap<String, String>(1);
            queryParams.put("error", "sptokenInvalid");
            return new DefaultViewModel(getLoginUri(), queryParams).setRedirect(true);
        }

        return super.doGet(request, response);
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", getLoginUri());
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(3);

        String value = Strings.clean(request.getParameter("sptoken"));

        if (value != null) {
            DefaultField field = new DefaultField();
            field.setName("sptoken");
            field.setType("hidden");
            field.setValue(value);
            fields.add(field);
        }

        String[] fieldNames = new String[]{"password", "confirmPassword"};

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setName(fieldName);
            field.setLabel("stormpath.web.change.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.change.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("password");
            String val = fieldValueResolver.getValue(request, fieldName);
            field.setValue(retainPassword && val != null ? val : "");

            fields.add(field);
        }

        return fields;
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        List<ErrorModel> errors = new ArrayList<ErrorModel>(1);
        String errorMsg = i18n(request, "stormpath.web.change.form.errors.default");
        int status = 400;

        if (e instanceof IllegalArgumentException || e instanceof MismatchedPasswordException) {
            errorMsg = e.getMessage();
        } else if (e instanceof ResourceException && ((ResourceException) e).getStatus() == 400) {
            //TODO: update this with a specific message that tells the exact password requirements.
            //This can only be done when this functionality is available via the Stormpath REST API
            //(currently being implemented in the Stormpath server-side REST API, not yet complete)
            errorMsg = i18n(request, "stormpath.web.change.form.errors.strength");
        } else if (e instanceof ResourceException && ((ResourceException) e).getStatus() == 404) {
            String url = request.getContextPath() + getForgotPasswordUri();
            errorMsg = i18n(request, "stormpath.web.change.form.errors.invalid", url);
        }

        errors.add(ErrorModel.builder().setMessage(errorMsg).setStatus(status).build());
        return errors;
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) throws Exception {

        String password = form.getFieldValue("password");

        Application application = (Application) request.getAttribute(Application.class.getName());
        String sptoken = form.getFieldValue("sptoken");
        application.resetPassword(sptoken, password);

        return new DefaultViewModel(getNextUri()).setRedirect(true);
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {

        //validate CSRF
        validateCsrfToken(request, response, form);

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired()) {
                String value = Strings.clean(field.getValue());
                if (value == null) {
                    String key = "stormpath.web.change.form.fields." + field.getName() + ".required";
                    String msg = i18n(request, key);
                    throw new IllegalArgumentException(msg);
                }
            }
        }

        //ensure fields match:
        String password = form.getFieldValue("password");
        String confirmPassword = form.getFieldValue("confirmPassword");

        if (!password.equals(confirmPassword)) {
            String key = "stormpath.web.change.form.errors.mismatch";
            String msg = i18n(request, key);
            throw new MismatchedPasswordException(msg);
        }
    }

    protected static class MismatchedPasswordException extends RuntimeException {
        public MismatchedPasswordException(String msg) {
            super(msg);
        }
    }
}
