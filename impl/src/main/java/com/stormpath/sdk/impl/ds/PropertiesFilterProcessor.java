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
 * This interface defines the methods used to process the added {@link PropertiesFilter}s by calling their {@link
 * PropertiesFilter#filter(Class, Map)} method and returning the result of processing all configured filters.
 *
 * @see DefaultPropertiesFilterProcessor
 * @since 1.0.RC
 */
public interface PropertiesFilterProcessor<T extends Map<String,?>> {

    /**
     * Processes the filters based on the provided {@code clazz} type and {@code properties} parameters.
     *
     * @param clazz      the provided class type.
     * @param properties the properties {@link Map} to be filtered.
     * @return the filtered {@link Map}.
     */
    T process(Class clazz, T properties);

    List<PropertiesFilter<T>> getFilters();
}
