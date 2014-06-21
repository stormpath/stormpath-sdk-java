/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.idsite;

import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.idsite.Nonce;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DefaultNonce implements Nonce {

    public static final StringProperty VALUE = new StringProperty("value");

    private final Map<String, Object> properties;

    public DefaultNonce(String nonceValue) {
        Assert.hasText(nonceValue);

        properties = new HashMap<String, Object>();
        properties.put(VALUE.getName(), nonceValue);
    }

    public DefaultNonce(Map<String, Object> properties) {

        Assert.notEmpty(properties);

        Assert.isTrue(properties.size() == 1 && properties.containsKey(VALUE.getName()));

        Object value = properties.get(VALUE.getName());

        Assert.isInstanceOf(String.class, value);

        this.properties = properties;
    }

    @Override
    public String getValue() {
        return properties.get(VALUE.getName()).toString();
    }

    @Override
    public String getHref() {
        return getValue();
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
