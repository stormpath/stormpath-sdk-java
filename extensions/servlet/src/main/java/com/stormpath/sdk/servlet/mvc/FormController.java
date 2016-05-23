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

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class FormController extends AbstractController {

    protected CsrfTokenManager csrfTokenManager;
    protected RequestFieldValueResolver fieldValueResolver;
    protected List<Field> formFields;

    public FormController() {

    }

    public FormController(ControllerConfigResolver configResolver) {
        super(configResolver);
        this.csrfTokenManager = configResolver.getCsrfTokenManager();
        this.formFields = configResolver.getFormFields();

        this.fieldValueResolver = new ContentNegotiatingFieldValueResolver();

        Assert.notNull(this.csrfTokenManager, "csrfTokenManager cannot be null.");
        Assert.notNull(this.fieldValueResolver, "fieldValueResolver cannot be null.");
    }

    private boolean isCsrfProtectionEnabled() {
        return csrfTokenManager != null && !(csrfTokenManager instanceof DisabledCsrfTokenManager);
    }

    protected Field createCsrfTokenField(String value) {
        return DefaultField.builder()
                .setName(csrfTokenManager.getTokenName())
                .setValue(value)
                .setType("hidden")
                .build();
    }

    protected void setCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        if (!isJsonPreferred(request)) {
            Assert.isInstanceOf(DefaultForm.class, form, "Form implementation class must equal or extend DefaultForm");

            String val = fieldValueResolver.getValue(request, csrfTokenManager.getTokenName());
            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                //This is a POST so we need to set the submitted CSRF token in the form
                form.addField(createCsrfTokenField(val));
            } else if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
                //This is a GET so we need to generate a new CSRF token for the form
                form.addField(createCsrfTokenField(csrfTokenManager.createCsrfToken(request, response)));
            }
        }
    }

    void validateCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        if (isCsrfProtectionEnabled() && !isJsonPreferred(request)) {
            String csrfToken = form.getFieldValue(csrfTokenManager.getTokenName());
            Assert.isTrue(csrfTokenManager.isValidCsrfToken(request, response, csrfToken), "Invalid CSRF token");
            form.getField(csrfTokenManager.getTokenName()).setValue(csrfTokenManager.createCsrfToken(request, response));
        }
    }

    private void setForm(Map<String, Object> model, Form form) {
        model.put("form", form);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, ?> model = createModel(request, response);
        return new DefaultViewModel(view, model);
    }

    protected Map<String, ?> createModel(HttpServletRequest request, HttpServletResponse response) {
        return createModel(request, response, null, null);
    }

    protected Map<String, ?> createModel(HttpServletRequest request, HttpServletResponse response,
                                         Form form, List<ErrorModel> errors) {

        Map<String, Object> model = newModel();

        if (!Collections.isEmpty(errors)) {
            model.put("errors", errors);
        }

        if (form == null) {
            form = createForm(request, response);
        }

        setForm(model, form);

        String status = Strings.clean(request.getParameter("status"));
        if (status != null) {
            model.put("status", status);
        }

        appendModel(request, response, form, errors, model);

        return model;
    }

    protected Form createForm(HttpServletRequest request, HttpServletResponse response) {
        Form form = createForm(request, response, false);

        return form;
    }

    protected Form createForm(HttpServletRequest request, HttpServletResponse response, boolean retainPassword) {
        DefaultForm form = DefaultForm.builder().setFields(createFields(request, retainPassword)).build();

        if (isCsrfProtectionEnabled()) {
            setCsrfToken(request, response, form);
        }

        return form;
    }

    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {
        List<Field> fields = new ArrayList<Field>();

        for (Field templateField : formFields) {
            Field clone = templateField.copy();

            if (clone.isEnabled()) {
                String val = fieldValueResolver.getValue(request, clone.getName());
                if (clone.getName() == "password" && retainPassword) {
                    clone.setValue(val);
                } else {
                    clone.setValue(val);
                }
                fields.add(clone);
            }
        }

        return fields;
    }

    protected void appendModel(HttpServletRequest request, HttpServletResponse response,
                               Form form, List<ErrorModel> errors, Map<String, Object> model) {
    }

    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Form form = createForm(request, response, true);

        try {
            validate(request, response, form);
            return onValidSubmit(request, response, form);
        } catch (Exception e) {
            return onErrorSubmit(request, response, form, e);
        }
    }

    protected ViewModel onErrorSubmit(HttpServletRequest request, HttpServletResponse response,
                                      Form form, Exception e) {
        sanitizeForm(form);

        List<ErrorModel> errors = toErrors(request, form, e);

        if (UserAgents.get(request).isJsonPreferred()) {
            //TODO according to the spec if multiple errors only the most relevant should be return in case of JSON response, we don't have way to know that for now
            response.setStatus(errors.get(0).getStatus());
            return new DefaultViewModel("stormpathJsonView", errors.get(0).toMap());
        } else {
            Map<String, ?> model = createModel(request, response, form, errors);
            return new DefaultViewModel(view, model);
        }
    }

    private void sanitizeForm(Form form) {
        //do not retain submitted password (not safe to have in the DOM text):
        Field field = form.getField("password");
        if (field != null) {
            field.setValue("");
        }
        field = form.getField("confirmPassword");
        if (field != null) {
            field.setValue("");
        }
    }

    protected abstract List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e);

    protected abstract ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) throws Exception;

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {

        validateCsrfToken(request, response, form);

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired() || field.isEnabled()) {
                String value = form.getFieldValue(field.getName());
                if (value == null) {
                    String key = "stormpath.web." + controllerKey + ".form.fields." + field.getName() + ".required";
                    String msg = i18n(request, key);
                    throw new ValidationException(msg);
                }
            }
        }

        //If JSON ensure we are not posting undeclared fields according to the spec
        if (isJsonPreferred(request)) {
            Map<String, Object> postedJsonFields = fieldValueResolver.getAllFields(request);

            for (String fieldName : postedJsonFields.keySet()) {
                if (form.getField(fieldName) == null && !"customData".equals(fieldName)) {
                    String key = "stormpath.web.form.fields.unknown";
                    String msg = i18n(request, key, fieldName);
                    throw new ValidationException(msg);
                }
            }
        }
    }
}
