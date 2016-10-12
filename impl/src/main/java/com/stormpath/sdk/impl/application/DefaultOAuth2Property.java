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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.EnabledProperty;
import com.stormpath.sdk.application.OAuth2Property;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;

import java.util.Map;

public class DefaultOAuth2Property extends ConfigurableProperty implements OAuth2Property {

    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> PASSWORD;

    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> CLIENT_CREDENTIALS;

    static {
        PASSWORD = new ParentAwareObjectProperty<>("password", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        CLIENT_CREDENTIALS = new ParentAwareObjectProperty<>("client_credentials", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
    }

    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultOAuth2Property(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public EnabledProperty getClientCredentials() {
        return getParentAwareObjectProperty(CLIENT_CREDENTIALS);
    }

    public void setClientCredentials(EnabledProperty clientCredentials) {
        setProperty(CLIENT_CREDENTIALS, clientCredentials);
    }

    @Override
    public EnabledProperty getPassword() {
        return getParentAwareObjectProperty(PASSWORD);
    }

    public void setPassword(EnabledProperty password) {
        setProperty(PASSWORD, password);
    }

    @Override
    public Boolean isEnabled() {
        return getBoolean(ENABLED);
    }

    public void setEnabled(Boolean enabled) {
        setProperty(ENABLED, enabled);
    }
}
