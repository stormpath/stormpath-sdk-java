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
import com.stormpath.sdk.impl.ds.api.ApiKeyQueryPropertiesFilter;
import com.stormpath.sdk.impl.ds.api.ApiKeyResourcePropertiesFilter;

import java.util.Map;

/**
 * <p>
 *     This interface defines the method required to filter the provided {@link Map} properties.
 * </p>
 * <p>
 * Filters are usually processed from the {@link PropertiesFilterProcessor}.
 * </p>
 * @since 1.0.RC
 *
 * @see PropertiesFilterProcessor
 * @see ApiKeyCachePropertiesFilter
 * @see ApiKeyResourcePropertiesFilter
 * @see ApiKeyQueryPropertiesFilter
 * @see QueryPropertiesFilter
 */
public interface PropertiesFilter<C extends Class,  T extends Map> {

    /**
     * Filters the provided {@code properties} {@link Map} and returns the filtered {@link Map}.
     *
     * @param properties the {@link Map} to be filtered.
     * @param clazz the class type of the class holding the properties.
     *
     * @return the filtered {@link Map}.
     *
     * @see ApiKeyCachePropertiesFilter#filter(Class, Map)
     * @see ApiKeyResourcePropertiesFilter#filter(Class, Map)
     * @see ApiKeyQueryPropertiesFilter#filter(Class, Map)
     * @see QueryPropertiesFilter#filter(Class, Map)
     */
    T filter(C clazz, T properties);
}
