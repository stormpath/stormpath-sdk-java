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
import com.stormpath.sdk.impl.application.CreateApplicationAndDirectoryRequest;
import com.stormpath.sdk.impl.application.CreateApplicationRequestVisitor;
import com.stormpath.sdk.impl.application.DefaultCreateApplicationRequest;
import com.stormpath.sdk.impl.directory.DefaultDirectory;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

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
            new CollectionReference<ApplicationList, Application>("applications", ApplicationList.class, Application.class);
    static final CollectionReference<DirectoryList, Directory> DIRECTORIES =
            new CollectionReference<DirectoryList, Directory>("directories", DirectoryList.class, Directory.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, KEY, APPLICATIONS, DIRECTORIES, CUSTOM_DATA);

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
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, criteria);
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
        return getDataStore().getResource(proxy.getHref(), DirectoryList.class, criteria);
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
}
