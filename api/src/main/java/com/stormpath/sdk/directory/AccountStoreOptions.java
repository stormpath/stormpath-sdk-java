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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.query.Options;

/**
 * Options that may be specified when retrieving Directory or
 * {@link com.stormpath.sdk.application.Application Application} resources.
 * <p/>
 * While a Directory is expected to be an account store, an Application can be perceived as one as well: it responds
 * to similar operations and merely delegates those operations to its associated login directories and/or groups.
 *
 * @see DirectoryOptions
 * @see com.stormpath.sdk.application.ApplicationOptions ApplicationOptions
 * @since 0.8
 */
public interface AccountStoreOptions<T> extends Options {

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.account.Account accounts}
     * are also retrieved in the same request (paginated).  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the
     * returned groups, see the {@link #withAccounts(int) withAccounts(limit)} or
     * {@link #withAccounts(int, int) withAccounts(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     */
    T withAccounts();

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.account.Account accounts} are
     * also retrieved in the same request (paginated), limiting the first page of account results to {@code limit} items.
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     *
     * @param limit the number of results in the Account collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withAccounts(int limit);

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.account.Account accounts} are
     * also retrieved in the same request (paginated) , with the first page of account results starting at the specified
     * {@code offset} index and limiting the number of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit  the number of results in the Account collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first Account to retrieve in the overall Account collection's result set.
     * @return this instance for method chaining.
     */
    T withAccounts(int limit, int offset);

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.group.Group groups}
     * are also retrieved in the same request (paginated).  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the
     * returned groups, see the {@link #withGroups(int) withGroups(limit)} or
     * {@link #withGroups(int, int) withGroups(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     */
    T withGroups();

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.group.Group groups} are also
     * retrieved in the same request (paginated), limiting the first page of group results to {@code limit} items.
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     *
     * @param limit the number of results in the Group collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withGroups(int limit);

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.group.Group groups} are also
     * retrieved in the same request (paginated) , with the first page of Group results starting at the specified
     * {@code offset} index and limiting the number of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit  the number of results in the Group collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first Group to retrieve in the overall Group collection's result set.
     * @return this instance for method chaining.
     */
    T withGroups(int limit, int offset);

    /**
     * Ensures that when retrieving the resource, the owning {@link com.stormpath.sdk.tenant.Tenant Tenant} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withTenant();
}
