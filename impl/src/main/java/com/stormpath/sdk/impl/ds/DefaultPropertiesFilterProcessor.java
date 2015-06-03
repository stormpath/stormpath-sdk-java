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
package com.stormpath.sdk.impl.ds;

import java.util.List;
import java.util.Map;

/**
 * This class is used to process the added {@link PropertiesFilter}s by calling their {@link
 * PropertiesFilter#filter(Class, Map)} method and returning the result of processing all configured filters. </p>
 *
 * @see PropertiesFilter
 * @since 1.0.RC
 */
public class DefaultPropertiesFilterProcessor<T extends Map<String,?>> implements PropertiesFilterProcessor<T> {

    private final List<PropertiesFilter<T>> filters;

    public DefaultPropertiesFilterProcessor(List<PropertiesFilter<T>> filters) {
        this.filters = java.util.Collections.unmodifiableList(filters);
    }

    /**
     * Calls {@link PropertiesFilter#filter(Class, Map)} on all the existing filters and returns the result of
     * calling all the filters.
     *
     * @param clazz              the provided class type.
     * @param properties the resource properties that will be filtered by the added filters contained.
     * @return a {@link Map} containing the result of calling all the filters.
     * @see PropertiesFilter#filter(Class, Map)
     */
    @Override
    public T process(Class clazz, T properties) {

        if (properties == null) {
            return null;
        }

        T result = properties;
        for (PropertiesFilter<T> filter : filters) {
            result = filter.filter(clazz, result);
        }

        return result;
    }

    @Override
    public List<PropertiesFilter<T>> getFilters() {
        return this.filters;
    }
}
