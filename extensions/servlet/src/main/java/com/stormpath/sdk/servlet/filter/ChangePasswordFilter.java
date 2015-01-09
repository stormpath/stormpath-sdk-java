/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChangePasswordFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordFilter.class);

    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";
    public static final String LOCALE_RESOLVER = "stormpath.web.locale.resolver";
    public static final String VIEW_TEMPLATE_PATH = "/WEB-INF/jsp/change.jsp";

    private CsrfTokenManager csrfTokenManager;
    private Resolver<Locale> localeResolver;
    private MessageSource messageSource;

    @Override
    protected void onInit() throws ServletException {
        this.csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);
        this.localeResolver = getConfig().getInstance(LOCALE_RESOLVER);
        this.messageSource = getConfig().getInstance(MESSAGE_SOURCE);
    }

    protected CsrfTokenManager getCsrfTokenManager() {
        return this.csrfTokenManager;
    }

    protected String getChangePasswordNextUrl() {
        return getConfig().getChangePasswordNextUrl();
    }

    protected String getLoginUrl() {
        return getConfig().getLoginUrl();
    }

    protected String i18n(HttpServletRequest request, String key) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale);
    }

    protected String i18n(HttpServletRequest request, String key, Object... args) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale, args);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String method = request.getMethod();

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            showForm(request, response);
        } else if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            handleFormSubmission(request, response);
        } else {
            //redirect to login: there is no reason for anyone to visit this page if wasn't access correctly:
            ServletUtils.issueRedirect(request, response, getLoginUrl(), null, true, true);
        }
    }

    protected void showForm(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        if (!Strings.hasText(request.getParameter("sptoken"))) {
            Map<String, String> queryParams = new HashMap<String, String>(1);
            queryParams.put("error", "sptokenInvalid");

            //not allowed to visit this page unless a sptoken is presented
            ServletUtils.issueRedirect(request, response, getLoginUrl(), queryParams, true, true);
            return;
        }

        Form form = createForm(request);
        ((DefaultForm) form).setCsrfToken(getCsrfTokenManager().createCsrfToken(request, response));
        setForm(request, form);
        String status = Strings.clean(request.getParameter("status"));
        if (status != null) {
            request.setAttribute("status", status);
        }
        request.getRequestDispatcher(VIEW_TEMPLATE_PATH).forward(request, response);
    }

    protected void setForm(HttpServletRequest request, Form form) {
        request.setAttribute("form", form);
    }

    @SuppressWarnings("unchecked")
    protected Form createForm(HttpServletRequest request) {

        DefaultForm form = new DefaultForm();

        String value = Strings.clean(request.getParameter("csrfToken"));
        form.setCsrfToken(value);

        value = Strings.clean(request.getParameter("next"));
        if (value != null) {
            form.setNext(value);
        }

        value = Strings.clean(request.getParameter("sptoken"));

        if (value != null) {
            DefaultField field = new DefaultField();
            field.setName("sptoken");
            field.setType("hidden");
            field.setValue(value);
            form.addField(field);
        }

        String[] fieldNames = new String[]{ "password", "confirmPassword" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setName(fieldName);
            field.setLabel("stormpath.web.change.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.change.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("password");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

            form.addField(field);
        }

        form.autofocus();

        return form;
    }

    protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request);

        try {
            changePassword(request, response, form);
        } catch (Exception e) {

            List<String> errors = new ArrayList<String>(1);

            if (e instanceof IllegalArgumentException || e instanceof MismatchedPasswordException) {
                errors.add(e.getMessage());
            } else if (e instanceof ResourceException && ((ResourceException) e).getStatus() == 400) {
                //TODO: update this with a specific message that tells the exact password requirements.
                //This can only be done when this functionality is available via the Stormpath REST API
                //(currently being implemented in the Stormpath server-side REST API, not yet complete)
                String key = "stormpath.web.change.form.errors.strength";
                String msg = i18n(request, key);
                errors.add(msg);

            } else if (e instanceof ResourceException && ((ResourceException) e).getCode() == 404) {
                String url = request.getContextPath() + getConfig().getForgotPasswordUrl();
                String key = "stormpath.web.change.form.errors.invalid";
                String msg = i18n(request, key, url);
                errors.add(msg);
            } else {
                log.warn("Potentially unexpected change password problem.", e);
                String key = "stormpath.web.change.form.errors.default";
                String msg = i18n(request, key);
                errors.add(msg);
            }
            request.setAttribute("errors", errors);

            //remove password values (not safe to transfer the values over the wire and represent them in the DOM):
            //do not retain submitted password (not safe to have in the DOM text):
            ((DefaultField) form.getField("password")).setValue("");
            ((DefaultField) form.getField("confirmPassword")).setValue("");

            setForm(request, form);

            //ensure new csrf token is used:
            ((DefaultForm) form).setCsrfToken(getCsrfTokenManager().createCsrfToken(request, response));

            request.getRequestDispatcher(VIEW_TEMPLATE_PATH).forward(request, response);
        }
    }

    protected void changePassword(HttpServletRequest request, HttpServletResponse response, Form form)
        throws ServletException, IOException {

        validate(request, response, form);

        String password = form.getFieldValue("password");

        Application application = (Application) request.getAttribute(Application.class.getName());
        String sptoken = form.getFieldValue("sptoken");
        application.resetPassword(sptoken, password);

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getChangePasswordNextUrl();
        }

        ServletUtils.issueRedirect(request, response, next, null, true, true);
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form)
        throws ServletException, IOException {

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

    protected void validateCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) {
        String csrfToken = form.getCsrfToken();
        Assert.isTrue(getCsrfTokenManager().isValidCsrfToken(request, response, csrfToken), "Invalid CSRF token");
    }

    protected static class MismatchedPasswordException extends RuntimeException {

        public MismatchedPasswordException(String msg) {
            super(msg);
        }
    }
}


