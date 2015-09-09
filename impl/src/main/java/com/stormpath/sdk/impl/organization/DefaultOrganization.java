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

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.directory.*;
import com.stormpath.sdk.group.*;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.*;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.0.RC4.6
 */
public class DefaultOrganization extends AbstractExtendableInstanceResource implements Organization {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StringProperty NAME_KEY = new StringProperty("nameKey");
    static final StatusProperty<OrganizationStatus> STATUS = new StatusProperty<OrganizationStatus>(OrganizationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<OrganizationAccountStoreMapping> DEFAULT_ACCOUNT_STORE_MAPPING =
            new ResourceReference<OrganizationAccountStoreMapping>("defaultAccountStoreMapping", OrganizationAccountStoreMapping.class);
    static final ResourceReference<OrganizationAccountStoreMapping> DEFAULT_GROUP_STORE_MAPPING   =
            new ResourceReference<OrganizationAccountStoreMapping>("defaultGroupStoreMapping", OrganizationAccountStoreMapping.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<DirectoryList, Directory> DIRECTORIES =
            new CollectionReference<DirectoryList, Directory>("directories", DirectoryList.class, Directory.class);
    static final CollectionReference<GroupList, Group> GROUPS =
            new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping> ACCOUNT_STORE_MAPPINGS =
            new CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping>("organizationAccountStoreMappings", OrganizationAccountStoreMappingList.class, OrganizationAccountStoreMapping.class);


    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, NAME_KEY, STATUS, TENANT, CUSTOM_DATA, DIRECTORIES, GROUPS, DEFAULT_ACCOUNT_STORE_MAPPING, DEFAULT_GROUP_STORE_MAPPING, ACCOUNT_STORE_MAPPINGS, ACCOUNTS);

    public DefaultOrganization(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOrganization(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public Organization setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getNameKey() {
        return getString(NAME_KEY);
    }

    @Override
    public Organization setNameKey(String nameKey) {
        setProperty(NAME_KEY, nameKey);
        return this;
    }

    @Override
    public OrganizationStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return OrganizationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Organization setStatus(OrganizationStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Organization setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public GroupList getGroups() {
        return getResourceProperty(GROUPS);
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        GroupList list = getGroups(); // obtains the href: no query is executed until iteration occurs
        return getDataStore().getResource(list.getHref(), GroupList.class, queryParams);
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        GroupList list = getGroups(); // obtains the href: no query is executed until iteration occurs
        return getDataStore().getResource(list.getHref(), GroupList.class, (Criteria<GroupCriteria>) criteria);
    }

    @Override
    public DirectoryList getDirectories() {
        return getResourceProperty(DIRECTORIES);
    }

    @Override
    public DirectoryList getDirectories(Map<String, Object> queryParams) {
        DirectoryList list = getDirectories(); // obtains the href: no query is executed until iteration occurs
        return getDataStore().getResource(list.getHref(), DirectoryList.class, queryParams);
    }

    @Override
    public DirectoryList getDirectories(DirectoryCriteria criteria) {
        DirectoryList list = getDirectories(); // obtains the href: no query is executed until iteration occurs
        return getDataStore().getResource(list.getHref(), DirectoryList.class, (Criteria<DirectoryCriteria>) criteria);
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings() {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Map<String, Object> queryParams) {
        OrganizationAccountStoreMappingList accountStoreMappings =
                getOrganizationAccountStoreMappings(); //obtains the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), OrganizationAccountStoreMappingList.class, queryParams);
    }
    
    @Override
    public AccountStore getDefaultAccountStore() {
        OrganizationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_ACCOUNT_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        OrganizationAccountStoreMappingList accountStoreMappingList = getOrganizationAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (OrganizationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultAccountStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            OrganizationAccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultAccountStore(true);
            mapping.save();
            setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, mapping);
        }
        save();
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria) {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    @Override
    public AccountStore getDefaultGroupStore() {
        OrganizationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_GROUP_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        OrganizationAccountStoreMappingList accountStoreMappingList = getOrganizationAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (OrganizationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultGroupStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_GROUP_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            OrganizationAccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultGroupStore(true);
            mapping.save();
            setProperty(DEFAULT_GROUP_STORE_MAPPING, mapping);
        }
        save();
    }

    @Override
    public OrganizationAccountStoreMapping createOrganizationAccountStoreMapping(OrganizationAccountStoreMapping mapping) throws ResourceException {
        return getDataStore().create("/" + ACCOUNT_STORE_MAPPINGS.getName(), mapping);
    }

    @Override
    public OrganizationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        Assert.notNull(accountStore, "accountStore cannot be null.");
        OrganizationAccountStoreMapping accountStoreMapping = getDataStore().instantiate(OrganizationAccountStoreMapping.class);
        accountStoreMapping.setAccountStore(accountStore);
        accountStoreMapping.setOrganization(this);
        accountStoreMapping.setListIndex(Integer.MAX_VALUE);
        return createOrganizationAccountStoreMapping(accountStoreMapping);
    }

    @Override
    public Group createGroup(Group group) throws ResourceException {
        Assert.notNull(group, "Group instance cannot be null.");
        CreateGroupRequest request = Groups.newCreateRequestFor(group).build();
        return createGroup(request);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        Assert.notNull(request, "Request cannot be null.");

        final Group group = request.getGroup();
        String href = getGroups().getHref();

        if (request.isGroupOptionsSpecified()) {
            return getDataStore().create(href, group, request.getGroupOptions());
        }
        return getDataStore().create(href, group);
    }

    public Account createAccount(Account account) {
        Assert.notNull(account, "Account instance cannot be null.");
        CreateAccountRequest request = Accounts.newCreateRequestFor(account).build();
        return createAccount(request);
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
        AccountList list = getAccounts();  //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, (Criteria<AccountCriteria>) criteria);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Account account = request.getAccount();
        String href = getAccounts().getHref();

        char querySeparator = '?';

        if (request.isRegistrationWorkflowOptionSpecified()) {
            href += querySeparator + "registrationWorkflowEnabled=" + request.isRegistrationWorkflowEnabled();
            querySeparator = '&';
        }

        if (request.isPasswordFormatSpecified()) {
            href += querySeparator + "passwordFormat=" + request.getPasswordFormat();
        }

        if (request.isAccountOptionsSpecified()) {
            return getDataStore().create(href, account, request.getAccountOptions());
        }

        return getDataStore().create(href, account);
    }

    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }
}
