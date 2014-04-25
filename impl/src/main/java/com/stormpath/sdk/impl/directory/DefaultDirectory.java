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
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryStatus;
import com.stormpath.sdk.group.*;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.oauth.IdentityProviderType;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.Provider;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultDirectory extends AbstractInstanceResource implements Directory {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StatusProperty<DirectoryStatus> STATUS = new StatusProperty<DirectoryStatus>(DirectoryStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<Provider> PROVIDER = new ResourceReference<Provider>("provider", Provider.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group> GROUPS =
            new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, TENANT, PROVIDER, ACCOUNTS, GROUPS);

    public DefaultDirectory(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultDirectory(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public void setName(String name) {
        setProperty(NAME, name);
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

    @Override
    public DirectoryStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return DirectoryStatus.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(DirectoryStatus status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public void createAccount(Account account) {
        Assert.notNull(account, "account cannot be null.");
        createAccount(Accounts.newCreateRequestFor(account).build());
    }

    @Override
    public void createAccount(Account account, boolean registrationWorkflowEnabled) {
        Assert.notNull(account, "account cannot be null.");
        createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(registrationWorkflowEnabled).build());
    }

    @Override
    public void createAccount(CreateAccountRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Account account = request.getAccount();
        String href = getAccounts().getHref();

        if (request.isRegistrationWorkflowOptionSpecified()) {
            href += "?registrationWorkflowEnabled=" + request.isRegistrationWorkflowEnabled();
        }

        if (request.isAccountOptionsSpecified()) {
            getDataStore().create(href, account, request.getAccountOptions());
            return;
        }

        getDataStore().create(href, account);
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS);
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        AccountList list = getAccounts(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, queryParams);
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        AccountList list = getAccounts(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, criteria);
    }

    @Override
    public GroupList getGroups() {
        return getResourceProperty(GROUPS);
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        GroupList list = getGroups(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), GroupList.class, queryParams);
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        GroupList list = getGroups(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), GroupList.class, criteria);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    /**
     * @since 0.6
     */
    @Override
    public void createGroup(Group group) {
        Assert.notNull(group, "Group cannot be null.");
        createGroup(Groups.newCreateRequestFor(group).build());
    }

    @Override
    public void createGroup(CreateGroupRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Group group = request.getGroup();
        String href = getGroups().getHref();
        if (request.isGroupOptionsSpecified()) {
            getDataStore().create(href, group, request.getGroupOptions());
            return;
        }
        getDataStore().create(href, group);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    /**
     * @since 0.9
     */
    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @since 1.0.alpha
     */
    @Override
    public Provider getProvider() {
        Object value = getProperty(PROVIDER.getName());

        if (Provider.class.isInstance(value) || value == null) {
            return (Provider) value;
        }
        if (value instanceof Map && !((Map) value).isEmpty()) {
            String href = (String) ((Map) value).get(HREF_PROP_NAME);

            if (href == null) {
                throw new IllegalStateException("provider resource does not contain its required href property.");
            }

            Provider provider = getDataStore().getResource(href, Provider.class, "providerId", IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP);
            setProvider(provider);
            return provider;
        }

        String msg = "'" + PROVIDER.getName() + "' property value type does not match the specified type. Specified type: " +
                PROVIDER.getType() + ".  Existing type: " + value.getClass().getName() + ".  Value: " + value;
        throw new IllegalStateException(msg);
    }

    /**
     * @since 1.0.alpha
     */
    @Override
    public Directory setProvider(Provider provider) {
        setProperty(PROVIDER, provider);
        return this;
    }

}
