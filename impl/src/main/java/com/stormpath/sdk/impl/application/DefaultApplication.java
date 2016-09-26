/*
 * Copyright 2015 Stormpath, Inc.
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

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyCriteria;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.impl.account.DefaultPasswordResetToken;
import com.stormpath.sdk.impl.account.DefaultVerificationEmailRequest;
import com.stormpath.sdk.impl.api.DefaultApiKeyCriteria;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.authc.AuthenticationRequestDispatcher;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.idsite.DefaultIdSiteCallbackHandler;
import com.stormpath.sdk.impl.idsite.DefaultIdSiteUrlBuilder;
import com.stormpath.sdk.impl.oauth.DefaultIdSiteAuthenticator;
import com.stormpath.sdk.impl.oauth.DefaultOAuthBearerRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.DefaultOAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.DefaultOAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.DefaultOAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.DefaultOAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.impl.provider.ProviderAccountResolver;
import com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory;
import com.stormpath.sdk.impl.query.Expandable;
import com.stormpath.sdk.impl.query.Expansion;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.ListProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.impl.saml.DefaultSamlCallbackHandler;
import com.stormpath.sdk.impl.saml.DefaultSamlIdpUrlBuilder;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.OAuthApiRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.saml.SamlPolicy;
import com.stormpath.sdk.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.ID;

/** @since 0.2 */
public class DefaultApplication extends AbstractExtendableInstanceResource implements Application {

    private static final String OAUTH_REQUEST_AUTHENTICATOR_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.DefaultOAuthRequestAuthenticator";

    private static final String OAUTH_BUILDER_NOT_AVAILABLE_MSG;

    private static final String OAUTH_AUTHENTICATION_REQUEST_DISPATCHER_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.OAuthAuthenticationRequestDispatcher";

    private static final Class<OAuthApiRequestAuthenticator> OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS;

    private static final Class<AuthenticationRequestDispatcher> AUTHENTICATION_REQUEST_DISPATCHER_CLASS;

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final Set<Class> HTTP_REQUEST_SUPPORTED_CLASSES;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG =
        "Class [%s] is not one of the supported http requests classes [%s].";

    private static final String INVALID_URI_FORMAT_MSG = "The provided URI does not match a valid URI scheme.";

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
    static final EnumProperty<ApplicationStatus> STATUS      =
        new EnumProperty<ApplicationStatus>(ApplicationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant>              TENANT                        =
        new ResourceReference<Tenant>("tenant", Tenant.class);
    static final ResourceReference<ApplicationAccountStoreMapping> DEFAULT_ACCOUNT_STORE_MAPPING =
        new ResourceReference<ApplicationAccountStoreMapping>("defaultAccountStoreMapping", ApplicationAccountStoreMapping.class);
    static final ResourceReference<ApplicationAccountStoreMapping> DEFAULT_GROUP_STORE_MAPPING   =
        new ResourceReference<ApplicationAccountStoreMapping>("defaultGroupStoreMapping", ApplicationAccountStoreMapping.class);
    static final ResourceReference<OAuthPolicy> OAUTH_POLICY   =
            new ResourceReference<OAuthPolicy>("oAuthPolicy", OAuthPolicy.class);
    static final ResourceReference<SamlPolicy> SAML_POLICY =
            new ResourceReference<SamlPolicy>("samlPolicy", SamlPolicy.class);
    static final ResourceReference<AccountLinkingPolicy> ACCOUNT_LINKING_POLICY =
            new ResourceReference<AccountLinkingPolicy>("accountLinkingPolicy", AccountLinkingPolicy.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account>                         ACCOUNTS               =
        new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group>                             GROUPS                 =
        new CollectionReference<GroupList, Group>("groups", GroupList.class, Group.class);
    static final CollectionReference<ApplicationAccountStoreMappingList, ApplicationAccountStoreMapping> ACCOUNT_STORE_MAPPINGS =
        new CollectionReference<ApplicationAccountStoreMappingList, ApplicationAccountStoreMapping>("accountStoreMappings",
                                                                              ApplicationAccountStoreMappingList.class,
                                                                              ApplicationAccountStoreMapping.class);
    static final CollectionReference<PasswordResetTokenList, PasswordResetToken>   PASSWORD_RESET_TOKENS  =
        new CollectionReference<PasswordResetTokenList, PasswordResetToken>("passwordResetTokens",
                                                                            PasswordResetTokenList.class,
                                                                            PasswordResetToken.class);

    // LIST PROPERTIES
    static final ListProperty AUTHORIZED_CALLBACK_URIS = new ListProperty("authorizedCallbackUris");

    public static final String AUTHORIZED_CALLBACK_URIS_PROPERTY_NAME = "authorizedCallbackUris";

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
        NAME, DESCRIPTION, STATUS, TENANT, DEFAULT_ACCOUNT_STORE_MAPPING, DEFAULT_GROUP_STORE_MAPPING, ACCOUNTS, GROUPS,
        ACCOUNT_STORE_MAPPINGS, PASSWORD_RESET_TOKENS, CUSTOM_DATA, OAUTH_POLICY, AUTHORIZED_CALLBACK_URIS, SAML_POLICY, ACCOUNT_LINKING_POLICY);

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
        return getDataStore().getResource(list.getHref(), AccountList.class, (Criteria<AccountCriteria>) criteria);
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
        return getDataStore().getResource(groups.getHref(), GroupList.class, (Criteria<GroupCriteria>) criteria);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email) {
        PasswordResetToken token = createPasswordResetToken(email, null);
        return token;
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email, AccountStore accountStore) throws ResourceException {
        PasswordResetToken token = createPasswordResetToken(email, accountStore);
        return token;
    }

