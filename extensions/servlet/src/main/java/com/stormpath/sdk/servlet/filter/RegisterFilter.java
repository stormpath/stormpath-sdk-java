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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.DefaultFormFieldsParser;
import com.stormpath.sdk.servlet.mvc.RegisterController;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.RC3
 */
public class RegisterFilter extends ControllerFilter {

    public static final String FIELDS = "stormpath.web.register.form.fields";
    public static final String ACCOUNT_SAVER_PROP = "stormpath.web.authc.saver";
    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";
    public static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";
    public static final String LOCALE_RESOLVER = "stormpath.web.locale.resolver";
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";

    @Override
    protected void onInit() throws ServletException {

        Client client = getClient();
        Saver<AuthenticationResult> authenticationResultSaver = getConfig().getInstance(ACCOUNT_SAVER_PROP);
        CsrfTokenManager csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);
        Publisher<RequestEvent> eventPublisher = getConfig().getInstance(EVENT_PUBLISHER);
        Resolver<Locale> localeResolver = getConfig().getInstance(LOCALE_RESOLVER);
        MessageSource i18n = getConfig().getInstance(MESSAGE_SOURCE);

        RegisterController controller = new RegisterController();
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setClient(client);
        controller.setEventPublisher(eventPublisher);
        controller.setFormFields(toDefaultFields(createFields()));
        controller.setLocaleResolver(localeResolver);
        controller.setMessageSource(i18n);
        controller.setAuthenticationResultSaver(authenticationResultSaver);
        controller.setUri(getConfig().getRegisterUrl());
        controller.setView("stormpath/register");
        controller.setVerifyViewName("stormpath/verify");
        controller.setNextUri(getConfig().getRegisterNextUrl());
        controller.setLoginUri(getConfig().getLoginUrl());
        controller.init();

        setController(controller);

        super.onInit();
    }

    protected List<DefaultField> toDefaultFields(List<Field> fields) {
        List<DefaultField> defaultFields = new ArrayList<DefaultField>(fields.size());
        for (Field field : fields) {
            Assert.isInstanceOf(DefaultField.class, field);
            defaultFields.add((DefaultField) field);
        }

        return defaultFields;
    }

    protected List<Field> createFields() {
        DefaultFormFieldsParser parser = new DefaultFormFieldsParser(FIELDS);
        String val = getConfig().get(FIELDS);
        return parser.parse(val);
    }
}
