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
package com.stormpath.sdk.impl.tenant;

import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.tenant.TenantOptions;

/**
 * @since 0.8
 */
public class DefaultTenantOptions extends DefaultOptions<TenantOptions> implements TenantOptions<TenantOptions> {

    @Override
    public TenantOptions withApplications() {
        return expand(DefaultTenant.APPLICATIONS);
    }

    @Override
    public TenantOptions withApplications(int limit) {
        return expand(DefaultTenant.APPLICATIONS, limit);
    }

    @Override
    public TenantOptions withApplications(int limit, int offset) {
        return expand(DefaultTenant.APPLICATIONS, limit, offset);
    }

    @Override
    public TenantOptions withDirectories() {
        return expand(DefaultTenant.DIRECTORIES);
    }

    @Override
    public TenantOptions withDirectories(int limit) {
        return expand(DefaultTenant.DIRECTORIES, limit);
    }

    @Override
    public TenantOptions withDirectories(int limit, int offset) {
        return expand(DefaultTenant.DIRECTORIES, limit, offset);
    }

    /**
     * @since 1.0.0
     */
    @Override
    public TenantOptions withCustomData() {
        return expand(DefaultTenant.CUSTOM_DATA);
    }

    @Override
    public TenantOptions withAccounts() {
        return expand(DefaultTenant.ACCOUNTS);
    }

    @Override
    public TenantOptions withAccounts(int limit) {
        return expand(DefaultTenant.ACCOUNTS, limit);
    }

    @Override
    public TenantOptions withAccounts(int limit, int offset) {
        return expand(DefaultTenant.ACCOUNTS, limit, offset);
    }

    @Override
    public TenantOptions withGroups() {
        return expand(DefaultTenant.GROUPS);
    }

    @Override
    public TenantOptions withGroups(int limit) {
        return expand(DefaultTenant.GROUPS, limit);
    }

    @Override
    public TenantOptions withGroups(int limit, int offset) {
        return expand(DefaultTenant.GROUPS, limit, offset);
    }

    @Override
    public TenantOptions withOrganizations() {
        return expand(DefaultTenant.GROUPS);
    }

    @Override
    public TenantOptions withOrganizations(int limit) {
        return expand(DefaultTenant.GROUPS, limit);
    }

    @Override
    public TenantOptions withOrganizations(int limit, int offset) {
        return expand(DefaultTenant.GROUPS, limit, offset);
    }
}
