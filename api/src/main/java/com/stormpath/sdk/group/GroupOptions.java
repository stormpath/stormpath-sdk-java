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
package com.stormpath.sdk.group;

import com.stormpath.sdk.query.Options;

/**
 * Group-specific options that may be specified when retrieving {@link Group} resources.
 *
 * @since 0.8
 */
public interface GroupOptions<T> extends Options {

    /**
     * Ensures that when retrieving a Group, the Group's  {@link Group#getCustomData() customData} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withCustomData();

    /**
     * Ensures that when retrieving a Group, the Group's parent {@link Group#getDirectory() directory} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withDirectory();

    /**
     * Ensures that when retrieving a Group, the Group's owning {@link Group#getTenant()} tenant} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withTenant();

    /**
     * Ensures that when retrieving a Group, the Groups's assigned {@link Group#getAccounts()} accounts}
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
     * Ensures that when retrieving a Group, the Group's assigned {@link Group#getAccounts()} accounts} are also
     * retrieved in the same request (paginated), limiting the first page of account results to {@code limit} items.
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     *
     * @param limit the number of results in the Account collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withAccounts(int limit);

    /**
     * Ensures that when retrieving a Group, the Group's assigned {@link Group#getAccounts()} accounts} are also
     * retrieved in the same request (paginated) , with the first page of account results starting at the specified
     * {@code offset} index and limiting the number of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit  the number of results in the Account collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first Account to retrieve in the overall Account collection's result set.
     * @return this instance for method chaining.
     */
    T withAccounts(int limit, int offset);

    /**
     * Ensures that when retrieving a Group, the Group's associated
     * {@link Group#getAccountMemberships() accountMemberships} are also retrieved in the same request (paginated).
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     * <p/>
     * If you wish to control
     * pagination parameters (offset and limit) for the returned accountMemberships, see the
     * {@link #withAccountMemberships(int) withAccountMemberships(limit)} or
     * {@link #withAccountMemberships(int, int) withAccountMemberships(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     */
    T withAccountMemberships();

    /**
     * Ensures that when retrieving a Group, the Group's associated
     * {@link Group#getAccountMemberships()} accountMemberships} are also retrieved in the same request (paginated),
     * limiting the first page of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit the number of results in the membership collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withAccountMemberships(int limit);

    /**
     * Ensures that when retrieving a Group, the Group's associated
     * {@link Group#getAccountMemberships()} accountMemberships} are also retrieved in the same request (paginated), with
     * the first page of results starting at the specified {@code offset} index and limiting the number
     * of results to {@code limit} items.  This enhances performance by leveraging a single request to retrieve
     * multiple related resources you know you will use.
     *
     * @param limit  the number of results in the membership collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first membership to retrieve in the overall membership collection's
     *               result set.
     * @return this instance for method chaining.
     */
    T withAccountMemberships(int limit, int offset);
}