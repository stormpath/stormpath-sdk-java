/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.resource.impl;

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @since 0.1
 */
public abstract class AbstractResource implements Resource {

    private static final Logger log = LoggerFactory.getLogger(AbstractResource.class);

    public static final String HREF_PROP_NAME = "href";

    private final Map<String, Object> properties;
    private final DataStore dataStore;
    protected final Lock readLock;
    protected final Lock writeLock;

    private volatile boolean materialized;
    private volatile boolean dirty;

    protected AbstractResource(DataStore dataStore) {
        this(dataStore, null);
    }

    protected AbstractResource(DataStore dataStore, Map<String, Object> properties) {
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
        this.dataStore = dataStore;
        this.properties = new LinkedHashMap<String, Object>();
        setProperties(properties);
    }

    public final void setProperties(Map<String, Object> properties) {
        writeLock.lock();
        try {
            this.properties.clear();
            this.dirty = false;
            if (properties != null && !properties.isEmpty()) {
                this.properties.putAll(properties);
                // Don't consider this resource materialized if it is only a reference.  A reference is any object that
                // has only one 'href' property.
                boolean hrefOnly = this.properties.size() == 1 && this.properties.containsKey(HREF_PROP_NAME);
                this.materialized = !hrefOnly;
            } else {
                this.materialized = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public String getHref() {
        return getStringProperty(HREF_PROP_NAME);
    }

    protected final DataStore getDataStore() {
        return this.dataStore;
    }

    protected final boolean isMaterialized() {
        return this.materialized;
    }

    protected final boolean isDirty() {
        return this.dirty;
    }

    /**
     * Returns {@code true} if the resource doesn't yet have an assigned 'href' property, {@code false} otherwise.
     *
     * @return {@code true} if the resource doesn't yet have an assigned 'href' property, {@code false} otherwise.
     * @since 0.2
     */
    protected final boolean isNew() {
        //we can't call getHref() in here, otherwise we'll have an infinite loop:
        Object prop = readProperty(HREF_PROP_NAME);
        if (prop == null) {
            return true;
        }
        String href = String.valueOf(prop);
        return !StringUtils.hasText(href);
    }

    protected void materialize() {
        AbstractResource resource = dataStore.load(getHref(), getClass());
        writeLock.lock();
        try {
            this.properties.clear();
            this.properties.putAll(resource.properties);
            this.materialized = true;
        } finally {
            writeLock.unlock();
        }
    }

    public Set<String> getPropertyNames() {
        readLock.lock();
        try {
            Set<String> keys = this.properties.keySet();
            return new LinkedHashSet<String>(keys);
        } finally {
            readLock.unlock();
        }
    }

    public Object getProperty(String name) {
        if (!HREF_PROP_NAME.equals(name)) {
            //not the href/id, must be a property that requires materialization:
            if (!isNew() && !isMaterialized()) {
                materialize();
            }
        }

        return readProperty(name);
    }

    private Object readProperty(String name) {
        readLock.lock();
        try {
            return this.properties.get(name);
        } finally {
            readLock.unlock();
        }
    }

    protected void setProperty(String name, Object value) {
        writeLock.lock();
        try {
            if (value == null) {
                Object removed = this.properties.remove(name);
                if (removed != null) {
                    this.dirty = true;
                }
            } else {
                this.properties.put(name, value);
                this.dirty = true;
            }
        } finally {
            writeLock.unlock();
        }
    }

    protected String getStringProperty(String key) {
        Object value = getProperty(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    protected int getIntProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof String) {
                return parseInt((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return -1;
    }

    private String getHref(Map props) {
        Object value = props != null ? props.get(HREF_PROP_NAME) : null;
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    protected <T extends Resource> T getResourceProperty(String key, Class<T> clazz) {
        Object value = getProperty(key);
        if (value instanceof Map) {
            String href = getHref((Map) value);
            if (href != null) {
                return dataStore.load(href, clazz);
            }
        }
        return null;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            if (log.isErrorEnabled()) {
                String msg = "Unabled to parse string '{}' into an integer value.  Defaulting to -1";
                log.error(msg, e);
            }
        }
        return -1;
    }

    public String toString() {
        readLock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append(": ").append(String.valueOf(entry.getValue()));
                first = false;
            }
            return sb.toString();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int hashCode() {
        readLock.lock();
        try {
            return this.properties.isEmpty() ? 0 : this.properties.hashCode();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!o.getClass().equals(getClass())) {
            return false;
        }
        AbstractResource other = (AbstractResource)o;
        readLock.lock();
        try {
            other.readLock.lock();
            try {
                return this.properties.equals(other.properties);
            } finally {
                other.readLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }
}
