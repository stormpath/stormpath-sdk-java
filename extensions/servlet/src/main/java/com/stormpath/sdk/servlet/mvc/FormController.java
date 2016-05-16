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
        super(configResolver.getNextUri(),
                configResolver.getView(),
                configResolver.getUri(),
                configResolver.getMessageSource(),
                configResolver.getLocaleResolver()
        );
        this.csrfTokenManager = configResolver.getCsrfTokenManager();
        this.fieldValueResolver = new ContentNegotiatingFieldValueResolver();

        Assert.notNull(this.csrfTokenManager, "csrfTokenManager cannot be null.");
        Assert.notNull(this.fieldValueResolver, "fieldValueResolver cannot be null.");
    }

    public CsrfTokenManager getCsrfTokenManager() {
        return csrfTokenManager;
    }

    public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
        Assert.notNull(this.csrfTokenManager, "csrfTokenManager cannot be null.");
        this.csrfTokenManager = csrfTokenManager;
    }

    public RequestFieldValueResolver getFieldValueResolver() {
        return fieldValueResolver;
    }

    public void setFieldValueResolver(RequestFieldValueResolver fieldValueResolver) {
        Assert.notNull(this.fieldValueResolver, "fieldValueResolver cannot be null.");
        this.fieldValueResolver = fieldValueResolver;
    }

    public List<Field> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<Field> formFields) {
        this.formFields = formFields;
    }

    private boolean isCsrfProtectionEnabled() {
        return csrfTokenManager != null && !(csrfTokenManager instanceof DisabledCsrfTokenManager);
    }

    protected Field createCsrfTokenField(String value) {
        return DefaultField.builder()
                .setName(getCsrfTokenManager().getTokenName())
                .setValue(value)
                .setType("hidden")
                .build();
    }

    protected void setCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        Assert.isInstanceOf(DefaultForm.class, form, "Form implementation class must equal or extend DefaultForm");

        String val = getFieldValueResolver().getValue(request, getCsrfTokenManager().getTokenName());
        if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            //This is a POST so we need to set the submitted CSRF token in the form
            form.addField(createCsrfTokenField(val));
        } else if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            //This is a GET so we need to generate a new CSRF token for the form
            form.addField(createCsrfTokenField(getCsrfTokenManager().createCsrfToken(request, response)));
        }
    }

    void validateCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        if (isCsrfProtectionEnabled()) {
            String csrfToken = form.getFieldValue(getCsrfTokenManager().getTokenName());
            Assert.isTrue(getCsrfTokenManager().isValidCsrfToken(request, response, csrfToken), "Invalid CSRF token");
            form.getField(getCsrfTokenManager().getTokenName()).setValue(getCsrfTokenManager().createCsrfToken(request, response));
        }
    }

    private void setForm(Map<String, Object> model, Form form) {
        model.put("form", form);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String view = getView();
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
        DefaultForm form = new DefaultForm.Builder().setFields(createFields(request, retainPassword)).build();

        if (isCsrfProtectionEnabled()) {
            setCsrfToken(request, response, form);
        }

        return form;
    }

    protected abstract List<Field> createFields(HttpServletRequest request, boolean retainPassword);

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
            return new DefaultViewModel("stormpathJsonView", errors.get(0).toMap());
        } else {
            Map<String, ?> model = createModel(request, response, form, errors);
            return new DefaultViewModel(getView(), model);
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

        //validate CSRF
        validateCsrfToken(request, response, form);

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired()) {
                String value = Strings.clean(field.getValue());
                if (value == null) {
                    //TODO: i18n
                    throw new IllegalArgumentException(Strings.capitalize(field.getName()) + " is required.");
                }
            }
        }
    }
}
