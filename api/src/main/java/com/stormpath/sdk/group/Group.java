/*
 * Copyright 2014 Stormpath, Inc.
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
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * A group is a uniquely-named collection of {@link Account}s within a {@link Directory}.
 *
 * @since 0.2
 */
public interface Group extends Resource, Saveable, Deletable, AccountStore, Extendable, Auditable {

    /**
     * Returns the group's name, guaranteed to be unique for all groups within a Directory.
     *
     * @return the group's name, guaranteed to be unique for all groups within a Directory.
     */
    String getName();

    /**
     * Sets the group's name, which must be unique among all other groups within a Directory.
     * </p>
     * An attempt to set a name that is in use when creating or saving the group will result in a
     * {@link com.stormpath.sdk.error.Error Error}
     *
     * @param name the group's name, which must be unique among all other groups within a Directory.
     * @return this instance for method chaining.
     */
    Group setName(String name);

    /**
     * Returns the group's description.  This is an optional property and may be null or empty.
     *
     * @return the group's description.  This is an optional property and may be null or empty.
     */
    String getDescription();

    /**
     * Sets the group's description.  This is an optional property and may be null or empty.
     *
     * @param description the group's description.  This is an optional property and may be null or empty.
     * @return this instance for method chaining.
     */
    Group setDescription(String description);

    /**
     * Returns the Group's status.  If a group is mapped to an Application as an Account Store (for login purposes),
     * and the Group is disabled, the accounts within that Group cannot login to the application.  Accounts in enabled
     * Groups mapped to an Application may login to that application.
     *
     * @return the Group's status
     */
    GroupStatus getStatus();

    /**
     * Sets the Group's status.  If a group is mapped to an Application as an Account Store (for login purposes),
     * and the Group is disabled, the accounts within that Group cannot login to the application.  Accounts in enabled
     * Groups mapped to an Application may login to that application.
     *
     * @param status the Group's status.
     * @return this instance for method chaining.
     */
    Group setStatus(GroupStatus status);

    /**
     * Returns the Stormpath Tenant that owns this Group resource.
     *
     * @return the Stormpath Tenant that owns this Group resource.
     */
    Tenant getTenant();

    /**
     * Returns the group's parent Directory (where the group is stored).
     *
     * @return the group's parent Directory (where the group is stored)
     */
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
     * Returns a paginated list of this group's accounts that match the specified query criteria.
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
     * Assigns the specified Account to this Group.
     * <p/>
     * <b>Immediate Execution:</b> Unlike other Group methods, you do <em>not</em> need to call {@link #save()} afterwards.
     * This method will interact with the server immediately.
     *
     * @return the new GroupMembership resource created reflecting the group-to-account association.
     * @since 0.4
     */
    GroupMembership addAccount(Account account);

    /**
     * Saves this Group resource and ensures the returned Group response reflects the specified options.  This
     * enhances performance by 'piggybacking' the response to return related resources you know you will use after
     * saving the group.
     *
     * @param responseOptions The {@code GroupOptions} to use to customize the Group resource returned in the save response.
     * @return this instance for method chaining.
     * @since 0.9
     */
    Group saveWithResponseOptions(GroupOptions responseOptions);
}
