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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.application.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.authc.BasicAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultApplication extends AbstractInstanceResource implements Application {

    // SIMPLE PROPERTIES:
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StatusProperty<ApplicationStatus> STATUS = new StatusProperty<ApplicationStatus>(ApplicationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS = new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group> GROUPS = new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<PasswordResetTokenList, PasswordResetToken> PASSWORD_RESET_TOKENS =
            new CollectionReference<PasswordResetTokenList, PasswordResetToken>("passwordResetTokens", PasswordResetTokenList.class, PasswordResetToken.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, TENANT, ACCOUNTS, GROUPS, PASSWORD_RESET_TOKENS);

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
        Map<String, String> passwordResetTokensRef = (Map<String, String>) getProperty(PASSWORD_RESET_TOKENS.getName());
        if (passwordResetTokensRef != null && !passwordResetTokensRef.isEmpty()) {
            return passwordResetTokensRef.get(HREF_PROP_NAME);
        }

        return null;
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

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    /**
     * @since 0.9
     */
    @Override
    public Account createAccount(Account account) throws ResourceException {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @since 0.9
     */
    @Override
    public Group createGroup(Group group) throws ResourceException {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMapping getAccountStoreMappings() {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStore getNewAccountStore() {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStore getNewGroupStore() {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @since 0.9
     */
    @Override
    public AccountStoreMapping createAccountStoreMapping(AccountStoreMapping mapping) throws ResourceException {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
