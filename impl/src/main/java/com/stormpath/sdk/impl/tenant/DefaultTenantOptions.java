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
import com.stormpath.sdk.tenant.Tenants;

/**
 * @since 0.8
 */
public class DefaultTenantOptions extends DefaultOptions<TenantOptions> implements TenantOptions<TenantOptions> {

    @Override
    public TenantOptions expandApplications() {
        return expand(Tenants.APPLICATIONS);
    }

    @Override
    public TenantOptions expandApplications(int limit) {
        return expand(Tenants.APPLICATIONS, limit);
    }

    @Override
    public TenantOptions expandApplications(int limit, int offset) {
        return expand(Tenants.APPLICATIONS, limit, offset);
    }

    @Override
    public TenantOptions expandDirectories() {
        return expand(Tenants.DIRECTORIES);
    }

    @Override
    public TenantOptions expandDirectories(int limit) {
        return expand(Tenants.DIRECTORIES, limit);
    }

    @Override
    public TenantOptions expandDirectories(int limit, int offset) {
        return expand(Tenants.DIRECTORIES, limit, offset);
    }
}
