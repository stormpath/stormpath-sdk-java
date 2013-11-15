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

import java.util.Collection;
import java.util.Date;
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
        return super.propertiesSize();
    }

    @Override
    public boolean isEmpty() {
        return super.isPropertiesEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.isInstanceOf(String.class, key);
        return super.containsPropertyKey(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsPropertyValue(value);
    }

    @Override
    public Object get(Object key) {
        Assert.isInstanceOf(String.class, key);
        return super.getProperty(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        return super.putProperty(key, value);
    }

    @Override
    public Object remove(Object key) {
        Assert.isInstanceOf(String.class, key);
        return super.removeProperty(key.toString());
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        super.putAllProperties(m);
    }

    @Override
    public void clear() {
        super.clearProperties();
    }

    @Override
    public Set<String> keySet() {
        return super.getPropertyNames();
    }

    @Override
    public Collection<Object> values() {
        return super.getPropertyValues();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return super.propertiesEntrySet();
    }

    @Override
    public void save(){
        for(String deletedPropertyName : this.getDeletedPropertyNames()){
            getDataStore().deleteResourceProperty(this, deletedPropertyName);
        }
        if(isDirty()){
            super.save();
        }
    }
}
