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
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.RequestAccountResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Form;
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
import java.util.Map;
import java.util.UUID;

public class LoginFilter extends PathMatchingFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);

    /**
     * Returns the context-relative URL where a user can be redirected to login.
     *
     * @return the context-relative URL where a user can be redirected to login.
     */
    public String getLoginUrl() {
        return getConfig().getLoginUrl();
    }

    public String getLoginNextUrl() {
        return getConfig().getLoginNextUrl();
    }

    @Override
    protected void onInit() throws ServletException {
        String pattern = getLoginUrl();
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
            ServletUtils.issueRedirect(request, response, getLoginUrl(), null, true, true);
        }
    }

    protected void addConfigProperties(HttpServletRequest request) {
        for (Map.Entry<String, String> entry : getConfig().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    private void setForm(HttpServletRequest request, Form form) {
        request.setAttribute("form", form);
    }

    @SuppressWarnings("unchecked")
    protected Form createForm(HttpServletRequest request, boolean retainPassword) {

        DefaultForm form = new DefaultForm();

        String value = Strings.clean(request.getParameter("csrfToken"));
        value = value != null ? value : UUID.randomUUID().toString().replace("-", ""); //TODO add to cache
        form.setCsrfToken(value);

        value = Strings.clean(request.getParameter("next"));
        if (value != null) {
            form.setNext(value);
        }

        String[] fieldNames = new String[]{"login", "password"};

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setRequired(true);
            field.setType("text");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

            String label;

            if ("login".equals(fieldName)) {
                label = "Email";
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

        form.autofocus();

        return form;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        addConfigProperties(request);
        Form form = createForm(request, false);
        setForm(request, form);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request, true);

        try {
            login(request, response, form);
        } catch (Exception e) {
            log.debug("Unable to login user.", e);

            List<String> errors = new ArrayList<String>(1);
            errors.add("Invalid username or password.");
            request.setAttribute("errors", errors);

            addConfigProperties(request);

            //do not retain submitted password (not save to have in the DOM text):
            ((DefaultField) form.getField("password")).setValue("");
            setForm(request, form);

            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected Account getAccount(HttpServletRequest req) {
        return RequestAccountResolver.INSTANCE.getAccount(req);
    }

    protected void login(HttpServletRequest req, HttpServletResponse resp, Form form)
        throws ServletException, IOException {

        Assert.hasText(form.getCsrfToken(), "CSRF Token must be specified."); //TODO check cache

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        Account account = getAccount(req);

        //TODO: session use should be configurable.  USE JWT when not.
        //put the account in the session for easy retrieval later:
        req.getSession().setAttribute("account", account);

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getLoginNextUrl();
        }

        ServletUtils.issueRedirect(req, resp, next, null, true, true);
    }
}
