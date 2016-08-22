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
package com.stormpath.sdk.impl.client;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.directory.CreateDirectoryRequest;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.ds.DefaultDataStore;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.authc.RequestAuthenticatorFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.organization.*;
import com.stormpath.sdk.query.Options;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantOptions;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * The default {@link Client} implementation.
 * <h3>DataStore API</h3>
 * <p>As of 0.8, this class implements the {@link
 * DataStore} interface, but this implementation merely acts as a wrapper to the underlying 'real' {@code DataStore}
 * instance. This is a convenience mechanism to eliminate the constant need to call {@code client.getDataStore()} every
 * time one needs to instantiate or look up a Resource.</p>
 *
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 * @since 1.0.alpha
 */
public class DefaultClient implements Client {

    private final DataStore dataStore;

    private String currentTenantHref;

    /**
     * Instantiates a new Client instance that will communicate with the Stormpath REST API.  See the class-level
     * JavaDoc for a usage example.
     *
     * @param stormpathCredentials the Stormpath account credentials that will be used to authenticate the client with
     *                             Stormpath's API server
     * @param baseUrl              the Stormpath base URL
     * @param proxy                the HTTP proxy to be used when communicating with the Stormpath API server (can be
     *                             null)
     * @param cacheManager         the {@link com.stormpath.sdk.cache.CacheManager} that should be used to cache
     *                             Stormpath REST resources (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Stormpath API
     *                             server (can be null)
     */
    public DefaultClient(StormpathCredentials stormpathCredentials, String baseUrl, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {
        Assert.notNull(stormpathCredentials, "stormpathCredentials argument cannot be null.");
        Assert.isTrue(connectionTimeout >= 0, "connectionTimeout cannot be a negative number.");
        RequestExecutor requestExecutor = createRequestExecutor(stormpathCredentials, proxy, authenticationScheme, requestAuthenticatorFactory,connectionTimeout);
        this.dataStore = createDataStore(requestExecutor, baseUrl, stormpathCredentials, cacheManager);
    }

    protected DataStore createDataStore(RequestExecutor requestExecutor, String baseUrl, StormpathCredentials stormpathCredentials, CacheManager cacheManager) {
        return new DefaultDataStore(requestExecutor, baseUrl, stormpathCredentials, cacheManager);
    }

    @Override
    public Tenant getCurrentTenant() {
        String href = currentTenantHref;
        if (href == null) {
            href = "/tenants/current";
        }
        Tenant current = this.dataStore.getResource(href, Tenant.class);
        this.currentTenantHref = current.getHref();
        return current;
    }

    @Override
    public ApiKey getApiKey() {
        return this.dataStore.getApiKey();
    }

    @Override
    public CacheManager getCacheManager() {
        return this.dataStore.getCacheManager();
    }

    @Override
    public DataStore getDataStore() {
        return this.dataStore;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private RequestExecutor createRequestExecutor(StormpathCredentials stormpathCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {

        String className = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor";

        Class requestExecutorClass;

        if (Classes.isAvailable(className)) {
            requestExecutorClass = Classes.forName(className);
        } else {
            //we might be able to check for other implementations in the future, but for now, we only support
            //HTTP calls via the HttpClient.  Throw an exception:

            String msg = "Unable to find the '" + className + "' implementation on the classpath.  Please ensure you " +
                    "have added the stormpath-sdk-httpclient .jar file to your runtime classpath.";
            throw new RuntimeException(msg);
        }

        Constructor<RequestExecutor> ctor = Classes.getConstructor(requestExecutorClass, StormpathCredentials.class, Proxy.class, AuthenticationScheme.class, RequestAuthenticatorFactory.class, Integer.class);

        return Classes.instantiate(ctor, stormpathCredentials, proxy, authenticationScheme, requestAuthenticatorFactory, connectionTimeout);
    }

    // ========================================================================
    // DataStore methods (delegate to underlying DataStore instance)
    // ========================================================================

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to instantiate Resource.
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @return a new instance of the specified Resource.
     */
    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.dataStore.instantiate(clazz);
    }

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to look up a Resource.
     *
     * @param href  the resource URL of the resource to retrieve
     * @param clazz the {@link Resource} sub-interface to instantiate
     * @param <T>   type parameter indicating the returned value is a {@link Resource} instance.
     * @return an instance of the specified class based on the data returned from the specified {@code href} URL.
     */
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return this.dataStore.getResource(href, clazz);
    }

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to look up a Resource.
     *
     * @param href  the URL of the resource to retrieve
     * @param clazz the {@link Resource} sub-interface to instantiate
     * @param options the {@link Options} sub-interface with the properties to expand
     * @param <T>   type parameter indicating the returned value is a {@link Resource} instance.
     * @return an instance of the specified {@code Class} based on the data returned from the specified {@code href} URL.
     * @since 1.0.RC4.6
     */
    @Override
    public <T extends Resource, O extends Options> T getResource(String href, Class<T> clazz, O options) {
        return this.dataStore.getResource(href, clazz, options);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public Application createApplication(Application application) throws ResourceException {
        return getCurrentTenant().createApplication(application);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public Application createApplication(CreateApplicationRequest request) throws ResourceException {
        return getCurrentTenant().createApplication(request);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public ApplicationList getApplications() {
        return getCurrentTenant().getApplications();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public ApplicationList getApplications(Map<String, Object> queryParams) {
        return getCurrentTenant().getApplications(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public ApplicationList getApplications(ApplicationCriteria criteria) {
        return getCurrentTenant().getApplications(criteria);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public Directory createDirectory(Directory directory) {
        return getCurrentTenant().createDirectory(directory);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC7
     */
    @Override
    public Organization createOrganization(Organization organization) {
        return getCurrentTenant().createOrganization(organization);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC7
     */
    @Override
    public Organization createOrganization(CreateOrganizationRequest request) throws ResourceException {
        return getCurrentTenant().createOrganization(request);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC7
     */
    @Override
    public OrganizationList getOrganizations() {
        return getCurrentTenant().getOrganizations();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC7
     */
    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        return getCurrentTenant().getOrganizations(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC7
     */
    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        return getCurrentTenant().getOrganizations(criteria);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public Directory createDirectory(CreateDirectoryRequest createDirectoryRequest) throws ResourceException {
        return getCurrentTenant().createDirectory(createDirectoryRequest);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public DirectoryList getDirectories() {
        return getCurrentTenant().getDirectories();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public DirectoryList getDirectories(Map<String, Object> queryParams) {
        return getCurrentTenant().getDirectories(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public DirectoryList getDirectories(DirectoryCriteria criteria) {
        return getCurrentTenant().getDirectories(criteria);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC
     */
    @Override
    public Account verifyAccountEmail(String token) {
        return getCurrentTenant().verifyAccountEmail(token);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts() {
        return getCurrentTenant().getAccounts();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        return getCurrentTenant().getAccounts(criteria);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        return getCurrentTenant().getAccounts(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups() {
        return getCurrentTenant().getGroups();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        return getCurrentTenant().getGroups(criteria);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC3
     */
    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        return getCurrentTenant().getGroups(queryParams);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.RC4.6
     */
    @Override
    public Tenant getCurrentTenant(TenantOptions tenantOptions) {
        String href = currentTenantHref;
        if (href == null) {
            href = "/tenants/current";
        }

        Tenant current = this.dataStore.getResource(href, Tenant.class, tenantOptions);
        this.currentTenantHref = current.getHref();
        return current;
    }
}
