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
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.servlet.mvc.View.STORMPATH_JSON_VIEW_NAME;

import static com.stormpath.sdk.servlet.mvc.JacksonFieldValueResolver.MARSHALLED_OBJECT;

/**
 * @since 1.0.RC4
 */
public abstract class FormController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(FormController.class);

    public final static String SPRING_SECURITY_AUTHENTICATION_FAILED_KEY = "SPRING_SECURITY_AUTHENTICATION_FAILED_MESSAGE";

    private CsrfTokenManager csrfTokenManager;
    private RequestFieldValueResolver fieldValueResolver;
    private List<Field> formFields;

    public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }

    public void setFieldValueResolver(RequestFieldValueResolver fieldValueResolver) {
        this.fieldValueResolver = fieldValueResolver;
    }

    protected RequestFieldValueResolver getFieldValueResolver() {
        return this.fieldValueResolver;
    }

    public void setFormFields(List<Field> formFields) {
        this.formFields = formFields;
    }

    protected CsrfTokenManager getCsrfTokenManager() {
        return csrfTokenManager;
    }

    @Override
    public void init() throws Exception {
        super.init();
        //Assert.notEmpty(formFields, "formFields cannot be null or empty."); some forms do this manually
        Assert.notNull(csrfTokenManager, "csrfTokenManager cannot be null.");
        Assert.notNull(fieldValueResolver, "fieldValueResolver cannot be null.");
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
        if (!isJsonPreferred(request, response)) {
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
        if (isCsrfProtectionEnabled() && !isJsonPreferred(request, response)) {
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

    @SuppressWarnings("unchecked")
    protected Map<String,?> createModel(HttpServletRequest request, HttpServletResponse response) {
        List<ErrorModel> errors = null;
        // session null check fixes https://github.com/stormpath/stormpath-sdk-java/issues/908
        if (request.getParameter("error") != null && request.getSession(false) != null) {
            //The login page is being re-rendered after an unsuccessful authentication attempt from Spring Security
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/648
            //See StormpathAuthenticationFailureHandler
            errors = new ArrayList<>();
            ErrorModel error =
                (ErrorModel) request.getSession(false).getAttribute(SPRING_SECURITY_AUTHENTICATION_FAILED_KEY);
            if (error != null) {
                errors.add(error);
            }
        }
        return createModel(request, response, null, errors);
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
        return createForm(request, response, false);
    }

    protected Form createForm(HttpServletRequest request, HttpServletResponse response, boolean retainPassword) {
        DefaultForm form = DefaultForm.builder().setFields(createFields(request, retainPassword)).build();

        if (isCsrfProtectionEnabled()) {
            setCsrfToken(request, response, form);
        }

        return form;
    }

    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {
        List<Field> fields = new ArrayList<>();

        for (Field templateField : formFields) {
            Field clone = templateField.copy();

            if (clone.isEnabled()) {
                String val = fieldValueResolver.getValue(request, clone.getName());
                if (retainPassword && "password".equals(clone.getName())) {
                    clone.setValue(val);
                } else {
                    clone.setValue(val);
                }
                // #645: Allow unresolved i18n keys to pass through for labels and placeholders
                ((DefaultField) clone).setLabel(i18n(request, clone.getLabel(), clone.getLabel()));
                ((DefaultField) clone).setPlaceholder(i18n(request, clone.getPlaceholder(), clone.getPlaceholder()));

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

        if (isJsonPreferred(request, response)) {
            //TODO according to the spec if multiple errors only the most relevant should be return in case of JSON response, we don't have way to know that for now
            response.setStatus(errors.get(0).getStatus());
            return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, errors.get(0).toMap());
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

        // check for request body and no parameters
        if (request.getParameterMap().size() == 0 && request.getContentLength() > 0) {
            // Read from request to see if social information exists
            ProviderAccountRequest accountRequest = getAccountProviderRequest(request);
            if (accountRequest != null) {
                ProviderAccountResult result = getApplication(request).getAccount(accountRequest);
                Account account = result.getAccount();
                if (account.getStatus().equals(AccountStatus.ENABLED)) {
                    request.setAttribute(Account.class.getName(), account);
                    return;
                }
            }
        }

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired() || field.isEnabled()) {
                String value = form.getFieldValue(field.getName());
                if (value == null) {
                    String key = "stormpath.web." + getControllerKey() + ".form.fields." + field.getName() + ".required";
                    String msg = i18n(request, key);
                    throw new ValidationException(msg);
                }
            }
        }

        //If JSON ensure we are not posting undeclared fields according to the spec
        if (isJsonPreferred(request, response)) {
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

    @SuppressWarnings("unchecked")
    private ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        Map<String, Object> map = (Map<String, Object>) request.getAttribute(MARSHALLED_OBJECT);
        Map<String, String> providerData = (Map<String, String>) map.get("providerData");

        if (providerData != null) {
            String providerId = providerData.get("providerId");
            ProviderAccountRequest accountRequest = null;
            switch (providerId) {
                case "facebook": {
                    String accessToken = providerData.get("accessToken");
                    accountRequest = Providers.FACEBOOK.account().setAccessToken(accessToken).build();
                    break;
                }
                case "github": {
                    String code = providerData.get("code");
                    accountRequest = Providers.GITHUB.account().setAccessToken(exchangeGithubCodeForAccessToken(code, request)).build();
                    break;
                }
                case "google": {
                    String code = providerData.get("code");
                    accountRequest = Providers.GOOGLE.account().setCode(code).build();
                    break;
                }
                case "linkedin": {
                    String code = providerData.get("code");
                    accountRequest = Providers.LINKEDIN.account().setCode(code).build();
                    break;
                }
                default: {
                    log.error("No provider configured for " + providerId);
                }
            }

            return accountRequest;
        }

        log.warn("Provider data not found in request.");
        return null;
    }
}
