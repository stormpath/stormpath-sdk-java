/*
 * Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.config.Config;
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
 * @since 1.0.RC8.3
 */
public class SendVerificationEmailController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(SendVerificationEmailController.class);

    private String loginUri;
    private AccountStoreResolver accountStoreResolver;

    public SendVerificationEmailController() {
        super();
    }

    public SendVerificationEmailController(Config config) {
        super(config.getSendVerificationEmailControllerConfig(), config.getProducesMediaTypes());

        this.loginUri = config.getLoginControllerConfig().getUri();
        this.accountStoreResolver = config.getAccountStoreResolver();

        Assert.hasText(this.loginUri, "loginUri cannot be null.");
        Assert.notNull(this.accountStoreResolver, "accountStoreResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
    }

    @Override
    protected List<Field> createFields(HttpServletRequest request, boolean retainPassword) {

        List<Field> fields = new ArrayList<Field>(1);

        String[] fieldNames = new String[]{"email"};

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
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        log.debug("Unable to send account verification email.", e);

        List<ErrorModel> errors = new ArrayList<ErrorModel>(1);
        errors.add(ErrorModel.builder().setMessage("Invalid email address.").build());

        return errors;
    }

    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) {

        Application application = (Application) request.getAttribute(Application.class.getName());

        String email = form.getFieldValue("email");

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            request.setAttribute("form", form);
            AccountStore accountStore = accountStoreResolver.getAccountStore(request, response);

            VerificationEmailRequest verificationEmailRequest = Applications.verificationEmailBuilder()
                    .setLogin(email)
                    .setAccountStore(accountStore)
                    .build();

            application.sendVerificationEmail(verificationEmailRequest);
        } catch (ResourceException e) {
            //404 == resource does not exist.  Do not let the user know that the account does not
            //exist, otherwise we open up to phishing attacks
            if (e.getCode() != 404) {
                throw e;
            }
            //otherwise don't do anything
        }

        return new DefaultViewModel(nextUri);
    }
}
