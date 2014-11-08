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
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.RequestAccountResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Mutator;
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
import java.util.UUID;

public class LoginFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);

    protected static final String ACCOUNT_SAVER = "stormpath.web.account.saver";

    private Mutator<AuthenticationResult> accountSaver;

    @Override
    protected void onInit() throws ServletException {
        this.accountSaver = getConfig().getInstance(ACCOUNT_SAVER);
    }

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

    public Mutator<AuthenticationResult> getAccountSaver() {
        return this.accountSaver;
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
            ServletUtils.issueRedirect(request, response, getLoginUrl(), null, true, true);
        }
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAccountSaver().set(request, response, result);
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

        String[] fieldNames = new String[]{ "login", "password" };

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

    protected void showForm(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //addConfigProperties(request);
        Form form = createForm(request, false);
        setForm(request, form);
        String status = Strings.clean(request.getParameter("status"));
        if (status != null) {
            request.setAttribute("status", status);
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request, true);

        try {
            login(request, response, form);
        } catch (Exception e) {
            log.debug("Unable to login user.", e);

            List<String> errors = new ArrayList<String>(1);
            errors.add("Invalid username or password.");
            request.setAttribute("errors", errors);

            //addConfigProperties(request);

            //do not retain submitted password (not save to have in the DOM text):
            ((DefaultField) form.getField("password")).setValue("");
            setForm(request, form);

            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected Account getAccount(HttpServletRequest req) {
        return RequestAccountResolver.INSTANCE.getAccount(req);
    }

    protected void validate(Form form) throws ServletException, IOException {

        //validate CSRF
        Assert.hasText(form.getCsrfToken(), "CSRF Token must be specified."); //TODO check cache

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

    protected void login(HttpServletRequest req, HttpServletResponse resp, Form form)
        throws ServletException, IOException {

        validate(form);

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        final Account account = getAccount(req);

        //simulate a result for the benefit of the 'saveResult' method signature:
        AuthenticationResult result = createAuthenticationResult(account);
        saveResult(req, resp, result);

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getLoginNextUrl();
        }

        ServletUtils.issueRedirect(req, resp, next, null, true, true);
    }

    protected AuthenticationResult createAuthenticationResult(final Account account) {
        return new AuthenticationResult() {
            @Override
            public Account getAccount() {
                return account;
            }

            @Override
            public void accept(AuthenticationResultVisitor visitor) {
                visitor.visit(this);

            }

            @Override
            public String getHref() {
                return account.getHref();
            }
        };

    }
}
