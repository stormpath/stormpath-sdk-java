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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.AbstractControllerConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0.0
 */
public class ServletControllerConfig extends AbstractControllerConfig {

    private final String view;
    private final String uri;
    private final String nextUri;
    private final boolean enabled;

    public ServletControllerConfig(String controllerKey, final Config config) {
        super(controllerKey);
        Assert.notNull(config, "config cannot be null.");

        setPropertyResolver(new AbstractPropertyResolver() {
            @Override
            public String getValue(String key) {
                return config.get(key);
            }

            @Override
            public Set<String> getKeys(String prefix) {
                Set<String> keys = new HashSet<>();
                for (String key : config.keySet()) {
                    if (key != null && key.startsWith(prefix)) {
                        keys.add(key);
                    }
                }
                return keys;
            }
        });

        super.init();

        this.view = getControllerPropertyValue("view");
        this.uri = getControllerPropertyValue("uri");
        this.nextUri = getControllerPropertyValue("nextUri");
        String val = getControllerPropertyValue("enabled");
        this.enabled = val == null || Boolean.parseBoolean(val);
    }

    protected String getControllerPropertyValue(String propName) {
        String key = getConfigPrefix() + "." + propName;
        return getPropertyResolver().getValue(key);
    }

    @Override
    public String getView() {
        return this.view;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public String getNextUri() {
        return this.nextUri;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
