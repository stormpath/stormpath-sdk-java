package com.stormpath.sdk.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.resource.ReferenceProperty;
import com.stormpath.sdk.resource.StatusProperty;
import com.stormpath.sdk.resource.StringProperty;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.8
 */
public final class Directories {

    public static final StringProperty NAME = new StringProperty("name", true);
    public static final StringProperty DESCRIPTION = new StringProperty("description");
    public static final StatusProperty STATUS = new StatusProperty("status");
    public static final ReferenceProperty<Tenant> TENANT = new ReferenceProperty<Tenant>("tenant", Tenant.class, true, false);
    public static final ReferenceProperty<Account> ACCOUNTS = new ReferenceProperty<Account>("accounts", Account.class, true, true);
    public static final ReferenceProperty<Group> GROUPS = new ReferenceProperty<Group>("groups", Group.class, true, true);

    public static DirectoryOptions options() {
        return (DirectoryOptions) Classes.newInstance("com.stormpath.sdk.impl.directory.DefaultDirectoryOptions");
    }

    public static DirectoryCriteria criteria() {
        return (DirectoryCriteria) Classes.newInstance("com.stormpath.sdk.impl.directory.DefaultDirectoryCriteria");
    }

    public static DirectoryCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }
}
