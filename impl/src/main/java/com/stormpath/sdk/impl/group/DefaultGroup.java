package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultGroup extends AbstractInstanceResource implements Group {

    private static String NAME = "name";
    private static String DESCRIPTION = "description";
    private static String STATUS = "status";
    private static String TENANT = "tenant";
    private static String DIRECTORY = "directory";
    private static String ACCOUNTS = "accounts";

    public DefaultGroup(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroup(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getStringProperty(NAME);
    }

    @Override
    public void setName(String name) {
        setProperty(NAME, name);
    }

    @Override
    public String getDescription() {
        return getStringProperty(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

    @Override
    public Status getStatus() {
        String value = getStringProperty(STATUS);
        if (value == null) {
            return null;
        }
        return Status.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(Status status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT, Tenant.class);
    }

    @Override
    public Directory getDirectory() {
        return getResourceProperty(DIRECTORY, Directory.class);
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS, AccountList.class);
    }

    @Override
    public void addAccount(Account account) {
        GroupMembership groupMembership = getDataStore().instantiate(GroupMembership.class);
        groupMembership.create(account, this);
    }
}
