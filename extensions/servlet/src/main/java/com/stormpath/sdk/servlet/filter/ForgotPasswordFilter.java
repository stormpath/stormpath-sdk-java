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
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordFilter.class);

    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";
    public static final String ACCOUNT_STORE_RESOLVER = "stormpath.web.accountStoreResolver";
    public static final String VIEW_TEMPLATE_PATH = "/WEB-INF/jsp/forgot.jsp";

    private AccountStoreResolver accountStoreResolver;
    private CsrfTokenManager csrfTokenManager;

    @Override
    protected void onInit() throws ServletException {
        this.csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);
        this.accountStoreResolver = getConfig().getInstance(ACCOUNT_STORE_RESOLVER);
    }

    protected CsrfTokenManager getCsrfTokenManager() {
        return this.csrfTokenManager;
    }

    protected AccountStoreResolver getAccountStoreResolver() {
        return accountStoreResolver;
    }

    protected String getForgotPasswordUrl() {
        return getConfig().getForgotPasswordUrl();
    }

    protected String getForgotPasswordNextUrl() {
        return getConfig().getForgotPasswordNextUrl();
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
            ServletUtils.issueRedirect(request, response, getForgotPasswordUrl(), null, true, true);
        }
    }

    protected void showForm(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //addConfigProperties(request);
        Form form = createForm(request);
        ((DefaultForm)form).setCsrfToken(getCsrfTokenManager().createCsrfToken(request, response));
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

        String[] fieldNames = new String[]{ "email" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setRequired(true);
            field.setType("text");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

            String label;

            if ("email".equals(fieldName)) {
                label = "Email";
            } else {
                //unrecognized property
                continue;
            }

            field.setLabel(label).setPlaceholder(label);

            form.addField(field);
        }

        form.autofocus();

        return form;
    }

    protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request);

        try {
            sendForgotPasswordEmail(request, response, form);
        } catch (Exception e) {
            log.debug("Unable to send reset password email.", e);

            List<String> errors = new ArrayList<String>(1);
            errors.add("Invalid email address.");
            request.setAttribute("errors", errors);

            setForm(request, form);

            //ensure new csrf token is used:
            ((DefaultForm)form).setCsrfToken(getCsrfTokenManager().createCsrfToken(request, response));

            request.getRequestDispatcher(VIEW_TEMPLATE_PATH).forward(request, response);
        }
    }

    protected void sendForgotPasswordEmail(HttpServletRequest request, HttpServletResponse response, Form form) throws ServletException, IOException {

        validate(request, response, form);

        Application application = (Application)request.getAttribute(Application.class.getName());

        String email = form.getFieldValue("email");

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            setForm(request, form);
            AccountStore accountStore = getAccountStoreResolver().getAccountStore(request, response);
            if (accountStore != null) {
                //application.sendPasswordResetEmail(email, accountStore);
            } else {
                application.sendPasswordResetEmail(email);
            }
        } catch (ResourceException e) {
            //404 == resource does not exist.  Do not let the user know that the account does not
            //exist, otherwise we open up to phishing attacks
            if (e.getCode() != 404) {
                throw e;
            }
            //otherwise don't do anything
        }

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getForgotPasswordNextUrl();
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
                    //TODO: i18n
                    throw new IllegalArgumentException(Strings.capitalize(field.getName()) + " is required.");
                }
            }
        }
    }

    protected void validateCsrfToken(HttpServletRequest request, HttpServletResponse response, Form form) {
        String csrfToken = form.getCsrfToken();
        Assert.isTrue(getCsrfTokenManager().isValidCsrfToken(request, response, csrfToken), "Invalid CSRF token");
    }

}
