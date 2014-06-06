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
import com.stormpath.sdk.authc.ApiAuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.api.DefaultApiKeyCriteria;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.authc.AuthenticationRequestDispatcher;
import com.stormpath.sdk.impl.authc.DefaultApiRequestAuthenticator;
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
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.authc.OauthRequestAuthenticator;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;

/** @since 0.2 */
public class DefaultApplication extends AbstractInstanceResource implements Application {

    private static final String OAUTH_REQUEST_AUTHENTICATOR_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.DefaultOauthRequestAuthenticator";

    private static final String OAUTH_BUILDER_NOT_AVAILABLE_MSG;

    private static final String OAUTH_AUTHENTICATION_REQUEST_DISPATCHER_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.OauthAuthenticationRequestDispatcher";

    private static final Class<OauthRequestAuthenticator> OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS;

    private static final Class<AuthenticationRequestDispatcher> AUTHENTICATION_REQUEST_DISPATCHER_CLASS;

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final Set<Class> HTTP_REQUEST_SUPPORTED_CLASSES;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG =
        "Class [%s] is not one of the supported http requests classes [%s].";

    static {
        if (Classes.isAvailable(OAUTH_REQUEST_AUTHENTICATOR_FQCN)) {
            OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS = Classes.forName(OAUTH_REQUEST_AUTHENTICATOR_FQCN);
        } else {
            OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS = null;
        }

        if (Classes.isAvailable(OAUTH_AUTHENTICATION_REQUEST_DISPATCHER_FQCN)) {
            AUTHENTICATION_REQUEST_DISPATCHER_CLASS = Classes.forName(OAUTH_AUTHENTICATION_REQUEST_DISPATCHER_FQCN);
        } else {
            AUTHENTICATION_REQUEST_DISPATCHER_CLASS = AuthenticationRequestDispatcher.class;
        }

        OAUTH_BUILDER_NOT_AVAILABLE_MSG = "Unable to find the '" + OAUTH_REQUEST_AUTHENTICATOR_FQCN +
                                          "' implementation on the classpath.  Please ensure you " +
                                          "have added the stormpath-sdk-oauth-{version}.jar file to your runtime classpath.";

        Set<Class> supportedClasses = new HashSet<Class>();

        supportedClasses.add(HttpRequest.class);

        if (Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN)) {
            supportedClasses.add(Classes.forName(HTTP_SERVLET_REQUEST_FQCN));
        }

