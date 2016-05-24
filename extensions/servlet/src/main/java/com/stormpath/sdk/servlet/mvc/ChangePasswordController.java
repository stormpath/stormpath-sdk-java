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
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class ChangePasswordController extends FormController {
    private String forgotPasswordUri;
    private String loginUri;

    public ChangePasswordController() {
        super();
    }

    public ChangePasswordController(Config config) {
        super(config.getChangePasswordControllerConfig());
        this.forgotPasswordUri = config.getForgotPasswordControllerConfig().getUri();
        this.loginUri = config.getLoginControllerConfig().getUri();

        Assert.hasText(forgotPasswordUri, "forgotPasswordUri cannot be null or empty.");
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            Map<String, String> queryParams = new HashMap<String, String>(1);
            queryParams.put("error", "sptokenInvalid");
            return new DefaultViewModel(loginUri, queryParams).setRedirect(true);
        }

        return super.doGet(request, response);
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
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
            field.setLabel("stormpath.web.changePassword.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.changePassword.form.fields." + fieldName + ".placeholder");
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
            String url = request.getContextPath() + forgotPasswordUri;
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

        return new DefaultViewModel(nextUri).setRedirect(true);
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {
        super.validate(request, response, form);

        //ensure fields match:
        String password = form.getFieldValue("password");
        String confirmPassword = form.getFieldValue("confirmPassword");

        if (!password.equals(confirmPassword)) {
            String key = "stormpath.web.changePassword.form.errors.mismatch";
            String msg = i18n(request, key);
            throw new MismatchedPasswordException(msg);
        }
    }
}
