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
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.DefaultFormFieldsParser;
import com.stormpath.sdk.servlet.mvc.RegisterController;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class RegisterFilter extends ControllerFilter {

    private static final String FIELDS = "stormpath.web.register.form.fields";
    private static final String ACCOUNT_SAVER_PROP = "stormpath.web.authc.saver";
    private static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";

    @Override
    protected void onInit() throws ServletException {
        Client client = getClient();
        Saver<AuthenticationResult> authenticationResultSaver = getConfig().getInstance(ACCOUNT_SAVER_PROP);
        Publisher<RequestEvent> eventPublisher = getConfig().getInstance(EVENT_PUBLISHER);

        RegisterController controller = new RegisterController(
                getConfig().getRegisterControllerConfig(),
                client,
                eventPublisher,
                toDefaultFields(createFields()),
                authenticationResultSaver,
                getConfig().getLoginControllerConfig().getUri(),
                getConfig().getVerifyControllerConfig().getView()
        );

        setController(controller);

        super.onInit();
    }

    protected List<Field> toDefaultFields(List<Field> fields) {
        List<Field> defaultFields = new ArrayList<Field>(fields.size());
        for (Field field : fields) {
            Assert.isInstanceOf(DefaultField.class, field);
            defaultFields.add(field);
        }

        return defaultFields;
    }

    protected List<Field> createFields() {
        DefaultFormFieldsParser parser = new DefaultFormFieldsParser(FIELDS);
        String val = getConfig().get(FIELDS);
        return parser.parse(val);
    }
}
