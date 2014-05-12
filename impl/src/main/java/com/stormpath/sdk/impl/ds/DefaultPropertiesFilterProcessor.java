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

import java.util.LinkedList;
import java.util.Map;

/**
 * <p>
 *     This class is used to process the added {@link PropertiesFilter}s by
 *     calling their {@link PropertiesFilter#filter(Class, Map)} method and returning
 *     the result of processing all configured filters.
 * </p>
 * @since 1.0.RC
 * @see PropertiesFilter
 */
public class DefaultPropertiesFilterProcessor implements PropertiesFilterProcessor<Map<String, ?>, PropertiesFilter, Class>{

    private final LinkedList<PropertiesFilter> filters = new LinkedList<PropertiesFilter>();
    private final LinkedList<PropertiesFilter> transitoryFilters = new LinkedList<PropertiesFilter>();

    /**
     * <p>
     * Calls {@link PropertiesFilter#filter(Class, Map)} on all the existing filters, including the transitory ones
     * added by {@link #addTransitoryFilter(PropertiesFilter)}, and returns the result of calling all the filters.
     * </p>
     * <p>
     * A call to this method removes all transitory filters added by {@link #addTransitoryFilter(PropertiesFilter)}
     * after they are processed.
     * </p>
     *
     * @param clazz the provided class type.
     * @param resourceProperties the resource properties that will be filtered by the added filters contained.
     *
     * @return a {@link Map} containing the result of calling all the filters.
     * @see PropertiesFilter#filter(Class, Map)
     * @see #addTransitoryFilter(PropertiesFilter)
     */
    @Override
    public Map<String, ?> process(Class clazz,  Map<String, ?> resourceProperties) {

        Map<String, ?> result = resourceProperties;
        for (PropertiesFilter filter : filters) {

            result = filter.filter(clazz, resourceProperties);
        }

        for (PropertiesFilter filter : transitoryFilters) {

            result = filter.filter(clazz, resourceProperties);
        }

        transitoryFilters.clear();

        return result;
    }

    /**
     * <p>
     * Adds a {@link PropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Class, Map)} method. After the next call to {@link #process(Class,Map)}, all filters added by this method
     * will be removed.
     * </p>
     *
     * @param filter the {@link PropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Class, Map)} method. After the next call to {@link #process(Class,Map)}, all filters added by this method
     * will be removed.
     *
     * @see #process(Class,Map)
     */
    public void addTransitoryFilter(PropertiesFilter filter) {
        transitoryFilters.add(filter);
    }

    @Override
    public void add(PropertiesFilter propertiesFilter) {
        filters.add(propertiesFilter);
    }
}
