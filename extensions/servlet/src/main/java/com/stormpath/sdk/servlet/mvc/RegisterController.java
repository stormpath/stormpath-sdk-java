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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class RegisterController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private Client client;
    private Publisher<RequestEvent> eventPublisher;
    private List<DefaultField> formFields;
    private Resolver<Locale> localeResolver;
    private MessageSource messageSource;
    //only used if account does not need email verification:
    private Saver<AuthenticationResult> authenticationResultSaver;

    private String loginUri;
    private String verifyViewName;

    @Override
    public void init() {
        super.init();
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
        Assert.notEmpty(formFields, "formFields cannot be null or empty.");
        Assert.notNull(localeResolver, "localeResolver cannot be null.");
        Assert.notNull(messageSource, "messageSource cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.hasText(verifyViewName, "verifyViewName cannot be null or empty.");
    }

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return true;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public List<DefaultField> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<DefaultField> formFields) {
        this.formFields = formFields;
    }

    public Resolver<Locale> getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(Resolver<Locale> localeResolver) {
        this.localeResolver = localeResolver;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource i18n) {
        this.messageSource = i18n;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public String getVerifyViewName() {
        return verifyViewName;
    }

    public void setVerifyViewName(String verifyViewName) {
        this.verifyViewName = verifyViewName;
    }

    protected String i18n(HttpServletRequest request, String key) {
        Locale locale = getLocaleResolver().get(request, null);
        return getMessageSource().getMessage(key, locale);
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<String> errors,
                               Map<String, Object> model) {
        model.put("loginUri", getLoginUri());
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(this.formFields.size());

        for(DefaultField template : this.formFields) {

            DefaultField field = template.copy();
            String fieldName = field.getName();

            String val = fieldValueResolver.getValue(request, fieldName);
            field.setValue(val != null ? val : "");

            if ("password".equals(fieldName) && !retainPassword) {
                field.setValue("");
            }

            fields.add(field);
        }

        return fields;
    }

    @Override
    protected List<String> toErrors(HttpServletRequest request, Form form, Exception e) {

        log.debug("Unable to register account.", e);

        List<String> errors = new ArrayList<String>();

        if (e instanceof IllegalArgumentException || e instanceof MismatchedPasswordException) {
            errors.add(e.getMessage());
        } else if (e instanceof ResourceException) {
            errors.add(((ResourceException)e).getStormpathError().getMessage());
        } else {
            String key = "stormpath.web.register.form.errors.default";
            String msg = i18n(request, key);
            errors.add(msg);
            log.warn("Unable to resister user account: {}", e.getMessage(), e);
        }

        return errors;
    }

    @SuppressWarnings("UnusedParameters")
    protected Account newAccount(HttpServletRequest request) {
        Client client = getClient();
        return client.instantiate(Account.class);
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

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {

        validateCsrfToken(request, response, form);

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired()) {
                String value = getValue(field);
                if (value == null) {
                    String key = "stormpath.web.login.form.fields." + field.getName() + ".required";
                    String msg = i18n(request, key);
                    throw new IllegalArgumentException(msg);
                }
            }
        }

        //ensure passwords match:
        String password = form.getFieldValue("password");
        String confirmPassword = form.getFieldValue("confirmPassword");

        if (!password.equals(confirmPassword)) {
            String key = "stormpath.web.register.form.errors.passwordMismatch";
            String msg = i18n(request, key);
            throw new MismatchedPasswordException(msg);
        }
    }

    protected static class MismatchedPasswordException extends RuntimeException {
        public MismatchedPasswordException(String msg) {
            super(msg);
        }
    }

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest req, HttpServletResponse resp, Form form) throws Exception {

        //Create a new Account instance that will represent the submitted user information:
        Account account = newAccount(req);

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
        Application app = (Application)req.getAttribute(Application.class.getName());

        //now persist the new account, and ensure our account reference points to the newly created/returned instance:
        account = app.createAccount(account);

        AccountStatus status = account.getStatus();

        RequestEvent e = createRegisteredEvent(req, resp, account);
        publish(e);

        if (status == AccountStatus.ENABLED) {

            //the user does not need to verify their email address, so just assume they are authenticated
            //(since they specified their password during registration):
            final AuthenticationResult result = new TransientAuthenticationResult(account);
            this.authenticationResultSaver.set(req, resp, result);

        } else if (status == AccountStatus.UNVERIFIED) {

            // The user must verify their email address:
            //
            // NOTE: we don't use the authenticationResultSaver here because we don't want the identity
            // to be retained after this request.  To avoid security attack vectors, the end-user must
            // verify their email address and then explicitly authenticate after doing so.  At that time,
            // the authentication state will be saved appropriately via the login filter.
            //
            // We set the account as a request attribute here only for the remainder of the request in case the
            // verify.jsp view wants to use it to render a user-specific message, for example,
            //
            // 'Thanks for registering Joe!  Almost done - please verify your email...'
            //
            Map<String,Object> model = newModel();
            model.put(Account.class.getName(), account);
            model.put("account", account);
            return new DefaultViewModel(getVerifyViewName(), model);
        }

        //finish up by showing the 'post register' view:
        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        return new DefaultViewModel(next).setRedirect(true);
    }

    protected RegisteredAccountRequestEvent createRegisteredEvent(HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  Account account) {
        return new DefaultRegisteredAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {
        try {
            getEventPublisher().publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish registered account request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }
}
