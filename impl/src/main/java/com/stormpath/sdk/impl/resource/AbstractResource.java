/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.Resource;
import org.codehaus.jackson.map.util.ISO8601DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
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

    private static final DateFormat dateFormatter = new ISO8601DateFormat();

    public static final String HREF_PROP_NAME = "href";

    protected final Map<String, Object> properties;       //Protected by read/write lock
    protected final Map<String, Object> dirtyProperties;  //Protected by read/write lock
    protected final Set<String> deletedPropertyNames;     //Protected by read/write lock
    private final InternalDataStore dataStore;
    protected final Lock readLock;
    protected final Lock writeLock;

    private volatile boolean materialized;
    protected volatile boolean dirty;

    protected final ReferenceFactory referenceFactory;

    protected AbstractResource(InternalDataStore dataStore) {
        this(dataStore, null);
    }

    protected AbstractResource(InternalDataStore dataStore, Map<String, Object> properties) {
        this.referenceFactory = new ReferenceFactory();
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
        this.dataStore = dataStore;
        this.properties = new LinkedHashMap<String, Object>();
        this.dirtyProperties = new LinkedHashMap<String, Object>();
        this.deletedPropertyNames = new HashSet<String>();
        setProperties(properties);
    }

    protected static Map<String, Property> createPropertyDescriptorMap(Property... props) {
        Map<String, Property> m = new LinkedHashMap<String, Property>();
        for (Property prop : props) {
            m.put(prop.getName(), prop);
        }
        return m;
    }

    public abstract Map<String, Property> getPropertyDescriptors();

    public final void setProperties(Map<String, Object> properties) {
        writeLock.lock();
        try {
            this.properties.clear();
            this.dirtyProperties.clear();
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

    protected final InternalDataStore getDataStore() {
        return this.dataStore;
    }

    protected final boolean isMaterialized() {
        return this.materialized;
    }

    /**
     * Returns {@code true} if the resource's properties have been modified in anyway since the resource instance was
     * created.
     *
     * @return {@code true} {@code true} if the resource's properties have been modified in anyway since the resource
     * instance was created
     */
    public final boolean isDirty() {
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
        return !Strings.hasText(href);
    }

    protected void materialize() {
        AbstractResource resource = dataStore.getResource(getHref(), getClass());
        writeLock.lock();
        try {
            this.properties.clear();
            this.properties.putAll(resource.properties);

            //retain dirty properties:
            this.properties.putAll(this.dirtyProperties);

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

    public Set<String> getUpdatedPropertyNames() {
        readLock.lock();
        try {
            Set<String> keys = this.dirtyProperties.keySet();
            return new LinkedHashSet<String>(keys);
        } finally {
            readLock.unlock();
        }
    }

    protected Set<String> getDeletedPropertyNames() {
        readLock.lock();
        try {
            return new LinkedHashSet<String>(this.deletedPropertyNames);
        } finally {
            readLock.unlock();
        }
    }

    public Object getProperty(String name) {
        if (!HREF_PROP_NAME.equals(name)) {
            //not the href/id, must be a property that requires materialization:
            if (!isNew() && !isMaterialized()) {

                //only materialize if the property hasn't been set previously (no need to execute a server
                // request since we have the most recent value already):
                boolean present = false;
                readLock.lock();
                try {
                    present = this.dirtyProperties.containsKey(name);
                } finally {
                    readLock.unlock();
                }

                if (!present) {
                    //exhausted present properties - we require a server call:
                    materialize();
                }
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

    /**
     * @since 0.8
     */
    protected void setProperty(Property property, Object value) {
        setProperty(property.getName(), value, true);
    }

    protected void setProperty(String name, Object value) {
        setProperty(name, value, true);
    }

    /**
     * @since 0.6.0
     */
    protected Object setProperty(String name, Object value, final boolean dirty) {
        writeLock.lock();
        Object previous ;
        try {
            previous = this.properties.put(name, value);
            if (dirty) {
                this.dirtyProperties.put(name, value);
                this.dirty = true;
            }
            if (this.deletedPropertyNames.contains(name)) {
                this.deletedPropertyNames.remove(name);
            }
        } finally {
            writeLock.unlock();
        }
        return previous;
    }

    /**
     * @since 0.8
     */
    protected String getString(StringProperty property) {
        return getStringProperty(property.getName());
    }

    protected String getStringProperty(String key) {
        Object value = getProperty(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    protected Date getDateProperty(DateProperty key) {
        Object value = getProperty(key.getName());
        if (value == null) {
            return null;
        }

        try {
            return dateFormatter.parse(String.valueOf(value));
        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                String msg = "Unabled to parse string '{}' into an date value.  Defaulting to null.";
                log.error(msg, e);
            }
        }
        return null;
    }

    /**
     * @since 0.8
     */
    protected int getInt(IntegerProperty property) {
        return getIntProperty(property.getName());
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

    /**
     * @since 0.9
     */
    protected boolean getBoolean(BooleanProperty property) {
        return getBooleanProperty(property.getName());
    }

    /**
     * Returns an actual boolean value instead of a possible null Boolean value since desired usage
     * is to have either a true or false.
     *
     * @since 0.9
     */
    protected boolean getBooleanProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.valueOf((String) value);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    protected <T extends Resource> T getResourceProperty(ResourceReference<T> property) {
        String key = property.getName();
        Class<T> clazz = property.getType();

        Object value = getProperty(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        if (value instanceof Map && !((Map) value).isEmpty()) {
            T resource = dataStore.instantiate(clazz, (Map<String, Object>) value);

            //replace the existing link object (map with an href) with the newly constructed Resource instance.
            //Don't dirty the instance - we're just swapping out a property that already exists for the materialized version.
            setProperty(key, resource, false);
            return resource;
        }

        String msg = "'" + key + "' property value type does not match the specified type.  Specified type: " +
                clazz.getName() + ".  Existing type: " + value.getClass().getName();
        msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
        throw new IllegalArgumentException(msg);
    }


//    /**
//     * @since 0.8
//     */
//    @SuppressWarnings("unchecked")
//    protected <T extends Resource, R extends T> R getSpecificResourceProperty(ResourceReference<T> property, Class<>) {
//        String key = property.getName();
//        Class<T> clazz = property.getType();
//
//        Object value = getProperty(key);
//        if (value == null) {
//            return null;
//        }
//        if (clazz.isInstance(value)) {
//            return (R) value;
//        }
//        if (value instanceof Map && !((Map) value).isEmpty()) {
//            T resource = dataStore.instantiate(clazz, (Map<String, Object>) value);
//
//
//            //replace the existing link object (map with an href) with the newly constructed Resource instance.
//            //Don't dirty the instance - we're just swapping out a property that already exists for the materialized version.
//            setProperty(key, resource, false);
//            return resource;
//        }
//
//        String msg = "'" + key + "' property value type does not match the specified type.  Specified type: " +
//                clazz.getName() + ".  Existing type: " + value.getClass().getName();
//        msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
//        throw new IllegalArgumentException(msg);
//    }

    /**
     * @param property
     * @param value
     * @param <T>
     * @since 0.9
     */
    protected <T extends Resource> void setResourceProperty(ResourceReference<T> property, Resource value) {
        Assert.notNull(property, "Property argument cannot be null.");
        String name = property.getName();
        Map<String, String> reference = this.referenceFactory.createReference(name, value);
        setProperty(name, reference);
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
            for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                String key = entry.getKey();
                //prevent printing of any sensitive values:
                if (isPrintableProperty(key)) {
                    sb.append(key).append(": ").append(String.valueOf(entry.getValue()));
                }
            }
            return sb.toString();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns {@code true} if the internal property is safe to print in toString(), {@code false} otherwise.
     *
     * @param name The name of the property to check for safe printing
     * @return {@code true} if the internal property is safe to print in toString(), {@code false} otherwise.
     * @since 0.4.1
     */
    protected boolean isPrintableProperty(String name) {
        return true;
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
        AbstractResource other = (AbstractResource) o;
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
