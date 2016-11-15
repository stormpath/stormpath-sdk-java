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

import com.stormpath.sdk.application.webconfig.Oauth2Config;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;

import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultOauth2Config extends ConfigurableProperty implements Oauth2Config {

    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultOauth2Config(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public Boolean isEnabled() {
        return getBoolean(ENABLED);
    }

    public Oauth2Config setEnabled(Boolean enabled) {
        setProperty(ENABLED, enabled);
        return this;
    }
}
