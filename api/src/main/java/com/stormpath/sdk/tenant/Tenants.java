package com.stormpath.sdk.tenant;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.ReferenceProperty;
import com.stormpath.sdk.resource.StringProperty;

/**
 * @since 0.8
 */
public final class Tenants {

    public static final StringProperty NAME = new StringProperty("name", true);
    public static final StringProperty KEY = new StringProperty("key", true);
    public static final ReferenceProperty<Application> APPLICATIONS = new ReferenceProperty<Application>("applications", Application.class, true, true);
    public static final ReferenceProperty<Directory> DIRECTORIES = new ReferenceProperty<Directory>("directories", Directory.class, true, true);

    //prevent instantiation
    private Tenants() {
    }

    public static TenantOptions options() {
        return (TenantOptions) Classes.newInstance("com.stormpath.sdk.impl.tenant.DefaultTenantOptions");
    }
}
