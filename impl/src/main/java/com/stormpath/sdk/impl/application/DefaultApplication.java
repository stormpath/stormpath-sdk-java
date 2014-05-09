/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.AccountStoreMapping;
import com.stormpath.sdk.application.AccountStoreMappingCriteria;
import com.stormpath.sdk.application.AccountStoreMappingList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.impl.api.DefaultApiKeyCriteria;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.authc.BasicAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.provider.ProviderAccountResolver;
import com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory;
import com.stormpath.sdk.impl.query.Expandable;
import com.stormpath.sdk.impl.query.Expansion;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.ID;

/**
 * @since 0.2
 */
public class DefaultApplication extends AbstractInstanceResource implements Application {

    private static final Logger log = LoggerFactory.getLogger(DefaultApplication.class);

    // SIMPLE PROPERTIES:
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StatusProperty<ApplicationStatus> STATUS = new StatusProperty<ApplicationStatus>(ApplicationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<AccountStoreMapping> DEFAULT_ACCOUNT_STORE_MAPPING = new ResourceReference<AccountStoreMapping>("defaultAccountStoreMapping", AccountStoreMapping.class);
    static final ResourceReference<AccountStoreMapping> DEFAULT_GROUP_STORE_MAPPING = new ResourceReference<AccountStoreMapping>("defaultGroupStoreMapping", AccountStoreMapping.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS = new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group> GROUPS = new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<AccountStoreMappingList, AccountStoreMapping> ACCOUNT_STORE_MAPPINGS =
            new CollectionReference<AccountStoreMappingList, AccountStoreMapping>("accountStoreMappings", AccountStoreMappingList.class, AccountStoreMapping.class);
    static final CollectionReference<PasswordResetTokenList, PasswordResetToken> PASSWORD_RESET_TOKENS =
            new CollectionReference<PasswordResetTokenList, PasswordResetToken>("passwordResetTokens", PasswordResetTokenList.class, PasswordResetToken.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, TENANT, DEFAULT_ACCOUNT_STORE_MAPPING, DEFAULT_GROUP_STORE_MAPPING, ACCOUNTS, GROUPS, ACCOUNT_STORE_MAPPINGS, PASSWORD_RESET_TOKENS);

    public DefaultApplication(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApplication(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public ApplicationStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return ApplicationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(ApplicationStatus status) {
        setProperty(STATUS, status.name());
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
        return getDataStore().getResource(list.getHref(), AccountList.class, criteria);
    }

    @Override
    //since 0.8
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
        GroupList groups = getGroups(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(groups.getHref(), GroupList.class, criteria);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public Account sendPasswordResetEmail(String accountUsernameOrEmail) {
        PasswordResetToken token = createPasswordResetToken(accountUsernameOrEmail);
        return token.getAccount();
    }

    private PasswordResetToken createPasswordResetToken(String email) {
        String href = getPasswordResetTokensHref();
        PasswordResetToken passwordResetToken = getDataStore().instantiate(PasswordResetToken.class);
        passwordResetToken.setEmail(email);
        return getDataStore().create(href, passwordResetToken);
    }

    private String getPasswordResetTokensHref() {
        Map<String, String> passwordResetTokensLink = (Map<String, String>) getProperty(PASSWORD_RESET_TOKENS.getName());
        return passwordResetTokensLink.get(HREF_PROP_NAME);
    }

    public Account verifyPasswordResetToken(String token) {
        String href = getPasswordResetTokensHref() + "/" + token;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        PasswordResetToken prToken = getDataStore().instantiate(PasswordResetToken.class, props);
        return prToken.getAccount();
    }

    @Override
    public AuthenticationResult authenticateAccount(AuthenticationRequest request) {
        return new BasicAuthenticator(getDataStore()).authenticate(getHref(), request);
    }

    /**
     * @since 1.0.beta
     */
    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) throws ResourceException {
        return new ProviderAccountResolver(getDataStore()).resolveProviderAccount(getHref(), request);
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
    public void delete() {
        getDataStore().delete(this);
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMappingList getAccountStoreMappings() {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        AccountStoreMappingList accountStoreMappings = getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), AccountStoreMappingList.class, queryParams);
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMappingList getAccountStoreMappings(AccountStoreMappingCriteria criteria) {
        AccountStoreMappingList accountStoreMappings = getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), AccountStoreMappingList.class, criteria);
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStore getDefaultAccountStore() {
        AccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_ACCOUNT_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /**
     * @since 0.9
     */
    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (AccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultAccountStore(true);
                accountStoreMapping.save();
                break;
            }
        }
        if (needToCreateNewStore) {
            AccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultAccountStore(true);
            mapping.save();
        }
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStore getDefaultGroupStore() {
        AccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_GROUP_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /**
     * @since 0.9
     */
    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (AccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultGroupStore(true);
                accountStoreMapping.save();
                break;
            }
        }
        if (needToCreateNewStore) {
            AccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultGroupStore(true);
            mapping.save();
        }
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMapping createAccountStoreMapping(AccountStoreMapping mapping) throws ResourceException {
        String href = getAccountStoreMappingsHref();
        return getDataStore().create(href, mapping);
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        AccountStoreMapping accountStoreMapping = getDataStore().instantiate(AccountStoreMapping.class);
        accountStoreMapping.setAccountStore(accountStore);
        accountStoreMapping.setApplication(this);
        accountStoreMapping.setListIndex(Integer.MAX_VALUE);
        return createAccountStoreMapping(accountStoreMapping);

    }

    /**
     * @since 1.1.beta
     */
    @Override
    public ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException {
        return getApiKey(id, new DefaultApiKeyOptions());
    }

    /**
     * @since 1.1.beta
     */
    @Override
    public ApiKey getApiKey(String id, ApiKeyOptions options) throws ResourceException, IllegalArgumentException {

        Assert.hasText(id, "The 'id' argument cannot be null or empty to get an api key.");
        Assert.notNull(options, "options argument cannot be null.");
        Assert.hasText(getHref(), "The application must have an href to get an api key.");


        DefaultApiKeyCriteria criteria = new DefaultApiKeyCriteria();
        criteria.add(new DefaultEqualsExpressionFactory(ID.getName()).eq(id));

        if (!options.isEmpty() && options instanceof Expandable) {

            Expandable expandable = (Expandable) options;

            for (Expansion exp : expandable.getExpansions()) {

                if ("tenant".equals(exp.getName())) {
                    criteria.withTenant();
                }

                if ("account".equals(exp.getName())) {
                    criteria.withAccount();
                }
            }
        }

        String href = getHref() + "/apiKeys";
        ApiKeyList apiKeys =  getDataStore().getResource(href, ApiKeyList.class, criteria);

        ApiKey apiKey = null;

        if (apiKeys != null && apiKeys.iterator().hasNext()) {
            apiKey = apiKeys.iterator().next(); // we expect only one api key to be in the collection
        }

        return apiKey;
    }

    /**
     * @since 0.9
     */
    private String getAccountStoreMappingsHref() {
        //TODO enable auto discovery via Tenant resource (should be just /accountStoreMappings)
        String href = "/accountStoreMappings";
        // TODO: Uncomment out below when application's accountStoreMapping endpoint accepts POST request.
//        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
//        return accountStoreMappingList.getHref();
        return href;
    }

}
