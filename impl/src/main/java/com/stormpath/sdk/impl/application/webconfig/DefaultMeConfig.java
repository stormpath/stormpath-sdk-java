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
import com.stormpath.sdk.application.webconfig.MeExpansionConfig;
import com.stormpath.sdk.application.webconfig.MeConfig;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;

import java.util.Map;

public class DefaultMeConfig extends ConfigurableProperty implements MeConfig {

    private static final ParentAwareObjectProperty<DefaultMeExpansionConfig, AbstractPropertyRetriever> EXPAND;
    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    static {
        EXPAND = new ParentAwareObjectProperty<>("expand", DefaultMeExpansionConfig.class, AbstractPropertyRetriever.class);
    }

    public DefaultMeConfig(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    @JsonProperty("expand")
    public MeExpansionConfig getMeExpansionConfig() {
        return getParentAwareObjectProperty(EXPAND);
    }

    @Override
    public boolean isEnabled() {
        return getBoolean(ENABLED);
    }

    @Override
    public void setEnabled(boolean enabled) {
        setProperty(ENABLED, enabled);
    }

}
