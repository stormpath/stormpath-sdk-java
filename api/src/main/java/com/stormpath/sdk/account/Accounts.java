package com.stormpath.sdk.account;

import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.resource.ReferenceProperty;
import com.stormpath.sdk.resource.StatusProperty;
import com.stormpath.sdk.resource.StringProperty;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.8
 */
public final class Accounts {

    public static final StringProperty EMAIL = new StringProperty("email", true);
    public static final StringProperty USERNAME = new StringProperty("username", true);
    //password is a property, but this should never be used for searches, so we leave it out on purpose
    public static final StringProperty GIVEN_NAME = new StringProperty("givenName", true);
    public static final StringProperty MIDDLE_NAME = new StringProperty("middleName");
    public static final StringProperty SURNAME = new StringProperty("surname", true);
    public static final StringProperty FULL_NAME = new StringProperty("fullName"); //computed property, can't set it or query based on it
    public static final StatusProperty STATUS = new StatusProperty("status");
    public static final ReferenceProperty<Directory> DIRECTORY = new ReferenceProperty<Directory>("directory", Directory.class, true, false);
    public static final ReferenceProperty<Tenant> TENANT = new ReferenceProperty<Tenant>("tenant", Tenant.class, true, false);
    public static final ReferenceProperty<Group> GROUPS = new ReferenceProperty<Group>("groups", Group.class, true, true);
    public static final ReferenceProperty<GroupMembership> GROUP_MEMBERSHIPS = new ReferenceProperty<GroupMembership>("groupMemberships", GroupMembership.class, true, true);

    public static AccountOptions options() {
        return (AccountOptions) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountOptions");
    }

    public static AccountCriteria criteria() {
        return (AccountCriteria) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountCriteria");
    }

    public static AccountCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }
}
