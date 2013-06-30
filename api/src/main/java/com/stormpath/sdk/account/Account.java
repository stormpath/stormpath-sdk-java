/*
 * Copyright 2012 Stormpath, Inc.
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
     * {@link #getGroups(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all groups assigned to the Account.
     * @see #getGroups(java.util.Map)
     */
    GroupList getGroups();

    /**
     * Returns a paginated list of the account's assigned groups that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../accounts/accountId/groups?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the account's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList getGroups(Map<String, Object> queryParams);

    Directory getDirectory();

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
