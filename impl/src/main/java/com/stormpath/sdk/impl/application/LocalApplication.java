package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountLinkingPolicy;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.impl.directory.OktaDirectory;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractCollectionResource;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.saml.SamlPolicy;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LocalApplication extends AbstractResource implements Application {

    private final Directory OKTA_TENANT_DIR;

    private final ApplicationAccountStoreMappingList applicationAccountStoreMappingList;

    private String name;

    public LocalApplication(InternalDataStore dataStore) {
        this(dataStore, new LinkedHashMap<String, Object>());
    }

    public LocalApplication(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.OKTA_TENANT_DIR = new OktaDirectory(dataStore);

        Map<String, Object> mappingProperties = new LinkedHashMap<>();
        mappingProperties.put("href", dataStore.getBaseUrl());
        mappingProperties.put("application", this);
        mappingProperties.put("accountStore", OKTA_TENANT_DIR);

        ApplicationAccountStoreMapping mapping = new DefaultApplicationAccountStoreMapping(dataStore, mappingProperties);

        applicationAccountStoreMappingList = AbstractCollectionResource.singletonCollectionResource(dataStore, DefaultApplicationAccountStoreMappingList.class, mapping);
    }

    @Override
    public AccountLinkingPolicy getAccountLinkingPolicy() {
        throw new UnsupportedOperationException("getAccountLinkingPolicy() method hasn't been implemented.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("save() method hasn't been implemented.");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("delete() method hasn't been implemented.");
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("getPropertyDescriptors() method hasn't been implemented.");
    }

    @Override
    public String getHref() {
        return getDataStore().getBaseUrl();
    }

    @Override
    public Date getCreatedAt() {
        throw new UnsupportedOperationException("getCreatedAt() method hasn't been implemented.");
    }

    @Override
    public Date getModifiedAt() {
        throw new UnsupportedOperationException("getModifiedAt() method hasn't been implemented.");
    }

    @Override
    public CustomData getCustomData() {
        throw new UnsupportedOperationException("getCustomData() method hasn't been implemented.");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Application setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("getDescription() method hasn't been implemented.");
    }

    @Override
    public Application setDescription(String description) {
        throw new UnsupportedOperationException("setDescription() method hasn't been implemented.");
    }

    @Override
    public ApplicationStatus getStatus() {
        throw new UnsupportedOperationException("getStatus() method hasn't been implemented.");
    }

    @Override
    public Application setStatus(ApplicationStatus status) {
        throw new UnsupportedOperationException("setStatus() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts() {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public Account createAccount(Account account) throws ResourceException {
        return OKTA_TENANT_DIR.createAccount(account);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) throws ResourceException {
        return OKTA_TENANT_DIR.createAccount(request);
    }

    @Override
    public GroupList getGroups() {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public Group createGroup(Group group) throws ResourceException {
        throw new UnsupportedOperationException("createGroup() method hasn't been implemented.");
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        throw new UnsupportedOperationException("createGroup() method hasn't been implemented.");
    }

    @Override
    public Tenant getTenant() {
        throw new UnsupportedOperationException("getTenant() method hasn't been implemented.");
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email) throws ResourceException {
        throw new UnsupportedOperationException("sendPasswordResetEmail() method hasn't been implemented.");
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email, AccountStore accountStore) throws ResourceException {
        throw new UnsupportedOperationException("sendPasswordResetEmail() method hasn't been implemented.");
    }

    @Override
    public Account verifyPasswordResetToken(String token) {
        throw new UnsupportedOperationException("verifyPasswordResetToken() method hasn't been implemented.");
    }

    @Override
    public Account resetPassword(String passwordResetToken, String newPassword) {
        throw new UnsupportedOperationException("resetPassword() method hasn't been implemented.");
    }

    @Override
    public AuthenticationResult authenticateAccount(AuthenticationRequest request) throws ResourceException {
        throw new UnsupportedOperationException("authenticateAccount() method hasn't been implemented.");
    }

    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) {
        throw new UnsupportedOperationException("getAccount() method hasn't been implemented.");
    }

    /**
     * @since 0.9
     */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings() {
        return applicationAccountStoreMappingList;
    }

    /**
     * @since 0.9
     */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        return applicationAccountStoreMappingList;
    }

    /**
     * @since 1.0.RC9
     */
    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria) {
        return applicationAccountStoreMappingList;
    }


    @Override
    public ApplicationAccountStoreMapping createAccountStoreMapping(ApplicationAccountStoreMapping mapping) throws ResourceException {
        throw new UnsupportedOperationException("createAccountStoreMapping() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException {
        throw new UnsupportedOperationException("getApiKey() method hasn't been implemented.");
    }

    @Override
    public ApiKey getApiKey(String id, ApiKeyOptions options) throws ResourceException, IllegalArgumentException {
        throw new UnsupportedOperationException("getApiKey() method hasn't been implemented.");
    }

    @Override
    public IdSiteUrlBuilder newIdSiteUrlBuilder() {
        throw new UnsupportedOperationException("newIdSiteUrlBuilder() method hasn't been implemented.");
    }

    @Override
    public SamlIdpUrlBuilder newSamlIdpUrlBuilder() {
        throw new UnsupportedOperationException("newSamlIdpUrlBuilder() method hasn't been implemented.");
    }

    @Override
    public IdSiteCallbackHandler newIdSiteCallbackHandler(Object httpRequest) {
        throw new UnsupportedOperationException("newIdSiteCallbackHandler() method hasn't been implemented.");
    }

    @Override
    public SamlCallbackHandler newSamlCallbackHandler(Object httpRequest) {
        throw new UnsupportedOperationException("newSamlCallbackHandler() method hasn't been implemented.");
    }

    @Override
    public void sendVerificationEmail(VerificationEmailRequest verificationEmailRequest) {
        throw new UnsupportedOperationException("sendVerificationEmail() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(String hrefOrName) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(DirectoryCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(OrganizationCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(GroupCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public Application saveWithResponseOptions(ApplicationOptions responseOptions) {
        throw new UnsupportedOperationException("saveWithResponseOptions() method hasn't been implemented.");
    }

    @Override
    public OAuthPolicy getOAuthPolicy() {
        throw new UnsupportedOperationException("getOAuthPolicy() method hasn't been implemented.");
    }

    @Override
    public SamlPolicy getSamlPolicy() {
        throw new UnsupportedOperationException("getSamlPolicy() method hasn't been implemented.");
    }

    @Override
    public ApplicationWebConfig getWebConfig() {
        return null;
        //throw new UnsupportedOperationException("getWebConfig() method hasn't been implemented.");
    }

    @Override
    public List<String> getAuthorizedCallbackUris() {
        throw new UnsupportedOperationException("getAuthorizedCallbackUris() method hasn't been implemented.");
    }

    @Override
    public Application setAuthorizedCallbackUris(List<String> authorizedCallbackUris) {
        throw new UnsupportedOperationException("setAuthorizedCallbackUris() method hasn't been implemented.");
    }

    @Override
    public Application addAuthorizedCallbackUri(String authorizedCallbackUri) {
        throw new UnsupportedOperationException("addAuthorizedCallbackUri() method hasn't been implemented.");
    }

    @Override
    public List<String> getAuthorizedOriginUris() {
        throw new UnsupportedOperationException("getAuthorizedOriginUris() method hasn't been implemented.");
    }

    @Override
    public Application setAuthorizedOriginUris(List<String> authorizedOriginUris) {
        throw new UnsupportedOperationException("setAuthorizedOriginUris() method hasn't been implemented.");
    }

    @Override
    public Application addAuthorizedOriginUri(String authorizedOriginUri) {
        throw new UnsupportedOperationException("addAuthorizedOriginUri() method hasn't been implemented.");
    }

    @Override
    public AccountStore getDefaultAccountStore() {
        return OKTA_TENANT_DIR;
    }

    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        throw new UnsupportedOperationException("setDefaultAccountStore() method hasn't been implemented.");
    }

    @Override
    public AccountStore getDefaultGroupStore() {
        return OKTA_TENANT_DIR;
    }

    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        throw new UnsupportedOperationException("setDefaultGroupStore() method hasn't been implemented.");
    }
}
