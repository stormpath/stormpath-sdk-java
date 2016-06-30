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
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @since 1.0.RC4
 */
public class VerifyController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(VerifyController.class);

    private String loginUri;
    private String loginNextUri;
    private Client client;
    private boolean autoLogin;
    private AccountModelFactory accountModelFactory;
    private ErrorModelFactory errorModelFactory;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private AccountStoreResolver accountStoreResolver;

    public VerifyController() {
        super();
    }

    public VerifyController(ControllerConfigResolver controllerConfigResolver,
                            String loginUri,
                            String loginNextUri,
                            Client client,
                            String produces,
                            boolean autoLogin,
                            Saver<AuthenticationResult> authenticationResultSaver,
                            AccountStoreResolver accountStoreResolver
                            ) {
        super(controllerConfigResolver, produces);

        this.loginUri = loginUri;
        this.loginNextUri = loginNextUri;
        this.client = client;
        this.autoLogin = autoLogin;
        this.accountModelFactory = new DefaultAccountModelFactory();
        this.errorModelFactory = new VerifyErrorModelFactory(messageSource);
        this.authenticationResultSaver = authenticationResultSaver;
        this.accountStoreResolver = accountStoreResolver;

        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        Assert.hasText(loginNextUri, "logoutUri cannot be null or empty.");
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
        Assert.notNull(accountModelFactory, "accountModelFactory cannot be null.");
        Assert.notNull(autoLogin, "autoLogin cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String sptoken = Strings.clean(fieldValueResolver.getValue(request, "sptoken"));

        if (sptoken == null) {
            if (isJsonPreferred(request, response)) {
                Map<String,Object> model = new HashMap<String, Object>();
                model.put("status", HttpServletResponse.SC_BAD_REQUEST);
                model.put("message", i18n(request, "stormpath.web.verifyEmail.form.errors.noToken"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new DefaultViewModel("stormpathJsonView", model);
            }
            Map<String, ?> model = createModel(request, response);
            return new DefaultViewModel(view, model).setRedirect(false);
        }

        try {
            return verify(request, response, sptoken);
        }
        catch (ResourceException re) {
            if (isJsonPreferred(request, response)) {
                Map<String,Object> model = errorModelFactory.toError(request, re).toMap();
                response.setStatus(errorModelFactory.toError(request, re).getStatus());
                return new DefaultViewModel("stormpathJsonView", model);
            }
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            Map<String,Object> model = newModel();
            List<String> errors = new ArrayList<String>();
            errors.add(i18n(request, "stormpath.web.verifyEmail.form.errors.invalidLink"));
            model.put("errors", errors);
            return new DefaultViewModel(view, model).setRedirect(false);

        }
        catch (Exception e) {
            if (isJsonPreferred(request, response)) {
                Map<String,Object> model = errorModelFactory.toError(request, e).toMap();
                errorModelFactory.toError(request, e);
                response.setStatus(errorModelFactory.toError(request, e).getStatus());
                return new DefaultViewModel("stormpathJsonView", model);
            }
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            Map<String,Object> model = newModel();
            List<String> errors = new ArrayList<String>();
            errors.add(i18n(request, "stormpath.web.verifyEmail.form.errors.invalidLink"));
            model.put("errors", errors);
            return new DefaultViewModel(view, model);
        }
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        return Arrays.asList(errorModelFactory.toError(request, e));
    }

    protected ViewModel verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
            throws ServletException, IOException {

        Account account = client.verifyAccountEmail(sptoken);
        //The sptoken is valid

        RequestEvent e = createVerifiedEvent(request, response, account);
        publish(e);

        if (UserAgents.get(request).isJsonPreferred()) {
            if (autoLogin){
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(request, response, result);

                Map<String,Object> model = new HashMap<String, Object>();
                model.put("account", accountModelFactory.toMap(account, Collections.EMPTY_LIST));
                return new DefaultViewModel("stormpathJsonView", model);
            }
            else {
                return new DefaultViewModel("stormpathJsonView");
            }
        }
        else {
            if (autoLogin) {
                final AuthenticationResult result = new TransientAuthenticationResult(account);
                this.authenticationResultSaver.set(request, response, result);

                return new DefaultViewModel(loginUri).setRedirect(true);
            }
            else {

                return new DefaultViewModel(nextUri).setRedirect(true);
            }
        }
    }

    protected VerifiedAccountRequestEvent createVerifiedEvent(HttpServletRequest request, HttpServletResponse response,
                                                              Account account) {
        return new DefaultVerifiedAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {
        try {
            eventPublisher.publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish verified account request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(1);

        String[] fieldNames = new String[]{ "email" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setLabel("stormpath.web.verifyEmail.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.verifyEmail.form.fields." + fieldName + ".placeholder");
            field.setRequired(true);
            field.setType("text");
            String param = fieldValueResolver.getValue(request, fieldName);
            field.setValue(param != null ? param : "");

            fields.add(field);
        }

        return fields;
    }

    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) {

        Application application = (Application) request.getAttribute(Application.class.getName());

        String email = fieldValueResolver.getValue(request, "email");

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            request.setAttribute("form", form);
            AccountStore accountStore = accountStoreResolver.getAccountStore(request, response);

            VerificationEmailRequest verificationEmailRequest = Applications.verificationEmailBuilder()
                    .setLogin(email)
                    .setAccountStore(accountStore)
                    .build();

            application.sendVerificationEmail(verificationEmailRequest);
        }
        finally {
            if (UserAgents.get(request).isJsonPreferred()) {
                return new DefaultViewModel("stormpathJsonView");
            }

            return new DefaultViewModel(nextUri.concat("?status=unverified"));
        }

    }
}
