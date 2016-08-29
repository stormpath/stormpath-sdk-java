/*
 * Copyright 2014 Stormpath, Inc.
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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.directory.AccountCreationPolicy;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryOptions;
import com.stormpath.sdk.directory.DirectoryStatus;
import com.stormpath.sdk.directory.PasswordPolicy;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.provider.IdentityProviderType;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultDirectory extends AbstractExtendableInstanceResource implements Directory {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final EnumProperty<DirectoryStatus> STATUS = new EnumProperty<DirectoryStatus>(DirectoryStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<Provider> PROVIDER = new ResourceReference<Provider>("provider", Provider.class);
    static final ResourceReference<PasswordPolicy> PASSWORD_POLICY = new ResourceReference<PasswordPolicy>("passwordPolicy", PasswordPolicy.class);
    static final ResourceReference<AccountCreationPolicy> ACCOUNT_CREATION_POLICY = new ResourceReference<AccountCreationPolicy>("accountCreationPolicy", AccountCreationPolicy.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group> GROUPS =
            new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<OrganizationList, Organization> ORGANIZATIONS =
            new CollectionReference<OrganizationList, Organization>("organizations", OrganizationList.class, Organization.class);
    static final CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping> ORGANIZATION_MAPPINGS =
            new CollectionReference<OrganizationAccountStoreMappingList, OrganizationAccountStoreMapping>("organizationMappings", OrganizationAccountStoreMappingList.class, OrganizationAccountStoreMapping.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, TENANT, PROVIDER, ACCOUNTS, GROUPS, CUSTOM_DATA, PASSWORD_POLICY, ACCOUNT_CREATION_POLICY, ORGANIZATION_MAPPINGS, ORGANIZATIONS);

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
    public Directory setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Directory setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
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
    public Directory setStatus(DirectoryStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public Account createAccount(Account account) {
        Assert.notNull(account, "account cannot be null.");
        return createAccount(Accounts.newCreateRequestFor(account).build());
    }

    @Override
    public Account createAccount(Account account, boolean registrationWorkflowEnabled) {
        Assert.notNull(account, "account cannot be null.");
        return createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(registrationWorkflowEnabled).build());
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        Assert.notNull(request, "Request cannot be null.");
        final Account account = request.getAccount();
        String href = getAccounts().getHref();

        if (request.isRegistrationWorkflowOptionSpecified()) {
            href += "?registrationWorkflowEnabled=" + request.isRegistrationWorkflowEnabled();
        }

        if (request.isAccountOptionsSpecified()) {
            return getDataStore().create(href, account, request.getAccountOptions());
        }

        return getDataStore().create(href, account);
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
        return getDataStore().getResource(list.getHref(), AccountList.class, (Criteria<AccountCriteria>) criteria);
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
        return getDataStore().getResource(list.getHref(), GroupList.class, (Criteria<GroupCriteria>) criteria);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    /**
     * @since 0.6
     */
    @Override
    public Group createGroup(Group group) {
        Assert.notNull(group, "Group cannot be null.");
        return createGroup(Groups.newCreateRequestFor(group).build());
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
     * @since 1.0.beta
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
            setProperty(PROVIDER, provider);
            return provider;
        }

        String msg = "'" + PROVIDER.getName() + "' property value type does not match the specified type. Specified type: " +
                PROVIDER.getType() + ".  Existing type: " + value.getClass().getName() + ".  Value: " + value;
        throw new IllegalStateException(msg);
    }

    /**
     * This method has not been exposed in the API since this operation is only 'legal' if you're going to create a brand new
     * directory. It is here, publicly visible, to allow the Tenant to add the provider information during Directory creation.
     *
     * @see {@link Tenant#createDirectory(com.stormpath.sdk.directory.CreateDirectoryRequest)}
     * @since 1.0.beta
     */
    public Directory setProvider(Provider provider) {
        //This exception should never be thrown to a developer unless he/she explicitly casts a Directory instance and then
        //tries to set the provider of an existing Directory.
        Assert.state(getHref() == null, "cannot change the provider of an existing Directory.");
        setProperty(PROVIDER, provider);
        return this;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        return getResourceProperty(PASSWORD_POLICY);
    }

    /**
     * @since 1.0-SNAPSHOT
     */
    @Override
    public AccountCreationPolicy getAccountCreationPolicy() {
        return getResourceProperty(ACCOUNT_CREATION_POLICY);
    }

    /**
     * @since 1.0.RC4.6
     */
    @Override
    public Directory saveWithResponseOptions(DirectoryOptions responseOptions) {
        Assert.notNull(responseOptions, "responseOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, responseOptions);
        return this;
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationList getOrganizations() {
        return getResourceProperty(ORGANIZATIONS);
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        OrganizationList list = getOrganizations(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), OrganizationList.class, queryParams);
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        OrganizationList list = getOrganizations(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), OrganizationList.class, (Criteria<OrganizationCriteria>) criteria);
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings() {
        return getResourceProperty(ORGANIZATION_MAPPINGS);
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Map<String, Object> queryParams) {
        OrganizationAccountStoreMappingList list = getOrganizationAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), OrganizationAccountStoreMappingList.class, queryParams);
    }

    /** @since 1.0.RC7.7 */
    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria) {
        OrganizationList list = getOrganizations(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), OrganizationAccountStoreMappingList.class, (Criteria<OrganizationAccountStoreMappingCriteria>) criteria);
    }
}
