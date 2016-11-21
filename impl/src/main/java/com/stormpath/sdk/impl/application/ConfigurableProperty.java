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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.2.0
 */
public abstract class ConfigurableProperty extends AbstractPropertyRetriever {

    private final AbstractPropertyRetriever parent;

    private final String name;

    protected final Map<String, Object> dirtyProperties;

    protected Map<String, Object> properties;

    protected ConfigurableProperty(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        this.properties = properties == null ? new LinkedHashMap<String, Object>() : properties;
        this.dirtyProperties = new HashMap<>();
        this.parent = parent;
        this.name = name;
    }

    public Object getProperty(String name) {
        readLock.lock();
        try {
            return this.dirtyProperties.containsKey(name) ? this.dirtyProperties.get(name) : this.properties.get(name);
        } finally {
            readLock.unlock();
        }
    }

    protected Object setProperty(String name, Object value, boolean dirty) {
        writeLock.lock();
        try {
            boolean alreadySet = this.dirtyProperties.containsKey(name);

            Object previous = this.dirtyProperties.put(name, value);
            if (alreadySet) {
                previous = this.properties.get(name);
            }

            if (dirty) {
                parent.setProperty(this.name, this);
            }
            return previous;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected Map<String, Object> getProperties() {
        return properties;
    }
}
