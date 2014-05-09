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

import com.stormpath.sdk.impl.ds.api.ApiKeyCachePropertiesFilter;
import com.stormpath.sdk.impl.ds.api.ApiKeyResourcePropertiesFilter;

import java.util.Map;

/**
 * <p>
 * This interface defines the method to filter resource properties
 * when some processing needs to happen before returning the
 * resource, usually from the data store.
 * </p>
 * <p>
 * Filters are usually processed from the {@link ResourcePropertiesFilterProcessor}.
 * </p>
 *
 * @see ApiKeyCachePropertiesFilter
 * @see ApiKeyResourcePropertiesFilter
 * @see ResourcePropertiesFilterProcessor
 * @since 1.1.beta
 */
public interface ResourcePropertiesFilter {

    /**
     * Filters the resource properties when some processing needs to happen before returning the
     * resource, usually from the data store.
     *
     * @param resourceProperties the resource properties to be filtered.
     *
     * @return the filtered properties maps.
     * @see ApiKeyCachePropertiesFilter#filter(Map)
     * @see ApiKeyResourcePropertiesFilter#filter(Map)
     * @see ResourcePropertiesFilterProcessor#process(Map)
     */
    Map<String, ?> filter(Map<String, ?> resourceProperties);
}
