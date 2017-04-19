package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.directory.CreateDirectoryRequest;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.organization.CreateOrganizationRequest;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;
import com.stormpath.sdk.saml.RegisteredSamlServiceProviderCriteria;
import com.stormpath.sdk.saml.RegisteredSamlServiceProviderList;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantOptions;

import java.util.Map;

/**
 *
 */
public class OktaTenant implements Tenant {

    private final OktaApplication application;

    public OktaTenant(OktaApplication application) {
        this.application = application;
    }

    @Override
    public Account verifyAccountEmail(String token) {
        return application.verifyAccountEmail(token);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("save() method hasn't been implemented.");
    }

    @Override
    public String getHref() {
        throw new UnsupportedOperationException("getHref() method hasn't been implemented.");
    }

    @Override
    public CustomData getCustomData() {
        throw new UnsupportedOperationException("getCustomData() method hasn't been implemented.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() method hasn't been implemented.");
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("getKey() method hasn't been implemented.");
    }

    @Override
    public Tenant saveWithResponseOptions(TenantOptions responseOptions) {
        throw new UnsupportedOperationException("saveWithResponseOptions() method hasn't been implemented.");
    }

    @Override
    public Application createApplication(Application application) throws ResourceException {
        throw new UnsupportedOperationException("createApplication() method hasn't been implemented.");
    }

    @Override
    public Application createApplication(CreateApplicationRequest request) throws ResourceException {
        throw new UnsupportedOperationException("createApplication() method hasn't been implemented.");
    }

    @Override
    public ApplicationList getApplications() {
        throw new UnsupportedOperationException("getApplications() method hasn't been implemented.");
    }

    @Override
    public ApplicationList getApplications(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getApplications() method hasn't been implemented.");
    }

    @Override
    public ApplicationList getApplications(ApplicationCriteria criteria) {
        throw new UnsupportedOperationException("getApplications() method hasn't been implemented.");
    }

    @Override
    public Directory createDirectory(Directory directory) {
        throw new UnsupportedOperationException("createDirectory() method hasn't been implemented.");
    }

    @Override
    public Organization createOrganization(Organization organization) {
        throw new UnsupportedOperationException("createOrganization() method hasn't been implemented.");
    }

    @Override
    public Organization createOrganization(CreateOrganizationRequest request) throws ResourceException {
        throw new UnsupportedOperationException("createOrganization() method hasn't been implemented.");
    }

    @Override
    public OrganizationList getOrganizations() {
        throw new UnsupportedOperationException("getOrganizations() method hasn't been implemented.");
    }

    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getOrganizations() method hasn't been implemented.");
    }

    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        throw new UnsupportedOperationException("getOrganizations() method hasn't been implemented.");
    }

    @Override
    public Directory createDirectory(CreateDirectoryRequest createDirectoryRequest) throws ResourceException {
        throw new UnsupportedOperationException("createDirectory() method hasn't been implemented.");
    }

    @Override
    public DirectoryList getDirectories() {
        throw new UnsupportedOperationException("getDirectories() method hasn't been implemented.");
    }

    @Override
    public DirectoryList getDirectories(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getDirectories() method hasn't been implemented.");
    }

    @Override
    public DirectoryList getDirectories(DirectoryCriteria criteria) {
        throw new UnsupportedOperationException("getDirectories() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts() {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups() {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public RegisteredSamlServiceProvider createRegisterdSamlServiceProvider(RegisteredSamlServiceProvider registeredSamlServiceProvider) throws ResourceException {
        throw new UnsupportedOperationException("createRegisterdSamlServiceProvider() method hasn't been implemented.");
    }

    @Override
    public RegisteredSamlServiceProviderList getRegisterdSamlServiceProviders() {
        throw new UnsupportedOperationException("getRegisterdSamlServiceProviders() method hasn't been implemented.");
    }

    @Override
    public RegisteredSamlServiceProviderList getRegisterdSamlServiceProviders(RegisteredSamlServiceProviderCriteria criteria) {
        throw new UnsupportedOperationException("getRegisterdSamlServiceProviders() method hasn't been implemented.");
    }
}