    private PasswordResetToken createPasswordResetToken(String email, AccountStore accountStore) {
        DefaultPasswordResetToken passwordResetToken = (DefaultPasswordResetToken) getDataStore().instantiate(PasswordResetToken.class);
        passwordResetToken.setEmail(email);
        if (accountStore != null) {
            passwordResetToken.setAccountStore(accountStore);
        }
        String href = getPasswordResetTokensHref();
        return getDataStore().create(href, passwordResetToken);
    }

    private String getPasswordResetTokensHref() {
        Map<String, String> passwordResetTokensLink =
            (Map<String, String>) getProperty(PASSWORD_RESET_TOKENS.getName());
        return passwordResetTokensLink.get(HREF_PROP_NAME);
    }

    // @since 1.0.RC8
    public SamlPolicy getSamlPolicy() {
        return getResourceProperty(SAML_POLICY);
    }

    // @since 1.0.RC8
    public List<String> getAuthorizedCallbackUris() {
        return new ArrayList<String>(getListProperty(AUTHORIZED_CALLBACK_URIS_PROPERTY_NAME));
    }

    // @since 1.0.RC8
    @Override
    public Application setAuthorizedCallbackUris(List<String> authorizedCallbackUris) {
        setProperty(AUTHORIZED_CALLBACK_URIS_PROPERTY_NAME, authorizedCallbackUris);
        save();
        return this;
    }

    // @since 1.0.RC8
    @Override
    public Application addAuthorizedCallbackUri(String authorizedCallbackUri) {
        // validate URI
        this.validateUri(authorizedCallbackUri);

        List<String> authorizedCallbackUris = this.getAuthorizedCallbackUris();
        authorizedCallbackUris.add(authorizedCallbackUri);
        setProperty(AUTHORIZED_CALLBACK_URIS_PROPERTY_NAME, authorizedCallbackUris);
        save();
        return this;
    }

    @Override
    public Account verifyPasswordResetToken(String token) {
        String href = getPasswordResetTokensHref() + "/" + token;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        PasswordResetToken prToken = getDataStore().instantiate(PasswordResetToken.class, props);
        return prToken.getAccount();
    }

    /** @since 1.0.RC7 */
    public OAuthPolicy getOAuthPolicy() {
        return getResourceProperty(OAUTH_POLICY);
    }

