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
import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class VerifyController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(VerifyController.class);

    private String loginUri;
    private String logoutUri;
    private Client client;
    private Publisher<RequestEvent> eventPublisher;
    private AccountStoreResolver accountStoreResolver;
    private boolean autoLogin;
    private AccountModelFactory accountModelFactory;
    private ErrorMapModelFactory errorMap;

    public void init() {
        this.accountModelFactory = new DefaultAccountModelFactory();
        this.errorMap = new DefaultErrorMapModelFactory();
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        Assert.hasText(logoutUri, "logoutUri cannot be null or empty.");
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
    }

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return true;
    }

    protected AccountStoreResolver getAccountStoreResolver() {
        return accountStoreResolver;
    }

    public void setAccountStoreResolver(AccountStoreResolver accountStoreResolver) {
        this.accountStoreResolver = accountStoreResolver;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        this.loginUri = loginUri;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return this.eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            if (UserAgents.get(request).isJsonPreferred()) {
                Map<String,Object> model = new HashMap<String, Object>();
                model.put("status", 400);
                model.put("message", "sptoken parameter not provided.");
                return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
            }
            //redirect to send verification email form
            String view = getView();
            Map<String,?> model = createModel(request, response);
            return new DefaultViewModel(view, model);
        }

        try {
            return verify(request, response, sptoken);
        }
        catch (ResourceException re) {
            if (UserAgents.get(request).isJsonPreferred()) {
                Map<String,Object> model = errorMap.toErrorMap(re.getStormpathError());
                return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
            }
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            String logoutUri = getLogoutUri();
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }
        catch (Exception e) {
            if (UserAgents.get(request).isJsonPreferred()) {
                Map<String,Object> model = new HashMap<String, Object>();
                model.put("status", 400);
                model.put("message", "Invalid username or password.");
                return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
            }
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            String logoutUri = getLogoutUri();
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }
    }

    protected ViewModel verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
        throws ServletException, IOException {

        Client client = getClient();

        Account account = client.verifyAccountEmail(sptoken);
        //The sptoken is valid

        RequestEvent e = createVerifiedEvent(request, response, account);
        publish(e);

        if (UserAgents.get(request).isJsonPreferred()) {
            Map<String,Object> model = new HashMap<String, Object>();
            if (isAutoLogin()){
                model.put("status", 200);
                model.put("account", accountModelFactory.toMap(account));
            }
            else {
                model.put("status", 200);
                model.put("message", "OK");
            }
            return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
        }

        String next = Strings.clean(request.getParameter("next"));

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        return new DefaultViewModel(next).setRedirect(true);
    }

    protected VerifiedAccountRequestEvent createVerifiedEvent(HttpServletRequest request, HttpServletResponse response,
                                                              Account account) {
        return new DefaultVerifiedAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {
        try {
            getEventPublisher().publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish verified account request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<String> errors,
                               Map<String, Object> model) {
        model.put("loginUri", getLoginUri());
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(1);

        String[] fieldNames = new String[]{ "email" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setLabel("stormpath.web.sendVerificationEmail.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.sendVerificationEmail.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("text");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

            fields.add(field);
        }

        return fields;
    }

    @Override
    protected List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        log.debug("Unable to send account verification email.", e);

        List<String> errors = new ArrayList<String>(1);
        errors.add("Invalid email address.");

        return errors;
    }

    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) {

        Application application = (Application) request.getAttribute(Application.class.getName());

        String email = form.getFieldValue("email");

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            request.setAttribute("form", form);
            AccountStore accountStore = getAccountStoreResolver().getAccountStore(request, response);

            VerificationEmailRequest verificationEmailRequest = Applications.verificationEmailBuilder()
                    .setLogin(email)
                    .setAccountStore(accountStore)
                    .build();

            application.sendVerificationEmail(verificationEmailRequest);
        } catch (ResourceException e) {
            //404 == resource does not exist.  Do not let the user know that the account does not
            //exist, otherwise we open up to phishing attacks
            if (e.getCode() != 404) {
                if (UserAgents.get(request).isJsonPreferred()) {
                    Map<String,Object> model = errorMap.toErrorMap(e.getStormpathError());
                    return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
                }
                throw e;
            }
            //otherwise don't do anything
        }

        if (UserAgents.get(request).isJsonPreferred()) {
            Map<String,Object> model = new HashMap<String, Object>();
            model.put("status", 200);
            model.put("message", "OK");
            return new DefaultViewModel("stormpath/verify", model).setRedirect(false);
        }

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        return new DefaultViewModel(next);
    }
}
