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
