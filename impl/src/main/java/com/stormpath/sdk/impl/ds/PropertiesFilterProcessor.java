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
package com.stormpath.sdk.impl.ds;

import java.util.Map;

/**
 * <p>
 *     This interface defines the methods used to process the added {@link PropertiesFilter}s by
 *     calling their {@link PropertiesFilter#filter(Class, Map)} method and returning
 *     the result of processing all configured filters.
 * </p>
 * @since 1.0.RC
 * @see DefaultPropertiesFilterProcessor
 */
public interface PropertiesFilterProcessor<T extends Map, F extends PropertiesFilter, C extends Class> {

    /**
     * Processes the filters based on the provided {@code clazz} and {@code properties} parameters.
     *
     * @param clazz the provided class.
     * @param properties the properties {@link Map} to be filtered.
     * @return the filtered {@link Map}.
     */
    T process(C clazz, T properties);

    /**
     * Adds a {@link PropertiesFilter} to be processed every time the {@link #process(Class, Map)} method is called.
     *
     * @param propertiesFilter the filter to be added.
     */
    void add(F propertiesFilter);

    /**
     * <p>
     * Adds a {@link PropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Class,Map)} method. After the next call to {@link #process(Class,Map)}, all filters added by this method
     * will be removed.
     * </p>
     *
     * @param propertiesFilter the {@link PropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Class,Map)} method. After the next call to {@link #process(Class,Map)}, all filters added by this method
     * will be removed.
     */
    void addTransitoryFilter(F propertiesFilter);
}
