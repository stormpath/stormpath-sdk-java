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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * A group is a uniquely-named collection of {@link Account}s within a {@link Directory}.
 *
 * @since 0.2
 */
public interface Group extends Resource, Saveable, Iterable<Account> {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Status getStatus();

    void setStatus(Status status);

    Tenant getTenant();

    Directory getDirectory();

    /**
     * Returns a paginated list of all accounts in the group.
     * <p/>
     * Tip: If this list might be large, instead of iterating over all accounts, it might be more convenient (and
     * practical) to execute a search for one or more of the group's accounts using the
     * {@link #getAccounts(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all accounts in the group.
     * @see #getAccounts(java.util.Map)
     */
    AccountList getAccounts();

    /**
     * Returns a paginated list of accounts in the group that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../groups/groupId/accounts?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of accounts in the group that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of accounts in the group that match the specified query criteria.
     * <p/>
     * The {@link com.stormpath.sdk.account.Accounts Accounts} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * group.list(Accounts
     *     .where(Accounts.description().containsIgnoreCase("foo"))
     *     .and(Accounts.name().startsWithIgnoreCase("bar"))
     *     .orderBySurname()
     *     .orderByGivenName().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of accounts in the group that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(AccountCriteria criteria);

    /**
     * Returns a paginated list of all of the memberships in which this group participates.
     *
     * @return a paginated list of all memberships in which this group participates.
     * @since 0.8
     */
    GroupMembershipList getAccountMemberships();

    /**
     * @since 0.4
     */
    GroupMembership addAccount(Account account);
}