        HTTP_REQUEST_SUPPORTED_CLASSES = supportedClasses;
    }

    private static final Logger log = LoggerFactory.getLogger(DefaultApplication.class);

    // SIMPLE PROPERTIES:
    static final StringProperty                    NAME        = new StringProperty("name");
    static final StringProperty                    DESCRIPTION = new StringProperty("description");
    static final StatusProperty<ApplicationStatus> STATUS      =
        new StatusProperty<ApplicationStatus>(ApplicationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant>              TENANT                        =
        new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<AccountStoreMapping> DEFAULT_ACCOUNT_STORE_MAPPING =
        new ResourceReference<AccountStoreMapping>("defaultAccountStoreMapping", AccountStoreMapping.class);
    static final ResourceReference<AccountStoreMapping> DEFAULT_GROUP_STORE_MAPPING   =
        new ResourceReference<AccountStoreMapping>("defaultGroupStoreMapping", AccountStoreMapping.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account>                         ACCOUNTS               =
        new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group>                             GROUPS                 =
        new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<AccountStoreMappingList, AccountStoreMapping> ACCOUNT_STORE_MAPPINGS =
        new CollectionReference<AccountStoreMappingList, AccountStoreMapping>("accountStoreMappings",
                                                                              AccountStoreMappingList.class,
                                                                              AccountStoreMapping.class);
    static final CollectionReference<PasswordResetTokenList, PasswordResetToken>   PASSWORD_RESET_TOKENS  =
        new CollectionReference<PasswordResetTokenList, PasswordResetToken>("passwordResetTokens",
                                                                            PasswordResetTokenList.class,
                                                                            PasswordResetToken.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
        NAME, DESCRIPTION, STATUS, TENANT, DEFAULT_ACCOUNT_STORE_MAPPING, DEFAULT_GROUP_STORE_MAPPING, ACCOUNTS, GROUPS,
        ACCOUNT_STORE_MAPPINGS, PASSWORD_RESET_TOKENS);

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
    public Application setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Application setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
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
    public Application setStatus(ApplicationStatus status) {
        setProperty(STATUS, status.name());
        return this;
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
    public Account sendPasswordResetEmail(String email) {
        PasswordResetToken token = createPasswordResetToken(email);
        return token.getAccount();
    }

    private PasswordResetToken createPasswordResetToken(String email) {
        String href = getPasswordResetTokensHref();
        PasswordResetToken passwordResetToken = getDataStore().instantiate(PasswordResetToken.class);
        passwordResetToken.setEmail(email);
        return getDataStore().create(href, passwordResetToken);
    }

    private String getPasswordResetTokensHref() {
        Map<String, String> passwordResetTokensLink =
            (Map<String, String>) getProperty(PASSWORD_RESET_TOKENS.getName());
        return passwordResetTokensLink.get(HREF_PROP_NAME);
    }

    @Override
    public Account verifyPasswordResetToken(String token) {
        String href = getPasswordResetTokensHref() + "/" + token;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        PasswordResetToken prToken = getDataStore().instantiate(PasswordResetToken.class, props);
        return prToken.getAccount();
    }

    /** @since 1.0.RC */
    @Override
    public Account resetPassword(String passwordResetToken, String newPassword) {
        Assert.hasText(passwordResetToken, "passwordResetToken cannot be empty or null.");
        Assert.hasText(newPassword, "newPassword cannot be empty or null.");
        String href = getPasswordResetTokensHref() + "/" + passwordResetToken;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        PasswordResetToken instantiatedToken = getDataStore().instantiate(PasswordResetToken.class, props);
        instantiatedToken.setPassword(newPassword);
        PasswordResetToken createdPasswordResetToken =
            getDataStore().create(href, instantiatedToken, PasswordResetToken.class);
        return createdPasswordResetToken.getAccount();
    }

    @Override
    public AuthenticationResult authenticateAccount(AuthenticationRequest request) {
        AuthenticationRequestDispatcher dispatcher = Classes.newInstance(AUTHENTICATION_REQUEST_DISPATCHER_CLASS);
        return dispatcher.authenticate(getDataStore(), this, request);
    }

    /** @since 1.0.beta */
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

    /** @since 0.9 */
    @Override
    public AccountStoreMappingList getAccountStoreMappings() {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    /** @since 0.9 */
    @Override
    public AccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        AccountStoreMappingList accountStoreMappings =
            getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), AccountStoreMappingList.class, queryParams);
    }

    /** @since 0.9 */
    @Override
    public AccountStoreMappingList getAccountStoreMappings(AccountStoreMappingCriteria criteria) {
        AccountStoreMappingList accountStoreMappings =
            getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), AccountStoreMappingList.class, criteria);
    }

    /** @since 0.9 */
    @Override
    public AccountStore getDefaultAccountStore() {
        AccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_ACCOUNT_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /** @since 0.9 */
    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (AccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultAccountStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            AccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultAccountStore(true);
            mapping.save();
            setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, mapping);
        }
        save();
    }

    /** @since 0.9 */
    @Override
    public AccountStore getDefaultGroupStore() {
        AccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_GROUP_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /** @since 0.9 */
    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (AccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultGroupStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_GROUP_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            AccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultGroupStore(true);
            mapping.save();
            setProperty(DEFAULT_GROUP_STORE_MAPPING, mapping);
        }
        save();
    }

    /** @since 0.9 */
    @Override
    public AccountStoreMapping createAccountStoreMapping(AccountStoreMapping mapping) throws ResourceException {
        String href = getAccountStoreMappingsHref();
        return getDataStore().create(href, mapping);
    }

    /** @since 0.9 */
    @Override
    public AccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        AccountStoreMapping accountStoreMapping = getDataStore().instantiate(AccountStoreMapping.class);
        accountStoreMapping.setAccountStore(accountStore);
        accountStoreMapping.setApplication(this);
        accountStoreMapping.setListIndex(Integer.MAX_VALUE);
        return createAccountStoreMapping(accountStoreMapping);

    }

    /** @since 1.0.RC */
    @Override
    public ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException {
        return getApiKey(id, new DefaultApiKeyOptions());
    }

    /** @since 1.0.RC */
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
        ApiKeyList apiKeys = getDataStore().getResource(href, ApiKeyList.class, criteria);

        ApiKey apiKey = null;

        if (apiKeys != null && apiKeys.iterator().hasNext()) {
            apiKey = apiKeys.iterator().next(); // we expect only one api key to be in the collection
        }

        return apiKey;
    }

    /** @since 0.9 */
    private String getAccountStoreMappingsHref() {
        //TODO enable auto discovery via Tenant resource (should be just /accountStoreMappings)
        String href = "/accountStoreMappings";
        // TODO: Uncomment out below when application's accountStoreMapping endpoint accepts POST request.
        //        AccountStoreMappingList accountStoreMappingList = getAccountStoreMappings();
        //        return accountStoreMappingList.getHref();
        return href;
    }

    @Override
    public ApiAuthenticationResult authenticateApiRequest(Object httpRequest) {
        validateHttpRequest(httpRequest);
        return new DefaultApiRequestAuthenticator(this, httpRequest).execute();
    }

    @Override
    public OauthRequestAuthenticator authenticateOauthRequest(Object httpRequest) {
        if (OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS == null) {
            throw new IllegalStateException(OAUTH_BUILDER_NOT_AVAILABLE_MSG);
        }
        validateHttpRequest(httpRequest);
        Constructor<OauthRequestAuthenticator> ctor =
            Classes.getConstructor(OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS, Application.class, Object.class);
        return Classes.instantiate(ctor, this, httpRequest);
    }

    @SuppressWarnings("unchecked")
    private void validateHttpRequest(Object httpRequest) {
        Assert.notNull(httpRequest);
        Class httpRequestClass = httpRequest.getClass();
        for (Class supportedClass : HTTP_REQUEST_SUPPORTED_CLASSES) {
            if (supportedClass.isAssignableFrom(httpRequestClass)) {
                return;
            }
        }
        throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequestClass.getName(),
                                                         HTTP_REQUEST_SUPPORTED_CLASSES.toString()));
    }
}
