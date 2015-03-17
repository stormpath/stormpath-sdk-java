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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.query.Options;

/**
 * @since 0.8
 */
public interface TenantOptions<T> extends Options {

    T withApplications();

    T withApplications(int limit);

    T withApplications(int limit, int offset);

    T withDirectories();

    T withDirectories(int limit);

    T withDirectories(int limit, int offset);

    /**
     * Ensures that when retrieving a Tenant, the Tenant's {@link Tenant#getCustomData() customData} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     * @since 1.0.0
     */
    T withCustomData();

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getAccounts() accounts} are also retrieved in the same request.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the returned accounts, see the
     * {@link #withAccounts(int) withAccounts(limit)} or
     * {@link #withAccounts(int, int) withAccounts(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withAccounts();

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getAccounts() accounts} are also retrieved in the same request.
     *
     * @param limit the number of results in the accounts collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withAccounts(int limit);

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getAccounts() accounts} are also retrieved in the same request.
     *
     * @param limit the number of results in the accounts collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first Account to retrieve in the overall accounts collection's result set.
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withAccounts(int limit, int offset);

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getGroups() groups} are also retrieved in the same request.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the returned groups, see the
     * {@link #withGroups(int) withGroups(limit)} or
     * {@link #withGroups(int, int) withGroups(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withGroups();

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getGroups() groups} are also retrieved in the same request.
     *
     * @param limit the number of results in the groups collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withGroups(int limit);

    /**
     * Ensures that when retrieving a Tenant, the associated {@link Tenant#getGroups() groups} are also retrieved in the same request.
     *
     * @param limit the number of results in the groups collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first {@link Group} to retrieve in the overall groups collection's result set.
     * @return this instance for method chaining.
     * @since 1.0.RC4
     */
    T withGroups(int limit, int offset);

}
