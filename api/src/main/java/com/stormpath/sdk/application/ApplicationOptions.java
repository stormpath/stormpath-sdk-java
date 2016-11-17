/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.application;

import com.stormpath.sdk.directory.AccountStoreOptions;

/**
 * Application-specific options that may be specified when retrieving {@link Application} resources.
 *
 * @since 0.8
 */
public interface ApplicationOptions<T> extends AccountStoreOptions<T> {

    /**
     * Ensures that when retrieving the resource, its associated {@link ApplicationAccountStoreMapping accountStoreMappings}
     * are also retrieved in the same request (paginated).  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the
     * returned accountStoreMappings, see the {@link #withAccountStoreMappings(int) withAccountStoreMappings(limit)} or
     * {@link #withAccountStoreMappings(int, int) withAccountStoreMappings(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     */
    T withAccountStoreMappings();

    /**
     * Ensures that when retrieving the resource, its associated {@link ApplicationAccountStoreMapping accountStoreMappings} are also
     * retrieved in the same request (paginated), limiting the first page of ApplicationAccountStoreMapping results to {@code limit} items.
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     *
     * @param limit the number of results in the ApplicationAccountStoreMappings collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withAccountStoreMappings(int limit);

    /**
     * Ensures that when retrieving the resource, its associated {@link ApplicationAccountStoreMapping accountStoreMappings} are also
     * retrieved in the same request (paginated) , with the first page of ApplicationAccountStoreMapping results starting at the specified
     * {@code offset} index and limiting the number of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit  the number of results in the ApplicationAccountStoreMapping collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first ApplicationAccountStoreMapping to retrieve in the overall ApplicationAccountStoreMapping collection's result set.
     * @return this instance for method chaining.
     */
    T withAccountStoreMappings(int limit, int offset);

    /**
     * Ensures that when retrieving an Application, the Application's {@link Application#getCustomData() customData} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     * @since 1.0.0
     */
    T withCustomData();

    /**
     * Ensures that when retrieving an Application, the Application's {@link Application#getWebConfig() webConfiguration}
     * is also retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     * @since 1.0.0
     */
    T withWebConfig();

    //todo: saml javadoc
    T withSamlPolicy();
}
