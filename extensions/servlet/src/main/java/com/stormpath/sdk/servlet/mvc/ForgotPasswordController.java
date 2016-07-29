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
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class ForgotPasswordController extends FormController {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordController.class);

    private String loginUri;
    private AccountStoreResolver accountStoreResolver;

    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }

    public void setAccountStoreResolver(AccountStoreResolver accountStoreResolver) {
        this.accountStoreResolver = accountStoreResolver;
    }

    @Override
    public void init() throws Exception {
        super.init();
        Assert.hasText(this.loginUri, "loginUri cannot be null.");
        Assert.notNull(this.accountStoreResolver, "accountStoreResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected String i18n(HttpServletRequest request, String key) {
        Locale locale = getLocaleResolver().get(request, null);
        return this.messageSource.getMessage(key, locale);
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isJsonPreferred(request, response)) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, Collections.EMPTY_MAP);
        }
        return super.doGet(request, response);
    }

    @Override
    protected void appendModel(HttpServletRequest request, HttpServletResponse response, Form form, List<ErrorModel> errors,
                               Map<String, Object> model) {
        model.put("loginUri", loginUri);
        String status = request.getParameter("status");
        if ("invalid_sptoken".equals(status)) {
            String key = "stormpath.web.forgotPassword.form.status";
            model.put("status", i18n(request, key.concat(".").concat(status)));
        }
    }

    @Override
    protected List<ErrorModel> toErrors(HttpServletRequest request, Form form, Exception e) {
        log.debug("Unable to send reset password email.", e);

        List<ErrorModel> errors = new ArrayList<>(1);
        errors.add(ErrorModel.builder().setMessage("Invalid email address.").build());

        return errors;
    }

    protected void validate(HttpServletRequest request, HttpServletResponse response, Form form) {
        if (!isJsonPreferred(request, response)){
            super.validate(request, response,form);
        }
    }

    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) {

        Application application = (Application) request.getAttribute(Application.class.getName());

        String email = form.getFieldValue("email");

        try {
            //set the form on the request in case the AccountStoreResolver needs to inspect it:
            request.setAttribute("form", form);
            AccountStore accountStore = accountStoreResolver.getAccountStore(request, response);
            if (accountStore != null) {
                application.sendPasswordResetEmail(email, accountStore);
            } else {
                application.sendPasswordResetEmail(email);
            }
        } catch (ResourceException e) {
            if (isJsonPreferred(request, response)) {
                return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME);
            }
        }
        if (isJsonPreferred(request, response)) {
            return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME);
        }

        return new DefaultViewModel(nextUri).setRedirect(true);
    }
}
