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
package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyCriteria;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.*;
import com.stormpath.sdk.impl.api.DefaultApiKey;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.group.DefaultGroupCriteria;
import com.stormpath.sdk.impl.group.DefaultGroupMembership;
import com.stormpath.sdk.impl.provider.IdentityProviderType;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultAccount extends AbstractExtendableInstanceResource implements Account {

    // SIMPLE PROPERTIES
    static final StringProperty EMAIL = new StringProperty("email");
    static final StringProperty USERNAME = new StringProperty("username");
    public static final StringProperty PASSWORD = new StringProperty("password");
    static final StringProperty GIVEN_NAME = new StringProperty("givenName");
    static final StringProperty MIDDLE_NAME = new StringProperty("middleName");
    static final StringProperty SURNAME = new StringProperty("surname");
    static final StatusProperty<AccountStatus> STATUS = new StatusProperty<AccountStatus>(AccountStatus.class);
    static final StringProperty FULL_NAME = new StringProperty("fullName"); //computed property, can't set it or query based on it

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<EmailVerificationToken> EMAIL_VERIFICATION_TOKEN =
            new ResourceReference<EmailVerificationToken>("emailVerificationToken", EmailVerificationToken.class);
    static final ResourceReference<Directory> DIRECTORY = new ResourceReference<Directory>("directory", Directory.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<ProviderData> PROVIDER_DATA = new ResourceReference<ProviderData>("providerData", ProviderData.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<GroupList, Group> GROUPS =
            new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<GroupMembershipList, GroupMembership> GROUP_MEMBERSHIPS =
            new CollectionReference<GroupMembershipList, GroupMembership>("groupMemberships", GroupMembershipList.class, GroupMembership.class);
    static final CollectionReference<ApiKeyList, ApiKey> API_KEYS =
            new CollectionReference<ApiKeyList, ApiKey>("apiKeys", ApiKeyList.class, ApiKey.class);
    // @since 1.0.RC4
    static final CollectionReference<ApplicationList, Application> APPLICATIONS =
            new CollectionReference<ApplicationList, Application>("applications", ApplicationList.class, Application.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            USERNAME, EMAIL, PASSWORD, GIVEN_NAME, MIDDLE_NAME, SURNAME, STATUS, FULL_NAME,
            EMAIL_VERIFICATION_TOKEN, CUSTOM_DATA, DIRECTORY, TENANT, GROUPS, GROUP_MEMBERSHIPS, 
            PROVIDER_DATA,API_KEYS, APPLICATIONS);

    public DefaultAccount(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccount(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    protected boolean isPrintableProperty(String name) {
        return !PASSWORD.getName().equalsIgnoreCase(name);
    }

    @Override
    public String getUsername() {
        return getString(USERNAME);
    }

    @Override
    public Account setUsername(String username) {
        setProperty(USERNAME, username);
        return this;
    }

    @Override
    public String getEmail() {
        return getString(EMAIL);
    }

    @Override
    public Account setEmail(String email) {
        setProperty(EMAIL, email);
        return this;
    }

    @Override
    public Account setPassword(String password) {
        setProperty(PASSWORD, password);
        return this;
    }

    @Override
    public String getGivenName() {
        return getString(GIVEN_NAME);
    }

    @Override
    public Account setGivenName(String givenName) {
        setProperty(GIVEN_NAME, givenName);
        return this;
    }

    @Override
    public String getMiddleName() {
        return getString(MIDDLE_NAME);
    }

    @Override
    public Account setMiddleName(String middleName) {
        setProperty(MIDDLE_NAME, middleName);
        return this;
    }

    @Override
    public String getSurname() {
        return getString(SURNAME);
    }

    @Override
    public Account setSurname(String surname) {
        setProperty(SURNAME, surname);
        return this;
    }

    @Override
    public String getFullName() {
        return getString(FULL_NAME);
    }

    @Override
    public AccountStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return AccountStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Account setStatus(AccountStatus status) {
        setProperty(STATUS, status.name());
        return this;
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
    public Directory getDirectory() {
        return getResourceProperty(DIRECTORY);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public GroupMembershipList getGroupMemberships() {
        return getResourceProperty(GROUP_MEMBERSHIPS);
    }

    @Override
    public GroupMembership addGroup(Group group) {
        return DefaultGroupMembership.create(this, group, getDataStore());
    }

    @Override
    public GroupMembership addGroup(String hrefOrName) {
        Group group =  findGroup(hrefOrName);
        if (group != null){
            return DefaultGroupMembership.create(this, group, getDataStore());
        }
        return null;
    }

    private Group findGroup(String hrefOrName) {

        Group group = null;

        //Let's check if hrefOrName looks like an href
        String[] splitHrefOrName = hrefOrName.split("/");
        if (splitHrefOrName.length > 4) {
            try {
                group = getDataStore().getResource(hrefOrName, Group.class);
            } catch (ResourceException e) {
                // Although hrefOrName seemed to be an actual href value no Resource was found in the backend.
                // Maybe this is actually a name rather than an href
            }
        }

        // Notice that accounts can only be added to Groups in the same directory
        Directory directory = this.getDirectory();
        if (group != null && group.getDirectory().getHref().equalsIgnoreCase(directory.getHref())){
            return group;
        }

        GroupList groups = directory.getGroups(Groups.where(Groups.name().eqIgnoreCase(hrefOrName)));
        if (groups.iterator().hasNext()){
            group = groups.iterator().next();
        }

        return group;
    }

    @Override
    public Account removeGroup(Group group) {
        GroupMembership groupMembership = null;
        for (GroupMembership aGroupMembership : getGroupMemberships()) {
            if (aGroupMembership.getGroup().getHref().equalsIgnoreCase(group.getHref())) {
                groupMembership = aGroupMembership;
                break;
            }
        }
        if (groupMembership != null){
            groupMembership.delete();
        }
        return this;
    }

    @Override
    public Account removeGroup(String hrefOrName) {
        GroupMembership groupMembership = null;
        for (GroupMembership aGroupMembership : getGroupMemberships()) {
            if (aGroupMembership.getGroup().getName().equalsIgnoreCase(hrefOrName) || aGroupMembership.getGroup().getHref().equalsIgnoreCase(hrefOrName)) {
                groupMembership = aGroupMembership;
                break;
            }
        }
        if (groupMembership != null){
            groupMembership.delete();
        }
        return this;
    }

    @Override
    public EmailVerificationToken getEmailVerificationToken() {
        return getResourceProperty(EMAIL_VERIFICATION_TOKEN);
    }

    /**
     * @since 0.8
     */
    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Account saveWithResponseOptions(AccountOptions accountOptions) {
        Assert.notNull(accountOptions, "accountOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, accountOptions);
        return this;
    }

    /**
     * @since 0.9.3
     */
    @Override
    public boolean isMemberOfGroup(String hrefOrName) {
        if(!Strings.hasText(hrefOrName)) {
            return false;
        }
        for (Group aGroup : getGroups()) {
            if (aGroup.getName().equalsIgnoreCase(hrefOrName) || aGroup.getHref().equalsIgnoreCase(hrefOrName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ApiKeyList getApiKeys() {
        return getResourceProperty(API_KEYS);
    }
    /**
     * Returns the {@link ProviderData} instance associated with this Account.
     *
     * @return the {@link ProviderData} instance associated with this Account.
     * @since 1.0.beta
     */
    @Override
    public ProviderData getProviderData() {
        Object value = getProperty(PROVIDER_DATA.getName());

        if (ProviderData.class.isInstance(value) || value == null) {
            return (ProviderData) value;
        }
        if (value instanceof Map && !((Map) value).isEmpty()) {
            String href = (String) ((Map) value).get(HREF_PROP_NAME);

            if (href == null) {
                throw new IllegalStateException("providerData resource does not contain its required href property.");
            }

            //Since the specific ProviderData instance that we need to create varies depending on the actual Provider
            //owning the account then we need to instruct the DataStore on how to instantiate it
            ProviderData providerData = getDataStore().getResource(href, ProviderData.class, "providerId", IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP);
            setProperty(PROVIDER_DATA, providerData);
            return providerData;
        }

        String msg = "'" + PROVIDER_DATA.getName() + "' property value type does not match the specified type. Specified type: " +
                PROVIDER_DATA.getType() + ".  Existing type: " + value.getClass().getName() + ".  Value: " + value;
        throw new IllegalStateException(msg);
    }

    @Override
    public ApiKeyList getApiKeys(Map<String, Object> queryParams) {
        ApiKeyList list = getApiKeys(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), ApiKeyList.class, queryParams);
    }

    @Override
    public ApiKeyList getApiKeys(ApiKeyCriteria criteria) {
        ApiKeyList list = getApiKeys(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), ApiKeyList.class, (Criteria<ApiKeyCriteria>) criteria);
    }

    /**
     * @since 1.0.RC
     */
    @Override
    public ApiKey createApiKey() {
        return createApiKey(new DefaultApiKeyOptions());
    }

    /**
     * @since 1.0.RC
     */
    @Override
    public ApiKey createApiKey(ApiKeyOptions options) {
        Assert.notNull(options, "options argument cannot be null.");
        String href = getApiKeys().getHref();
        return getDataStore().create(href, new DefaultApiKey(getDataStore()), options);
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications() {
        return getResourceProperty(APPLICATIONS);
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications(Map<String, Object> queryParams) {
        ApplicationList proxy = getApplications(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, queryParams);
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications(ApplicationCriteria criteria) {
        ApplicationList proxy = getApplications(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, (Criteria<ApplicationCriteria>) criteria);
    }
}
