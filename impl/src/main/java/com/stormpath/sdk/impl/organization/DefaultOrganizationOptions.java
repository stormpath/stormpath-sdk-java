/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.organization;

import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.organization.OrganizationOptions;

/**
 * @since 1.0.RC4.6
 */
public class DefaultOrganizationOptions extends DefaultOptions<OrganizationOptions> implements OrganizationOptions {

    public OrganizationOptions withDirectories() {
        return expand(DefaultOrganization.DIRECTORIES);
    }

    public OrganizationOptions withDirectories(int limit) {
        return expand(DefaultOrganization.DIRECTORIES);
    }

    public OrganizationOptions withDirectories(int limit, int offset) {
        return expand(DefaultOrganization.DIRECTORIES);
    }

    public OrganizationOptions withGroups() {
        return expand(DefaultOrganization.GROUPS);
    }

    public OrganizationOptions withGroups(int limit) {
        return expand(DefaultOrganization.GROUPS, limit);
    }

    public OrganizationOptions withGroups(int limit, int offset) {
        return expand(DefaultOrganization.GROUPS, limit, offset);
    }

    public OrganizationOptions withTenant() {
        return expand(DefaultOrganization.TENANT);
    }

    @Override
    public OrganizationOptions withCustomData() {
        return expand(DefaultOrganization.CUSTOM_DATA);
    }

    @Override
    public Object withAccounts() {
        return expand(DefaultOrganization.ACCOUNTS);
    }

    @Override
    public Object withAccounts(int limit) {
        return expand(DefaultOrganization.ACCOUNTS, limit);
    }

    @Override
    public Object withAccounts(int limit, int offset) {
        return expand(DefaultOrganization.ACCOUNTS, limit, offset);
    }
}
