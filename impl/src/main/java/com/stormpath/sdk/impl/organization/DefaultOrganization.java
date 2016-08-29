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
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.organization.OrganizationStatus;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultOrganization extends AbstractExtendableInstanceResource implements Organization {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StringProperty NAME_KEY = new StringProperty("nameKey");
    static final EnumProperty<OrganizationStatus> STATUS = new EnumProperty<OrganizationStatus>(OrganizationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<OrganizationAccountStoreMapping> DEFAULT_ACCOUNT_STORE_MAPPING =
            new ResourceReference<OrganizationAccountStoreMapping>("defaultAccountStoreMapping", OrganizationAccountStoreMapping.class);
    static final ResourceReference<OrganizationAccountStoreMapping> DEFAULT_GROUP_STORE_MAPPING   =
            new ResourceReference<OrganizationAccountStoreMapping>("defaultGroupStoreMapping", OrganizationAccountStoreMapping.class);
    static final ResourceReference<AccountLinkingPolicy> ACCOUNT_LINKING_POLICY =
            new ResourceReference<AccountLinkingPolicy>("accountLinkingPolicy", AccountLinkingPolicy.class);


    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping> ACCOUNT_STORE_MAPPINGS =
            new CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping>("accountStoreMappings",
                    OrganizationAccountStoreMappingList.class,
                    OrganizationAccountStoreMapping.class);


    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, NAME_KEY, STATUS, TENANT, CUSTOM_DATA, DEFAULT_ACCOUNT_STORE_MAPPING, DEFAULT_GROUP_STORE_MAPPING, ACCOUNT_STORE_MAPPINGS, ACCOUNT_LINKING_POLICY);

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
    public OrganizationAccountStoreMappingList getAccountStoreMappings() {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    @Override
    public OrganizationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        OrganizationAccountStoreMappingList accountStoreMappings =
                getAccountStoreMappings(); //obtains the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), OrganizationAccountStoreMappingList.class, queryParams);
    }

    @Override
    public AccountStore getDefaultAccountStore() {
        OrganizationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_ACCOUNT_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    @Override
    public OrganizationAccountStoreMappingList getAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria) {
        OrganizationAccountStoreMappingList mappings = getAccountStoreMappings();  //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(mappings.getHref(), OrganizationAccountStoreMappingList.class, (Criteria<OrganizationAccountStoreMappingCriteria>) criteria);
    }

    @Override
    public AccountStore getDefaultGroupStore() {
        OrganizationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_GROUP_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        OrganizationAccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
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
    }

    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        OrganizationAccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
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
    }

    /** @since 1.0.RC9 */
    @Override
    public OrganizationAccountStoreMapping createAccountStoreMapping(OrganizationAccountStoreMapping mapping) throws ResourceException {
        return getDataStore().create("/organizationAccountStoreMappings", mapping);
    }

    @Override
    public OrganizationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        Assert.notNull(accountStore, "accountStore cannot be null.");
        OrganizationAccountStoreMapping accountStoreMapping = getDataStore().instantiate(OrganizationAccountStoreMapping.class);
        accountStoreMapping.setAccountStore(accountStore);
        accountStoreMapping.setOrganization(this);
        accountStoreMapping.setListIndex(Integer.MAX_VALUE);
        accountStoreMapping = createAccountStoreMapping(accountStoreMapping);
        return accountStoreMapping;
    }

    public Account createAccount(Account account) {
        Assert.notNull(account, "Account instance cannot be null.");
        CreateAccountRequest request = Accounts.newCreateRequestFor(account).build();
        return createAccount(request);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Account account = request.getAccount();
        Account createdAccount = null;

        AccountStore acctStore = getDefaultAccountStore();
        AccountStore accountStore = null;
        if (acctStore != null && acctStore.getHref().contains("directories")) {
            accountStore = getDataStore().getResource(acctStore.getHref(), Directory.class);
        } else if (acctStore != null && acctStore.getHref().contains("groups")) {
            accountStore = getDataStore().getResource(acctStore.getHref(), Group.class);
        }
        if (accountStore == null){
            throw new IllegalStateException("No account store assigned to this organization has been configured as the default storage location for newly created accounts.");
        }
        if (accountStore instanceof Directory){
            createdAccount = ((Directory) accountStore).createAccount(account);
        }
        if (accountStore instanceof Group){
            createdAccount = ((Group) accountStore).getDirectory().createAccount(account);
        }
        return createdAccount;
    }

    @Override
    public Group createGroup(Group group) {
        Assert.notNull(group, "Group instance cannot be null.");
        CreateGroupRequest request = Groups.newCreateRequestFor(group).build();
        return createGroup(request);
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Group group = request.getGroup();
        Group createdGroup = null;
        AccountStore groupStore = null;

        AccountStore store = getDefaultGroupStore();
        if (store != null && store.getHref().contains("directories")) {
            groupStore = getDataStore().getResource(store.getHref(), Directory.class);
        }
        if (groupStore == null){
            throw new IllegalStateException("No groupStore assigned to this organization has been configured as the default storage location for newly created accounts.");
        }
        createdGroup = ((Directory) groupStore).createGroup(group);
        return createdGroup;
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public OrganizationAccountStoreMapping addAccountStore(String hrefOrName) {
        Assert.hasText(hrefOrName, "hrefOrName cannot be null or empty.");
        AccountStore accountStore = null;

        //Let's check if hrefOrName looks like an href. If so, we will also identify whether it refers to directory or a group
        String[] splitHrefOrName = hrefOrName.split("/");
        if (splitHrefOrName.length > 4) {
            Class<? extends AccountStore> accountStoreType = null;
            String[] splitApplicationHref = getHref().split("/");
            if (splitHrefOrName.length == splitApplicationHref.length) {
                if (splitHrefOrName[4].equals("directories")) {
                    accountStoreType = Directory.class;
                } else if (splitHrefOrName[4].equals("groups")) {
                    accountStoreType = Group.class;
                }
            }
            if (accountStoreType != null) {
                try {
                    //Let's check if the provided value is an actual href for an existent resource
                    accountStore = getDataStore().getResource(hrefOrName, accountStoreType);
                } catch (ResourceException e) {
                    //Although hrefOrName seemed to be an actual href value no Resource was found in the backend. So maybe
                    //this is actually a name rather than an href. Let's try to find a resource by name now...
                }
            }
        }
        if (accountStore == null) {
            //Let's try to find both a Directory and a Group with the given name
            Directory directory = getSingleTenantDirectory(Directories.where(Directories.name().eqIgnoreCase(hrefOrName)));
            Group group = getSingleTenantGroup(Groups.where(Groups.name().eqIgnoreCase(hrefOrName)));
            if (directory != null && group != null) {
                //The provided criteria matched more than one Groups in the tenant, we will throw
                throw new IllegalArgumentException("There are both a Directory and a Group matching the provided name in the current tenant. " +
                        "Please provide the href of the intended Resource instead of its name in order to univocally identify it.");
            }
            accountStore = (directory != null) ? directory : group;
        }
        if(accountStore != null) {
            return addAccountStore(accountStore);
        }
        //We could not find any resource matching the hrefOrName value; we return null
        return null;
    }

    /**
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     */
    private Directory getSingleTenantDirectory(DirectoryCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");
        Tenant tenant = getDataStore().getResource("/tenants/current", Tenant.class);
        DirectoryList directories = tenant.getDirectories(criteria);

        Directory foundDirectory = null;
        for (Directory dir : directories) {
            if (foundDirectory != null) {
                //The provided criteria matched more than one Directory in the tenant, we will throw
                throw new IllegalArgumentException("The provided criteria matched more than one Directory in the current Tenant.");
            }
            foundDirectory = dir;
        }
        return foundDirectory;
    }

    /**
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     * */
    private Group getSingleTenantGroup(GroupCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");

        Tenant tenant = getDataStore().getResource("/tenants/current", Tenant.class);
        DirectoryList directories = tenant.getDirectories();
        Group foundGroup = null;
        for (Directory directory : directories) {
            GroupList groups = directory.getGroups(criteria);
            //There cannot be more than one group with the same name in a single tenant. Thus, the group list will have either
            //zero or one items, never more.
            for (Group grp : groups) {
                if(foundGroup != null) {
                    //The provided criteria matched more than one Groups in the tenant, we will throw
                    throw new IllegalArgumentException("The provided criteria matched more than one Group in the current Tenant.");
                }
                foundGroup = grp;
            }
        }
        return foundGroup;
    }

    /* @since 1.1.0 */
    @Override
    public AccountLinkingPolicy getAccountLinkingPolicy() {
        return getResourceProperty(ACCOUNT_LINKING_POLICY);
    }
}
