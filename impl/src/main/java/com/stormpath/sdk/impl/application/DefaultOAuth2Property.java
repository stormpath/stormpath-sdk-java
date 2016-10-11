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
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ObjectProperty;

import java.util.Map;

public class DefaultOAuth2Property extends ConfigurableProperty implements OAuth2Property {

    private static final ObjectProperty<DefaultEnabledProperty> PASSWORD = new ObjectProperty<>("password", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> CLIENT_CREDENTIALS = new ObjectProperty<>("clientCredentials", DefaultEnabledProperty.class);
    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultOAuth2Property(Map<String, Object> properties) {
        super(properties);
    }

    @Override
    public EnabledProperty getClientCredentials() {
        return getObjectProperty(CLIENT_CREDENTIALS);
    }

    public void setClientCredentials(EnabledProperty clientCredentials) {
        setProperty(CLIENT_CREDENTIALS, clientCredentials);
    }

    @Override
    public EnabledProperty getPassword() {
        return getObjectProperty(PASSWORD);
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
