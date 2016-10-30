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
package com.stormpath.sdk.impl.application.webconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stormpath.sdk.application.webconfig.ClientCredentialsConfig;
import com.stormpath.sdk.application.webconfig.Oauth2Config;
import com.stormpath.sdk.application.webconfig.PasswordConfig;
import com.stormpath.sdk.application.webconfig.WebFeatureConfig;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;

import java.util.Map;

public class DefaultOauth2Config extends ConfigurableProperty implements Oauth2Config {

    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> PASSWORD;

    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> CLIENT_CREDENTIALS;

    static {
        PASSWORD = new ParentAwareObjectProperty<>("password", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        CLIENT_CREDENTIALS = new ParentAwareObjectProperty<>("client_credentials", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
    }

    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultOauth2Config(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    @JsonProperty("client_credentials")
    public ClientCredentialsConfig getClientCredentialsConfig() {
        return getParentAwareObjectProperty(CLIENT_CREDENTIALS);
    }

    public void setClientCredentials(WebFeatureConfig clientCredentials) {
        setProperty(CLIENT_CREDENTIALS, clientCredentials);
    }

    @Override
    @JsonProperty("password")
    public PasswordConfig getPasswordConfig() {
        return getParentAwareObjectProperty(PASSWORD);
    }

    public void setPassword(WebFeatureConfig password) {
        setProperty(PASSWORD, password);
    }

    @Override
    public boolean isEnabled() {
        return getBoolean(ENABLED);
    }

    public void setEnabled(boolean enabled) {
        setProperty(ENABLED, enabled);
    }
}
