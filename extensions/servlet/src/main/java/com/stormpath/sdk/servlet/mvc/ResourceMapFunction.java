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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A function that accepts a {@link Resource} and converts it to a Map&lt;String,Object&gt; instance suitable for
 * serialization, likely to JSON.
 *
 * @since 1.1.0
 */
public class ResourceMapFunction<T extends Resource> implements Function<T, Map<String, Object>> {

    private static final Logger log = LoggerFactory.getLogger(ResourceMapFunction.class);

    private Collection<String> excludedFields = java.util.Collections.emptySet();
    private Collection<String> includedFields = java.util.Collections.emptySet();

    public void setExcludedFields(Collection<String> excludedFields) {
        this.excludedFields = excludedFields == null ? java.util.Collections.<String>emptySet() : excludedFields;
    }

    public Collection<String> getExcludedFields() {
        return excludedFields;
    }

    public Collection<String> getIncludedFields() {
        return includedFields;
    }

    public void setIncludedFields(Collection<String> includedFields) {
        this.includedFields = includedFields == null ? java.util.Collections.<String>emptySet() : includedFields;
    }

    @Override
    public Map<String, Object> apply(T t) {
        return toMap(t);
    }

    @SuppressWarnings("RedundantIfStatement") //for readability
    private boolean include(String propName) {

        if (propName.equals("password")) { //don't expose sensitive data
            return false;
        }

        if (getIncludedFields().contains(propName)) {
            return true;
        }

        if (getExcludedFields().contains(propName)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("SimplifiableIfStatement") //for readability
    private boolean include(String propName, Object propValue) {

        if (propName.equals("password")) { //don't expose sensitive data
            return false;
        }

        if (getIncludedFields().contains(propName)) {
            return true;
        }

        if (getExcludedFields().contains(propName)) {
            return false;
        }

        return !(propValue instanceof Resource) || propValue instanceof CustomData;
    }

    public Map<String, Object> toMap(Object o) {

        Assert.notNull(o, "Resource object cannot be null");
        Assert.isInstanceOf(AbstractResource.class, o, "Object must be an instance of " + AbstractResource.class.getName());
        AbstractResource resource = (AbstractResource) o;

        if (o instanceof CustomData) {

            CustomData cd = (CustomData) o;

            //force data load before copying:
            cd.getCreatedAt();

            return deepCopy((CustomData) o); //copy the source - don't modify it
        }

        Map<String, Object> props = new LinkedHashMap<>();

        for (String propName : resource.getPropertyNames()) {

            if (!include(propName)) {
                continue;
            }

            Object propValue;

            try {
                final Class<? extends AbstractResource> resourceClass = resource.getClass();
                final String methodName = "get" + Strings.capitalize(propName);
                Method method = resourceClass.getMethod(methodName);
                propValue = method.invoke(resource);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                if (log.isWarnEnabled()) {
                    String msg = "Unable to access account property '" + propName + "': " + e.getMessage();
                    log.warn(msg, e);
                }
                continue;
            }

            if (!include(propName, propValue)) {
                continue;
            }

            if (propValue instanceof CollectionResource) {

                CollectionResource collectionResource = (CollectionResource) propValue;
                List<Object> list = new ArrayList<>();

                for (Object cr : collectionResource) {
                    Map<String, Object> val = toMap(cr);
                    list.add(val);
                }

                propValue = list;

            } else if (propValue instanceof AbstractResource) {
                propValue = toMap(propValue);
            }

            props.put(propName, propValue);
        }

        return props;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deepCopy(Map<String, Object> src) {

        Map<String, Object> dst = new LinkedHashMap<>();

        for (String name : src.keySet()) {
            Object value = src.get(name);
            if (value instanceof Map) {
                value = deepCopy((Map) value);
            } else if (value instanceof Collection) {
                value = deepCopy((Collection) value);
            }
            dst.put(name, value);
        }

        return dst;
    }

    @SuppressWarnings("unchecked")
    private Collection deepCopy(Collection src) {

        Collection dst = src instanceof List ? new ArrayList() : new LinkedHashSet();

        for (Object value : src) {
            if (value instanceof Map) {
                value = deepCopy((Map) value);
            } else if (value instanceof Collections) {
                value = deepCopy((Collection) value);
            }
            dst.add(value);
        }

        return dst;
    }
}
