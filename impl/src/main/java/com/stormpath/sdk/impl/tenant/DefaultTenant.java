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
package com.stormpath.sdk.impl.tenant;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.directory.CreateDirectoryRequest;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.application.CreateApplicationAndDirectoryRequest;
import com.stormpath.sdk.impl.application.CreateApplicationRequestVisitor;
import com.stormpath.sdk.impl.application.DefaultCreateApplicationRequest;
import com.stormpath.sdk.impl.directory.DefaultDirectory;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.organization.CreateOrganizationAndDirectoryRequest;
import com.stormpath.sdk.impl.organization.CreateOrganizationRequestVisitor;
import com.stormpath.sdk.impl.organization.DefaultCreateOrganizationRequest;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.*;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.phone.PhoneList;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantOptions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultTenant extends AbstractExtendableInstanceResource implements Tenant {

    // SIMPLE PROPERTIES:
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty KEY = new StringProperty("key");

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<ApplicationList, Application> APPLICATIONS =
            new CollectionReference<>("applications", ApplicationList.class, Application.class);
    static final CollectionReference<DirectoryList, Directory> DIRECTORIES =
            new CollectionReference<>("directories", DirectoryList.class, Directory.class);
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupList, Group> GROUPS =
            new CollectionReference<>("groups", GroupList.class, Group.class);
    static final CollectionReference<OrganizationList, Organization> ORGANIZATIONS =
            new CollectionReference<>("organizations", OrganizationList.class, Organization.class);
    static final CollectionReference<PhoneList, Phone> PHONES =
            new CollectionReference<>("phones", PhoneList.class, Phone.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, KEY, APPLICATIONS, DIRECTORIES, CUSTOM_DATA, ACCOUNTS, GROUPS, ORGANIZATIONS,PHONES);

    public DefaultTenant(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultTenant(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public String getKey() {
        return getString(KEY);
    }

    @Override
    public Application createApplication(Application application) {
        CreateApplicationRequest request = Applications.newCreateRequestFor(application).build();
        return createApplication(request);
    }

    @Override
    public Application createApplication(CreateApplicationRequest ar) {
        Assert.isInstanceOf(DefaultCreateApplicationRequest.class, ar);
        DefaultCreateApplicationRequest request = (DefaultCreateApplicationRequest) ar;

        final Application application = request.getApplication();
        final String[] href = new String[]{"/" + APPLICATIONS.getName()};

        request.accept(new CreateApplicationRequestVisitor() {
            @Override
            public void visit(DefaultCreateApplicationRequest ignored) {
            }

            @Override
            public void visit(CreateApplicationAndDirectoryRequest request) {
                String name = request.getDirectoryName();
                if (name == null) {
                    name = "true"; //boolean true means 'auto name the directory'
                }
                href[0] += "?createDirectory=" + name;
            }
        });

        return getDataStore().create(href[0], application);
    }

    @Override
    public ApplicationList getApplications() {
        return getResourceProperty(APPLICATIONS);
    }

    @Override
    public ApplicationList getApplications(Map<String, Object> queryParams) {
        ApplicationList proxy = getApplications(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, queryParams);
    }

    @Override
    public ApplicationList getApplications(ApplicationCriteria criteria) {
        ApplicationList proxy = getApplications(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, (Criteria<ApplicationCriteria>) criteria);
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public Organization createOrganization(Organization organization) {
        CreateOrganizationRequest request = Organizations.newCreateRequestFor(organization).build();
        return createOrganization(request);
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public Organization createOrganization(CreateOrganizationRequest orgRequest) throws ResourceException {
        Assert.isInstanceOf(DefaultCreateOrganizationRequest.class, orgRequest);
        DefaultCreateOrganizationRequest request = (DefaultCreateOrganizationRequest) orgRequest;

        final Organization organization = request.getOrganization();
        final String[] href = new String[]{"/" + ORGANIZATIONS.getName()};

        request.accept(new CreateOrganizationRequestVisitor() {
            @Override
            public void visit(DefaultCreateOrganizationRequest ignored) {
            }

            @Override
            public void visit(CreateOrganizationAndDirectoryRequest request) {
                String name = request.getDirectoryName();
                if (name == null) {
                    name = "true"; //boolean true means 'auto name the directory'
                }
                href[0] += "?createDirectory=" + name;
            }
        });

        return getDataStore().create(href[0], organization);
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public OrganizationList getOrganizations() {
        return getResourceProperty(ORGANIZATIONS);
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        //This is just a proxy - does not execute a query until iteration occurs
        OrganizationList proxy = getOrganizations();
        return getDataStore().getResource(proxy.getHref(), OrganizationList.class, queryParams);
    }

    /**
     * @since 1.0.RC5
     */
    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        OrganizationList proxy = getOrganizations(); // this is just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), OrganizationList.class, (Criteria<OrganizationCriteria>) criteria);
    }

    @Override
    public Directory createDirectory(Directory directory) {
        Assert.notNull(directory, "Directory instance cannot be null.");
        return getDataStore().create("/" + DIRECTORIES.getName(), directory);
    }

    /**
     * @since 1.0.beta
     */
    @Override
    public Directory createDirectory(CreateDirectoryRequest createDirectoryRequest) {
        Assert.notNull(createDirectoryRequest, "createDirectoryRequest cannot be null.");
        Assert.notNull(createDirectoryRequest.getDirectory(), "the specified directory cannot be null.");

        Directory directory = createDirectoryRequest.getDirectory();
        if(createDirectoryRequest.getProvider() != null) {
            Assert.isAssignable(DefaultDirectory.class, directory.getClass(), "the directory instance is of " +
                    "an unidentified type. The specified provider cannot be set to it: " + createDirectoryRequest.getDirectory());
            ((DefaultDirectory)directory).setProvider(createDirectoryRequest.getProvider());
        }

        return getDataStore().create("/" + DIRECTORIES.getName(), directory);
    }

    @Override
    public DirectoryList getDirectories() {
        return getResourceProperty(DIRECTORIES);
    }

    @Override
    public DirectoryList getDirectories(Map<String, Object> queryParams) {
        DirectoryList proxy = getDirectories(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), DirectoryList.class, queryParams);
    }

    @Override
    public DirectoryList getDirectories(DirectoryCriteria criteria) {
        DirectoryList proxy = getDirectories(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), DirectoryList.class, (Criteria<DirectoryCriteria>) criteria);
    }

    @Override
    public Account verifyAccountEmail(String token) {

        //TODO enable auto discovery via Tenant resource (should be just /emailVerificationTokens
        String href = "/accounts/emailVerificationTokens/" + token;

        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put(HREF_PROP_NAME, href);

        EmailVerificationToken evToken = getDataStore().instantiate(EmailVerificationToken.class, props);

        //execute a POST (should clean this up / make it more obvious)
        return getDataStore().save(evToken, Account.class);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        AccountList proxy = getAccounts(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), AccountList.class, (Criteria<AccountCriteria>) criteria);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        AccountList proxy = getAccounts(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), AccountList.class, queryParams);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups() {
        return getResourceProperty(GROUPS);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        GroupList proxy = getGroups(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), GroupList.class, (Criteria<GroupCriteria>) criteria);
    }

    /**
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        GroupList proxy = getGroups(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), GroupList.class, queryParams);
    }

    /**
     * @since 1.0.RC4.6
     */
    @Override
    public Tenant saveWithResponseOptions(TenantOptions responseOptions) {
        Assert.notNull(responseOptions, "responseOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, responseOptions);
        return this;
    }
}
