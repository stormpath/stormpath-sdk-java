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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @since 1.1.beta
 */
public class ResourcePropertiesFilterProcessor extends LinkedList<ResourcePropertiesFilter> {

    private final LinkedList<ResourcePropertiesFilter> transitoryFilters = new LinkedList<ResourcePropertiesFilter>();

    public ResourcePropertiesFilterProcessor() {
        add(new ResourcePropertiesFilter() {
            @Override
            public Map<String, ?> filter(Map<String, ?> resourceProperties) {
                return resourceProperties;
            }
        });
    }

    public ResourcePropertiesFilterProcessor(Collection<? extends ResourcePropertiesFilter> c) {
        super(c);
        add(new ResourcePropertiesFilter() {
            @Override
            public Map<String, ?> filter(Map<String, ?> resourceProperties) {
                return resourceProperties;
            }
        });
    }

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

    public void addTransitoryFilter(ResourcePropertiesFilter filter) {
        transitoryFilters.add(filter);
    }
}
