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
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.*;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultGroup extends AbstractExtendableInstanceResource implements Group {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final EnumProperty<GroupStatus> STATUS = new EnumProperty<GroupStatus>(GroupStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Directory> DIRECTORY = new ResourceReference<Directory>("directory", Directory.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupMembershipList, GroupMembership> ACCOUNT_MEMBERSHIPS =
            new CollectionReference<GroupMembershipList, GroupMembership>("accountMemberships", GroupMembershipList.class, GroupMembership.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, CUSTOM_DATA, DIRECTORY, TENANT, ACCOUNTS, ACCOUNT_MEMBERSHIPS);

    public DefaultGroup(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroup(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public Group setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Group setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public GroupStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return GroupStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Group setStatus(GroupStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public Directory getDirectory() {
        return getResourceProperty(DIRECTORY);
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
        AccountList list = getAccounts(); //safe to get the href; does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, (Criteria<AccountCriteria>) criteria);
    }

    @Override
    public GroupMembershipList getAccountMemberships() {
        return getResourceProperty(ACCOUNT_MEMBERSHIPS);
    }

    @Override
    public GroupMembership addAccount(Account account) {
        return DefaultGroupMembership.create(account, this, getDataStore());
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public GroupMembership addAccount(String hrefOrEmailOrUsername) {
        Assert.hasText(hrefOrEmailOrUsername, "hrefOrEmailOrUsername cannot be null or empty");
        Account account =  findAccount(hrefOrEmailOrUsername);
        if (account != null){
            return DefaultGroupMembership.create(account, this, getDataStore());
        }
        else {
            throw new IllegalStateException("No matching account for hrefOrEmailOrUsername was found.");
        }
    }

    /**
     * @since 1.0.RC5
     */
    private Account findAccount(String hrefOrEmailOrUsername){
        Account account = null;

        // Let's check if hrefOrName looks like an href
        String[] splitHrefOrEmailOrName = hrefOrEmailOrUsername.split("/");
        Directory directory = this.getDirectory();
        if (splitHrefOrEmailOrName.length > 4) {
            try {
                account = getDataStore().getResource(hrefOrEmailOrUsername, Account.class);
                // Notice that groups can only be related to Accounts in the same directory
                if (account != null && account.getDirectory().getHref().equals(directory.getHref())){
                    return account;
                }
            } catch (ResourceException e) {
                // Although hrefOrName seemed to be an actual href value no Resource was found in the backend.
                // Maybe this is actually a name rather than an href
            }
        }
        AccountList accounts = directory.getAccounts(Accounts.where(Accounts.username().eqIgnoreCase(hrefOrEmailOrUsername)));
        if (accounts.iterator().hasNext()){
            account = accounts.iterator().next();
        } else {
            accounts = directory.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(hrefOrEmailOrUsername)));
            if (accounts.iterator().hasNext()){
                account = accounts.iterator().next();
            }
        }
        return account;
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public Group removeAccount(Account account) {
        Assert.notNull(account, "account cannot be null");

        GroupMembership groupMembership = null;
        for (GroupMembership accountGroupMembership : getAccountMemberships()) {
            if (accountGroupMembership.getAccount().getHref().equals(account.getHref())) {
                groupMembership = accountGroupMembership;
                break;
            }
        }
        if (groupMembership != null){
            groupMembership.delete();
        } else {
            throw new IllegalStateException("The specified account does not belong to this Group.");
        }
        return this;
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public Group removeAccount(String hrefOrEmailOrUsername) {
        Assert.hasText(hrefOrEmailOrUsername, "hrefOrEmailOrUsername cannot be null or empty");
        GroupMembership groupMembership = null;
        for (GroupMembership aGroupMembership : getAccountMemberships()) {
            if (aGroupMembership.getAccount().getHref().equals(hrefOrEmailOrUsername)
                    || aGroupMembership.getAccount().getEmail().equals(hrefOrEmailOrUsername)
                    || aGroupMembership.getAccount().getUsername().equals(hrefOrEmailOrUsername)) {
                groupMembership = aGroupMembership;
                break;
            }
        }
        if (groupMembership != null){
            groupMembership.delete();
        } else {
            throw new IllegalStateException("The specified account does not belong to this Group.");
        }
        return this;
    }

    /**
     * @since 0.8
     */
    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    /**
     * @since 0.8
     */
    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    public Group saveWithResponseOptions(GroupOptions groupOptions) {
        Assert.notNull(groupOptions, "groupOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, groupOptions);
        return this;
    }
}
