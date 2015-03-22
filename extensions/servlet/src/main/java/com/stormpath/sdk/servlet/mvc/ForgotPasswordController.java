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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class ForgotPasswordController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordController.class);

    private String loginUri;
    private String nextView;
    private AccountStoreResolver accountStoreResolver;

    public void init() {
        super.init();
        Assert.hasText(this.nextView, "nextView cannot be null.");
        Assert.hasText(this.loginUri, "loginUri cannot be null.");
    }

    protected AccountStoreResolver getAccountStoreResolver() {
        return accountStoreResolver;
    }

    public void setAccountStoreResolver(AccountStoreResolver accountStoreResolver) {
        this.accountStoreResolver = accountStoreResolver;
    }

    public String getNextView() {
        return nextView;
    }

    public void setNextView(String nextView) {
        Assert.hasText(nextView, "nextView cannot be null or empty.");
        this.nextView = nextView;
    }

    public String getLoginUri() {
        return loginUri;
    }

    public void setLoginUri(String loginUri) {
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        this.loginUri = loginUri;
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
            field.setLabel("stormpath.web.forgot.form.fields." + fieldName + ".label");
            field.setPlaceholder("stormpath.web.forgot.form.fields." + fieldName + ".placeholder");
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
        log.debug("Unable to send reset password email.", e);

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
            if (accountStore != null) {
                application.sendPasswordResetEmail(email, accountStore);
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
            next = getNextView();
        }

        return new DefaultViewModel(next).setRedirect(true);
    }
}
