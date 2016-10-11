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

import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.Property;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurableProperty extends AbstractPropertyRetriever {

    protected final Map<String, Object> dirtyProperties;
    protected Map<String, Object> properties;

    protected ConfigurableProperty(Map<String, Object> properties) {
        this.properties = properties == null ? new HashMap<String, Object>() : properties;
        this.dirtyProperties = new HashMap<>();
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    protected Object setProperty(String name, Object value, boolean dirty) {
        Object previous = this.properties.put(name, value);
        if (dirty) {
            this.dirtyProperties.put(name, value);
        }
        return previous;
    }

    /**
     * @since 0.8
     */
    protected void setProperty(Property property, Object value) {
        setProperty(property.getName(), value, true);
    }

    protected void setProperty(String name, Object value) {
        setProperty(name, value, true);
    }
}
