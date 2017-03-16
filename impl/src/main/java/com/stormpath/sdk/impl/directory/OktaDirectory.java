package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.directory.AccountCreationPolicy;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryOptions;
import com.stormpath.sdk.directory.DirectoryStatus;
import com.stormpath.sdk.directory.PasswordPolicy;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.provider.OktaProvider;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.schema.Schema;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

public class OktaDirectory extends AbstractResource implements Directory {

    private final Provider OKTA_PROVIDER;

    public OktaDirectory(InternalDataStore dataStore) {
        super(dataStore);
        this.OKTA_PROVIDER = new OktaProvider(dataStore.getBaseUrl(), null, null);
    }

    public OktaDirectory(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.OKTA_PROVIDER = new OktaProvider(dataStore.getBaseUrl(), null, null);
    }

    @Override
    public String getHref() {
        return getDataStore().getBaseUrl();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Date getCreatedAt() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public CustomData getCustomData() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Date getModifiedAt() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getName() {
        return "Okta";
    }

    @Override
    public Directory setName(String name) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getDescription() {
        return "Okta tenant.";
    }

    @Override
    public Directory setDescription(String description) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public DirectoryStatus getStatus() {
        return DirectoryStatus.ENABLED;
    }

    @Override
    public Directory setStatus(DirectoryStatus status) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Account createAccount(Account account) {
        Assert.notNull(account, "Account instance cannot be null.");
        CreateAccountRequest request = Accounts.newCreateRequestFor(account).build();
        return createAccount(request);
    }

    @Override
    public Account createAccount(Account account, boolean registrationWorkflowEnabled) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        String usersHref = getHref() + "/api/v1/users";
        final Account account = request.getAccount();
        return getDataStore().create(usersHref, account);
    }

    @Override
    public AccountList getAccounts() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Tenant getTenant() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Group createGroup(Group group) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Provider getProvider() {
        return OKTA_PROVIDER;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AccountCreationPolicy getAccountCreationPolicy() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Directory saveWithResponseOptions(DirectoryOptions responseOptions) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Schema getAccountSchema() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
