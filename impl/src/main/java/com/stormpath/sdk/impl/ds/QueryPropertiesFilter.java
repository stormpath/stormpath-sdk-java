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

import com.stormpath.sdk.impl.ds.api.ApiKeyQueryPropertiesFilter;
import com.stormpath.sdk.impl.http.QueryString;

/**
 * <p>
 * This interface defines the method to filter query string
 * when some processing needs to happen before making a
 * query to the server, usually from the data store.
 * </p>
 * <p>
 * PropertiesFilter are usually processed from the {@link PropertiesFilterProcessor}.
 * </p>
 * @since 1.1.beta
 * @see PropertiesFilterProcessor
 * @see ApiKeyQueryPropertiesFilter
 */
public interface QueryPropertiesFilter extends PropertiesFilter<Class, QueryString> {

    /**
     * Filters the resource properties when some processing needs to happen before
     * making a query to the server, usually from the data store.
     *
     * @param queryString the query string to be filtered.
     * @return the filtered query string
     * @see ApiKeyQueryPropertiesFilter#filter(Class, QueryString)
     */
    QueryString filter(Class clazz, QueryString queryString);
}
