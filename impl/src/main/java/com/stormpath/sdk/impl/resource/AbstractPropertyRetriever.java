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
package com.stormpath.sdk.impl.resource;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractPropertyRetriever {

    private static final Logger log = LoggerFactory.getLogger(AbstractPropertyRetriever.class);

    private static final DateFormat dateFormatter = new ISO8601DateFormat();

    public abstract Object getProperty(String name);

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

    /**
     * @since 0.8
     */

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
     * Returns the {@link List} property identified by {@code key}
     *
     * @since 1.0.RC8
     */
    protected List getListProperty(String key) {
        Object list = getProperty(key);
        return (List) list;
    }

    /**
     * Returns the {@link Set} property identified by {@code key}
     *
     * @since 1.0.RC8
     */
    protected Set getSetProperty(String key) {
        Object set = getProperty(key);
        return (Set) set;
    }

    /**
     * @since 1.0.RC4
     */
    protected Map getMap(MapProperty mapProperty) {
        return getMapProperty(mapProperty.getName());
    }

    /**
     * @since 1.0.RC4
     */
    protected Map getMapProperty(String key) {
        Object value = getProperty(key);
        if (value != null) {
            if (value instanceof Map) {
                return (Map) value;
            }
            String msg = "'" + key + "' property value type does not match the specified type. Specified type: Map. " +
                    "Existing type: " + value.getClass().getName();
            msg += (isPrintableProperty(key) ? ".  Value: " + value : ".");
            throw new IllegalArgumentException(msg);
        }
        return null;
    }

    protected <E extends Enum<E>> E getEnumProperty(EnumProperty<E> enumProperty) {
        return getEnumProperty(enumProperty.getName(), enumProperty.getType());
    }

    protected <E extends Enum<E>> E getEnumProperty(String key, Class<E> type) {
        Assert.notNull(type, "type cannot be null.");

        Object value = getProperty(key);

        if (value != null) {
            if (value instanceof String) {
                return Enum.valueOf(type, value.toString());
            }
            if (type.isAssignableFrom(value.getClass())) {
                //noinspection unchecked
                return (E) value;
            }
        }
        return null;
    }

    protected <T> T getObjectProperty(ObjectProperty<T> objectProperty) {
        return getObjectProperty(objectProperty.getName(), objectProperty.getType());
    }

    protected <T> T getObjectProperty(String name, Class<T> type) {

        Object value = getProperty(name);

        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (value instanceof Map) {
            Constructor<T> propertyConstructor = Classes.getConstructor(type, Map.class);
            try {
                T configurableProperty = propertyConstructor.newInstance(value);
                setProperty(name, configurableProperty, false);
                return configurableProperty;
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to create ", e);
            }
        }

        String msg = "'" + name + "' property value type does not match the specified type. Specified type: ConfigurableProperty. " +
                "Existing type: " + value.getClass().getName();

        throw new IllegalArgumentException(msg);
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
    protected abstract Object setProperty(String name, Object value, final boolean dirty);

}
