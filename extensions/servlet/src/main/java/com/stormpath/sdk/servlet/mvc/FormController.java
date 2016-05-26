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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class FormController extends AbstractController {

    private CsrfTokenManager csrfTokenManager;
    private String view;
    private String uri;

    private final static String SPRING_SECURITY_AUTHENTICATION_FAILED_KEY = "SPRING_SECURITY_AUTHENTICATION_FAILED_MESSAGE";

    public void init() {
        Assert.hasText(this.view, "view cannot be null or empty.");
        Assert.hasText(this.uri, "uri cannot be null or empty.");
        Assert.notNull(this.csrfTokenManager, "csrfTokenManager cannot be null.");
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public CsrfTokenManager getCsrfTokenManager() {
        return csrfTokenManager;
    }

    public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }

    protected boolean isCsrfProtectionEnabled() {
        return csrfTokenManager != null && !(csrfTokenManager instanceof DisabledCsrfTokenManager);
    }

    protected void setNewCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        Assert.isInstanceOf(DefaultForm.class, form, "Form implementation class must equal or extend DefaultForm");
        ((DefaultForm)form).setCsrfToken(getCsrfTokenManager().createCsrfToken(request, response));
    }

    protected void validateCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) throws IllegalArgumentException {
        if (isCsrfProtectionEnabled()) {
            String csrfToken = form.getCsrfToken();
            Assert.isTrue(getCsrfTokenManager().isValidCsrfToken(request, response, csrfToken), "Invalid CSRF token");
        }
    }

    protected void setForm(Map<String,Object> model, Form form) {
        model.put("form", form);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String view = getView();
        Map<String,?> model = createModel(request, response);
        return new DefaultViewModel(view, model);
    }

    @SuppressWarnings("unchecked")
    protected Map<String,?> createModel(HttpServletRequest request, HttpServletResponse response) {
        List<String> errors = null;
        if (request.getParameter("error") != null) {
            //The login page is being re-rendered after an unsuccessful authentication attempt from Spring Security
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/648
            //See StormpathAuthenticationFailureHandler
            errors = new ArrayList<>();
            List<String> errorList = (List<String>) request.getSession(false).getAttribute(SPRING_SECURITY_AUTHENTICATION_FAILED_KEY);
            errors.addAll(errorList);
        }
        return createModel(request, response, null, errors);
    }

    protected Map<String,?> createModel(HttpServletRequest request, HttpServletResponse response,
                                        Form form, List<String> errors) {

        Map<String,Object> model = newModel();

        model.put("social", false); //until social login is enabled

        if (!Collections.isEmpty(errors)) {
            model.put("errors", errors);
        }

        if (form == null) {
            form = createForm(request);
        }

        if (isCsrfProtectionEnabled()) {
            setNewCsrfToken(request, response, form);
        }
        setForm(model, form);

        String status = Strings.clean(request.getParameter("status"));
        if (status != null) {
            model.put("status", status);
        }

        appendModel(request, response, form, errors, model);

        return model;
    }

    protected Form createForm(HttpServletRequest request) {
        return createForm(request, false);
    }

    protected Form createForm(HttpServletRequest request, boolean retainPassword) {

        DefaultForm form = new DefaultForm();

        form.setAction(getUri());

        if (isCsrfProtectionEnabled()) {
            String csrfTokenName = csrfTokenManager.getTokenName();
            form.setCsrfTokenName(csrfTokenName);
            String value = Strings.clean(request.getParameter(csrfTokenName));
            form.setCsrfToken(value);
        }

        String value = Strings.clean(request.getParameter("next"));
        if (value != null) {
            form.setNext(value);
        }

        List<Field> fields = createFields(request, retainPassword);
        for(Field field : fields) {
            form.addField(field);
        }

        form.autofocus();

        return form;
    }

    protected abstract List<Field> createFields(HttpServletRequest request, boolean retainPassword);


    protected void appendModel(HttpServletRequest request, HttpServletResponse response,
                               Form form, List<String> errors, Map<String,Object> model) {
    }

    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Form form = createForm(request, true);

        try {
            validate(request, response, form);
            return onValidSubmit(request, response, form);
        } catch (Exception e) {
            return onErrorSubmit(request, response, form, e);
        }
    }

    protected ViewModel onErrorSubmit(HttpServletRequest request, HttpServletResponse response,
                                      Form form, Exception e) {

        List<String> errors = toErrors(request, form, e);

        //do not retain submitted password (not safe to have in the DOM text):
        Field field = form.getField("password");
        if (field != null) {
            ((DefaultField)field).setValue("");
        }
        field = form.getField("confirmPassword");
        if (field != null) {
            ((DefaultField)field).setValue("");
        }

        String view = getView();
        Map<String,?> model = createModel(request, response, form, errors);
        return new DefaultViewModel(view, model);
    }

    protected abstract List<String> toErrors(HttpServletRequest request, Form form, Exception e);

    protected abstract ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form)
        throws Exception;

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
