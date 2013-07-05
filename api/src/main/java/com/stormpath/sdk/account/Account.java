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
package com.stormpath.sdk.account;

import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.group.GroupMembershipList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * An Account is a unique identity within a {@link Directory}.
 *
 * @since 0.1
 */
public interface Account extends Resource, Saveable {

    String getUsername();

    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    void setPassword(String password);

    String getGivenName();

    void setGivenName(String givenName);

    String getMiddleName();

    void setMiddleName(String middleName);

    String getSurname();

    void setSurname(String surname);

    Status getStatus();

    void setStatus(Status status);

    /**
     * Returns a paginated list of the account's assigned groups.
     * <p/>
     * Tip: If this list might be large, instead of iterating over all groups, it might be more convenient (and
     * practical) to execute a search for one or more of the account's groups using the
     * {@link #list(com.stormpath.sdk.group.GroupCriteria)} or {@link #listGroups(java.util.Map)} methods instead of
     * this one.
     *
     * @return a paginated list of all groups assigned to the Account.
     * @see #list(com.stormpath.sdk.group.GroupCriteria)
     * @see #listGroups(java.util.Map)
     */
    GroupList getGroups();

    /**
     * Returns a paginated list of the account's assigned groups that match the specified query criteria.
     * <p/>
     * This method is mostly provided as a non-type-safe alternative to the
     * {@link #list(com.stormpath.sdk.group.GroupCriteria)} method which might be useful in dynamic languages on the
     * JVM (for example, with Groovy):
     * <pre>
     * return account.getGroups([description: '*foo*', orderBy: 'name desc', limit: 50]);
     * </pre>
     * The query parameter names and values must be equal to those documented in the Stormpath REST API product guide.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../accounts/accountId/groups?param1=value1&param2=value2&...
     * </pre>
     * <p/>
     * If in doubt, use {@link #list(com.stormpath.sdk.group.GroupCriteria)} as all possible query options are available
     * via type-safe guarantees that can be auto-completed by most IDEs.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the account's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList listGroups(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the account's groups that match the specified query criteria.  The
     * {@link com.stormpath.sdk.group.Groups Groups} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * account.list(Groups
     *     .where(Groups.DESCRIPTION.icontains("foo"))
     *     .and(Groups.NAME.iStartsWith("bar"))
     *     .orderByName().descending()
     *     .orderBySurname().ascending()
     *     .expandAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the account's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList list(GroupCriteria criteria);

    /**
     * Returns the account's parent Directory (where the account is stored).
     *
     * @return the account's parent Directory (where the account is stored)
     */
    Directory getDirectory();

    /* NOT YET ENABLED (focusing on collection searches first)
     *
     * Returns the account's parent Directory according to the specified options (providing for resource
     * expansion).
     * <p/>
     * The {@link com.stormpath.sdk.directory.Directories Directories} utility class is available to help construct
     * the options DSL.  For example:
     * <pre>
     * account.getDirectory(Directories.options().expandAccounts(50, 100));
     * </pre>
     *
     * @param options the retrieval options to use when performing a request to acquire the Directory.
     * @return the account's Directory.
     * @since 0.8
     *
    Directory getDirectory(DirectoryOptions options);
    */

    Tenant getTenant();

    /**
     * @since 0.4
     */
    GroupMembershipList getGroupMemberships();

    /**
     * @since 0.4
     */
    GroupMembership addGroup(Group group);

    EmailVerificationToken getEmailVerificationToken();
}
