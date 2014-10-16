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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterFilter extends PathMatchingFilter {

    private static final Logger log = LoggerFactory.getLogger(RegisterFilter.class);

    public static final String FIELD_CONFIG_NAME_PREFIX = "stormpath.web.register.form.fields.";

    /**
     * Returns the context-relative URL where a user can be redirected to login.
     *
     * @return the context-relative URL where a user can be redirected to login.
     */
    public String getRegisterUrl() {
        return getConfig().getRegisterUrl();
    }

    public String getRegisterNextUrl() {
        return getConfig().getRegisterNextUrl();
    }

    @Override
    protected void onInit() throws ServletException {
        String pattern = getRegisterUrl();
        int i = pattern.indexOf('?');
        if (i != -1) {
            pattern = pattern.substring(0, i);
        }
        i = pattern.indexOf(';');
        if (i != -1) {
            pattern = pattern.substring(0, i);
        }
        this.pathPatterns.add(pattern);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            doGet(request, response);
        } else if ("POST".equalsIgnoreCase(method)) {
            doPost(request, response);
        } else {
            ServletUtils.issueRedirect(request, response, getRegisterUrl(), null, true, true);
        }
    }

    /*
    protected void addConfigProperties(HttpServletRequest request) {
        for (Map.Entry<String, String> entry : getConfig().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    */

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //addConfigProperties(request);
        Form form = createForm(request, false);
        setForm(request, form);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    protected Form createForm(HttpServletRequest request, boolean retainPassword) {

        DefaultForm form = new DefaultForm();

        String value = Strings.clean(request.getParameter("csrfToken"));
        value = value != null ? value : UUID.randomUUID().toString().replace("-", ""); //TODO add to cache
        form.setCsrfToken(value);

        value = Strings.clean(request.getParameter("next"));
        if (value != null) {
            form.setNext(value);
        }

        Config config = getConfig();

        for (String key : config.keySet()) {

            if (key.startsWith(FIELD_CONFIG_NAME_PREFIX)) {

                value = config.get(key);

                if (value.equalsIgnoreCase("disabled")) {
                    //don't include it in the form:
                    continue;
                }

                boolean required = value.equalsIgnoreCase("required");

                String fieldName = key.substring(FIELD_CONFIG_NAME_PREFIX.length());

                DefaultField field = new DefaultField();
                field.setName(fieldName);
                field.setRequired(required);
                field.setType("text");
                String param = request.getParameter(fieldName);
                field.setValue(param != null ? param : "");

                String label;

                if ("username".equals(fieldName)) {
                    label = "Username";
                } else if ("email".equals(fieldName)) {
                    label = "Email";
                } else if ("givenName".equals(fieldName)) {
                    label = "First Name";
                } else if ("middleName".equals(fieldName)) {
                    label = "Middle Name";
                } else if ("surname".equals(fieldName)) {
                    label = "Last Name";
                } else if ("password".equals(fieldName)) {
                    field.setType("password");
                    label = "Password";
                    if (!retainPassword) {
                        field.setValue("");
                    }
                } else {
                    //unrecognized property
                    continue;
                }

                field.setLabel(label).setPlaceholder(label);

                form.addField(field);
            }
        }

        form.autofocus();

        return form;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request, true);

        try {
            register(request, response, form);
        } catch (Exception e) {
            log.debug("Unable to register account.", e);

            //addConfigProperties(request);

            List<String> errors = new ArrayList<String>();

            if (e instanceof ResourceException) {
                errors.add(((ResourceException) e).getStormpathError().getMessage());
            } else {
                errors.add("Oops! Unable to register you!  Please contact support.");
                log.warn("Unable to resister user account: " + e.getMessage(), e);
            }
            request.setAttribute("errors", errors);

            //do not retain submitted password (not save to have in the DOM text):
            ((DefaultField) form.getField("password")).setValue("");
            setForm(request, form);

            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    protected void setForm(HttpServletRequest request, Form form) {
        request.setAttribute("form", form);
    }

    protected Account newAccount(ServletContext sc) {
        return ClientResolver.INSTANCE.getClient(sc).instantiate(Account.class);
    }

    protected String getValue(Form form, String fieldName) {
        Field field = form.getField(fieldName);
        if (field != null) {
            return getValue(field);
        }
        return null;
    }

    protected String getValue(Field field) {
        return Strings.clean(field.getValue());
    }

    protected void validate(Form form) throws ServletException, IOException {

        //validate CSRF
        Assert.hasText(form.getCsrfToken(), "CSRF Token must be specified."); //TODO check cache

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired()) {
                String value = getValue(field);
                if (value == null) {
                    //TODO: i18n
                    throw new IllegalArgumentException(Strings.capitalize(field.getName()) + " is required.");
                }
            }
        }
    }

    protected void register(HttpServletRequest req, HttpServletResponse resp, Form form)
        throws ServletException, IOException {

        validate(form);

        //Create a new Account instance that will represent the submitted user information:
        Account account = newAccount(req.getServletContext());

        String value = getValue(form, "email");
        if (value != null) {
            account.setEmail(value);
        }

        value = getValue(form, "username");
        if (value != null) {
            account.setUsername(value);
        }

        value = getValue(form, "password");
        if (value != null) {
            account.setPassword(value);
        }

        value = getValue(form, "givenName");
        account.setGivenName(value != null ? value : "UNSPECIFIED");

        value = getValue(form, "middleName");
        if (value != null) {
            account.setMiddleName(value);
        }

        value = getValue(form, "surname");
        account.setSurname(value != null ? value : "UNSPECIFIED");

        //Get the Stormpath Application instance corresponding to this web app:
        Application app = ApplicationResolver.INSTANCE.getApplication(req.getServletContext());

        //now persist the new account, and ensure our account reference points to the newly created/returned instance:
        account = app.createAccount(account);

        if (account.getStatus() == AccountStatus.UNVERIFIED) {
            // the user needs to verify their email address first:
            req.setAttribute("account", account);
            req.getRequestDispatcher("/verifyEmail.jsp").forward(req, resp);
            return;
        }

        //otherwise, continue:

        //TODO: session use should be configurable.  USE JWT when not.
        //put the account in the session for easy retrieval later:
        req.getSession().setAttribute("account", account);

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getRegisterNextUrl();
        }

        ServletUtils.issueRedirect(req, resp, next, null, true, true);
    }
}