    /** @since 1.0.RC */
    @Override
    public Account resetPassword(String passwordResetToken, String newPassword) {
        Assert.hasText(passwordResetToken, "passwordResetToken cannot be empty or null.");
        Assert.hasText(newPassword, "newPassword cannot be empty or null.");
        String href = getPasswordResetTokensHref() + "/" + passwordResetToken;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        DefaultPasswordResetToken instantiatedToken = (DefaultPasswordResetToken) getDataStore().instantiate(PasswordResetToken.class, props);
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
    public void delete() {
        getDataStore().delete(this);
    }

    /** @since 0.9 */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings() {
        return getResourceProperty(ACCOUNT_STORE_MAPPINGS);
    }

    /** @since 0.9 */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        ApplicationAccountStoreMappingList accountStoreMappings =
            getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), ApplicationAccountStoreMappingList.class, queryParams);
    }

    /** @since 1.0.RC9 */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria) {
        ApplicationAccountStoreMappingList accountStoreMappings =
            getAccountStoreMappings(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(accountStoreMappings.getHref(), ApplicationAccountStoreMappingList.class, (Criteria<ApplicationAccountStoreMappingCriteria>) criteria);
    }

    /** @since 0.9 */
    @Override
    public AccountStore getDefaultAccountStore() {
        ApplicationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_ACCOUNT_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /** @since 0.9 */
    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        ApplicationAccountStoreMappingList applicationAccountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (ApplicationAccountStoreMapping accountStoreMapping : applicationAccountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultAccountStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            ApplicationAccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultAccountStore(true);
            mapping.save();
            setProperty(DEFAULT_ACCOUNT_STORE_MAPPING, mapping);
        }
        save();
    }

    /** @since 0.9 */
    @Override
    public AccountStore getDefaultGroupStore() {
        ApplicationAccountStoreMapping accountStoreMap = getResourceProperty(DEFAULT_GROUP_STORE_MAPPING);
        return accountStoreMap == null ? null : accountStoreMap.getAccountStore();
    }

    /** @since 0.9 */
    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        ApplicationAccountStoreMappingList applicationAccountStoreMappingList = getAccountStoreMappings();
        boolean needToCreateNewStore = true;
        for (ApplicationAccountStoreMapping accountStoreMapping : applicationAccountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(accountStore.getHref())) {
                needToCreateNewStore = false;
                accountStoreMapping.setDefaultGroupStore(true);
                accountStoreMapping.save();
                setProperty(DEFAULT_GROUP_STORE_MAPPING, accountStoreMapping);
                break;
            }
        }
        if (needToCreateNewStore) {
            ApplicationAccountStoreMapping mapping = addAccountStore(accountStore);
            mapping.setDefaultGroupStore(true);
            mapping.save();
            setProperty(DEFAULT_GROUP_STORE_MAPPING, mapping);
        }
        save();
    }

    /** @since 0.9 */
    @Override
    public ApplicationAccountStoreMapping createAccountStoreMapping(ApplicationAccountStoreMapping mapping) throws ResourceException {
        String href = getAccountStoreMappingsHref();
        return getDataStore().create(href, mapping);
    }

    /** @since 0.9 */
    @Override
    public ApplicationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        ApplicationAccountStoreMapping accountStoreMapping = getDataStore().instantiate(ApplicationAccountStoreMapping.class);
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
        ApiKeyList apiKeys = getDataStore().getResource(href, ApiKeyList.class, (Criteria<ApiKeyCriteria>) criteria);

        Iterator<ApiKey> iterator = apiKeys.iterator();

        // we expect only one api key to be in the collection
        return iterator.hasNext() ? iterator.next() : null;
    }

    // since 1.0.RC8
    private void validateUri(String uri){
        try {
            URL url = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(INVALID_URI_FORMAT_MSG);
        }
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

    /** @since 1.0.RC */
    @Override
    public IdSiteUrlBuilder newIdSiteUrlBuilder() {
        return new DefaultIdSiteUrlBuilder(getDataStore(), getHref());
    }

    /** @since 1.0.RC8 */
    @Override
    public SamlIdpUrlBuilder newSamlIdpUrlBuilder() {
        return new DefaultSamlIdpUrlBuilder(getDataStore(), getHref(), this.getSamlPolicy().getSamlServiceProvider().getSsoInitiationEndpoint().getHref());
    }

    /** @since 1.0.RC */
    @Override
    public IdSiteCallbackHandler newIdSiteCallbackHandler(Object httpRequest) {

        validateHttpRequest(httpRequest);

        return new DefaultIdSiteCallbackHandler(getDataStore(), this, httpRequest);
    }

    /** @since 1.0.RC8 */
    @Override
    public SamlCallbackHandler newSamlCallbackHandler(Object httpRequest) {

        validateHttpRequest(httpRequest);

        return new DefaultSamlCallbackHandler(getDataStore(), this, httpRequest);
    }

    /** @since 1.0.0 */
    public void sendVerificationEmail(VerificationEmailRequest verificationEmailRequest) {
        Assert.notNull(verificationEmailRequest, "verificationEmailRequest must not be null.");
        Assert.hasText(verificationEmailRequest.getLogin(), "verificationEmailRequest's email property is required.");

        AccountStore accountStore = verificationEmailRequest.getAccountStore();
        if(accountStore != null && accountStore.getHref() == null) {
            throw new IllegalArgumentException("verificationEmailRequest's accountStore has been specified but its href is null.");
        }

        String href = getHref() + "/verificationEmails";
        //We are passing a dummy return type (VerificationEmailRequest). It is not actually needed, but if we use the
        //the two-parameters create(...) operation, we get an exception caused by an empty response body from the backend
        getDataStore().create(href, (DefaultVerificationEmailRequest) verificationEmailRequest, DefaultVerificationEmailRequest.class);
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

    /** @since 1.0.RC3 */
    @Override
    public ApplicationAccountStoreMapping addAccountStore(String hrefOrName) {
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
                } else if (splitHrefOrName[4].equals("organizations")){
                    accountStoreType = Organization.class;
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

    /** @since 1.0.RC3 */
    @Override
    public ApplicationAccountStoreMapping addAccountStore(DirectoryCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");
        Directory directory = getSingleTenantDirectory(criteria);
        if (directory != null) {
            return addAccountStore(directory);
        }
        //No directory matching the given information could be found; therefore no account store can be added. We return null...
        return null;
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public ApplicationAccountStoreMapping addAccountStore(OrganizationCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");
        Organization organization = getSingleOrganization(criteria);
        if (organization != null) {
            return addAccountStore(organization);
        }
        //No organization matching the given information could be found; therefore no account store can be added. Return null.
        return null;
    }

    /** @since 1.0.RC3 */
    @Override
    public ApplicationAccountStoreMapping addAccountStore(GroupCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");
        Group group = getSingleTenantGroup(criteria);
        if (group != null) {
            return addAccountStore(group);
        }
        //No group matching the given information could be found; therefore no account store can be added. We return null...
        return null;
    }

    /**
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     * @since 1.0.RC3
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
     * @since 1.0.RC3
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

    /**
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     * @since 1.0.RC5
     * */
    private Organization getSingleOrganization(OrganizationCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");

        Tenant tenant = getDataStore().getResource("/tenants/current", Tenant.class);
        OrganizationList organizations = tenant.getOrganizations(criteria);
        Organization found = null;
        int count = 0;

        //There cannot be more than one organization with the same name in a single tenant. Thus, the group list will have either
        //zero or one items, never more.
        for (Organization org : organizations) {
            count++;
            found = org;
        }
        if(count > 1) {
            //The provided criteria matched more than one Organizations in the tenant, we will throw
            throw new IllegalArgumentException("The provided criteria matched more than one Organization in the current Tenant.");
        }
        return found;
    }

    /**
     * @since 1.0.RC4.6
     */
    @Override
    public Application saveWithResponseOptions(ApplicationOptions responseOptions) {
        Assert.notNull(responseOptions, "responseOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, responseOptions);
        return this;
    }

    /* @since 1.0.0 */
    public OAuthClientCredentialsGrantRequestAuthenticator createClientCredentialsGrantAuthenticator() {
        return new DefaultOAuthClientCredentialsGrantRequestAuthenticator(this, getDataStore());
    }

    /* @since 1.1.0 */
    public OAuthStormpathSocialGrantRequestAuthenticator createStormpathSocialGrantAuthenticator() {
        return new DefaultOAuthStormpathSocialGrantRequestAuthenticator(this, getDataStore());
    }

    /* @since 1.0.RC7 */
    public OAuthPasswordGrantRequestAuthenticator createPasswordGrantAuthenticator() {
        return new DefaultOAuthPasswordGrantRequestAuthenticator(this, getDataStore());
    }

    /* @since 1.0.RC7 */
    public OAuthRefreshTokenRequestAuthenticator createRefreshGrantAuthenticator() {
        return new DefaultOAuthRefreshTokenRequestAuthenticator(this, getDataStore());
    }

    /* @since 1.0.RC7 */
    public OAuthBearerRequestAuthenticator createJwtAuthenticator() {
        return new DefaultOAuthBearerRequestAuthenticator(this, getDataStore());
    }

    /* @since 1.0.RC8.2 */
    public IdSiteAuthenticator createIdSiteAuthenticator() {
        return new DefaultIdSiteAuthenticator(this, getDataStore());
    }

    /* @since 1.1.0 */
    @Override
    public AccountLinkingPolicy getAccountLinkingPolicy() {
        return getResourceProperty(ACCOUNT_LINKING_POLICY);
    }
}
