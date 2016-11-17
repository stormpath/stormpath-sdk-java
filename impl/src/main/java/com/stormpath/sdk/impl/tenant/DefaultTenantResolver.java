/*
 * Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantOptions;

/**
 * @since 1.2.0
 */
public class DefaultTenantResolver implements TenantResolver {

    private String currentTenantHref;
    private DataStore dataStore;

    public DefaultTenantResolver(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Tenant getCurrentTenant() {
        String href = currentTenantHref;
        if (href == null) {
            href = "/tenants/current";
        }
        Tenant current = this.dataStore.getResource(href, Tenant.class);
        this.currentTenantHref = current.getHref();
        return current;
    }

    @Override
    public Tenant getCurrentTenant(TenantOptions tenantOptions) {
        String href = currentTenantHref;
        if (href == null) {
            href = "/tenants/current";
        }

        Tenant current = this.dataStore.getResource(href, Tenant.class, tenantOptions);
        this.currentTenantHref = current.getHref();
        return current;
    }

}
