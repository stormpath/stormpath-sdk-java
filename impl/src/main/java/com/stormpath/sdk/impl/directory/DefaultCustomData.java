/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @since 0.9
 */
public class DefaultCustomData extends AbstractInstanceResource implements CustomData {

    static final DateProperty CREATED_AT = new DateProperty("createdAt");
    static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            CREATED_AT, MODIFIED_AT);

    public DefaultCustomData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultCustomData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return properties.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return properties.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.isInstanceOf(String.class, key);
        readLock.lock();
        try {
            return properties.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        readLock.lock();
        try {
            return properties.containsValue(value);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object get(Object key) {
        Assert.isInstanceOf(String.class, key);
        return super.getProperty(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        return super.setProperty(key, value, true);
    }

    @Override
    public Object remove(Object key) {
        Assert.isInstanceOf(String.class, key);
        writeLock.lock();
        try {
            Object object = this.properties.remove(key);
            this.dirtyProperties.remove(key);
            this.deletedPropertyNames.add(key.toString());
            this.dirty = true;
            return object;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        if (Collections.isEmpty(m)) {
            return;
        }
        Set<? extends Map.Entry<? extends String, ?>> entrySet = m.entrySet();
        writeLock.lock();
        try {
            for (Map.Entry<? extends String, ?> entry : entrySet) {
                setProperty(entry.getKey(), entry.getValue());
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            Set<String> propertiesToFilter = new HashSet<String>();
            propertiesToFilter.add(HREF_PROP_NAME);
            propertiesToFilter.addAll(getPropertyDescriptors().keySet());

            for (String propertyName : getPropertyNames()) {
                if (propertiesToFilter.contains(propertyName)) {
                    continue;
                }
                this.properties.remove(propertyName);
                this.dirtyProperties.remove(propertyName);
                this.deletedPropertyNames.add(propertyName);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<String> keySet() {
        return super.getPropertyNames();
    }

    @Override
    public Collection<Object> values() {
        readLock.lock();
        try {
            return properties.values();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        readLock.lock();
        try {
            return this.properties.entrySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save() {
        if (isDirty()) {
            if (hasRemovedProperties()) {
                deleteRemovedProperties();
            }
            if (hasNewProperties()) {
                super.save();
            }
        }
    }

    public void deleteRemovedProperties() {
        Set<String> deletedPropertyNames = this.getDeletedPropertyNames();
        for (String deletedPropertyName : deletedPropertyNames) {
            getDataStore().deleteResourceProperty(this, deletedPropertyName);
        }
    }

    public boolean hasRemovedProperties() {
        readLock.lock();
        try {
            return !deletedPropertyNames.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public boolean hasNewProperties() {
        readLock.lock();
        try {
            return !dirtyProperties.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

}
