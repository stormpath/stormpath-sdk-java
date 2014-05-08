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
 * @since 1.1.beta
 */
public class ResourcePropertiesFilterProcessor extends LinkedList<ResourcePropertiesFilter> {

    private final LinkedList<ResourcePropertiesFilter> transitoryFilters = new LinkedList<ResourcePropertiesFilter>();

    /**
     *
     * Calls {@link ResourcePropertiesFilter#filter(Map)} on all the existing filters, including the transitory ones
     * added by {@link #addTransitoryFilter(ResourcePropertiesFilter)}, and returns the result of calling all the filters.
     * </p>
     * A call to this method removes all transitory filters added by {@link #addTransitoryFilter(ResourcePropertiesFilter)}
     * after they are processed.
     * </p>
     *
     * @param resourceProperties the resource properties that will be filtered by the filters contained in this list.
     *
     * @return a {@link Map} containing the result of calling all the filters.
     */
    public Map<String, ?> process(Map<String, ?> resourceProperties) {

        Map<String, ?> result = null;
        for (ResourcePropertiesFilter filter : this) {

            result = filter.filter(resourceProperties);
        }

        for (ResourcePropertiesFilter filter : transitoryFilters) {

            result = filter.filter(resourceProperties);
        }

        transitoryFilters.clear();

        return result;
    }

    /**
     * Adds a {@link ResourcePropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Map)} method. After the next call to {@link #process(Map)}, all filters added by this method
     * will be removed.
     * </p>
     *
     * @param filter the {@link ResourcePropertiesFilter} that will only be executed in the next call to
     * the {@link #process(Map)} method. After the next call to {@link #process(Map)}, all filters added by this method
     * will be removed.
     */
    public void addTransitoryFilter(ResourcePropertiesFilter filter) {
        transitoryFilters.add(filter);
    }
}
