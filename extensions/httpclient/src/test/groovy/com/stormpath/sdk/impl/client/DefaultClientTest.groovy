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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationCriteria
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.CreateApplicationRequest
import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.ApiKey
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.CreateDirectoryRequest
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryCriteria
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 *
 * @since 1.0.alpha
 */
class DefaultClientTest {

    @Test
    void testConstructorOK() {

        def apiKey = createStrictMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def proxy = createStrictMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createStrictMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1

        expect(proxy.getHost()).andReturn("192.168.2.110")
        expect(proxy.getPort()).andReturn(777)
        expect(proxy.isAuthenticationRequired()).andReturn(false)

        replay(apiKey, proxy, cacheManager)

        Client client = new DefaultClient(apiKey, baseUrl, proxy, cacheManager, authcScheme)

        assertEquals(client.dataStore.requestExecutor.apiKey, apiKey)

        verify(apiKey, proxy, cacheManager)
    }

    @Test
    void testConstructorApiKeyNull() {

        String baseUrl = "http://localhost:8080/v1"
        def proxy = createMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1

        try {
            new DefaultClient(null, baseUrl, proxy, cacheManager, authcScheme)
            fail("Should have thrown due to null ApiKey")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "apiKey argument cannot be null.")
        }
    }

    @Test
    void testGetCurrentTenant() {

        def apiKey = createStrictMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def proxy = createStrictMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createStrictMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1
        def cache = createStrictMock(Cache)
        def map = createStrictMock(Map)
        def set = createStrictMock(Set)
        def iterator = createNiceMock(Iterator)

        expect(proxy.getHost()).andReturn("192.168.2.110")
        expect(proxy.getPort()).andReturn(777)
        expect(proxy.isAuthenticationRequired()).andReturn(false)
        expect(cacheManager.getCache("com.stormpath.sdk.tenant.Tenant")).andReturn(cache)
        expect(cache.get("http://localhost:8080/v1/tenants/current")).andReturn(map)
        expect(map.isEmpty()).andReturn(false).times(2)
        expect(map.size()).andReturn(1)
        expect(map.entrySet()).andReturn(set)
        expect(set.iterator()).andReturn(iterator)

        replay(apiKey, proxy, cacheManager, cache, map, set, iterator)

        Client client = new DefaultClient(apiKey, baseUrl, proxy, cacheManager, authcScheme)
        client.getCurrentTenant()

        verify(apiKey, proxy, cacheManager, cache, map, set, iterator)
    }

    @Test
    void testGetDataStore() {

        def apiKey = createNiceMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def authcScheme = AuthenticationScheme.SAUTHC1

        Client client = new DefaultClient(apiKey, baseUrl, null, null, authcScheme)

        assertNotNull(client.getDataStore())
        assertEquals(client.getDataStore().baseUrl, baseUrl)
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testTenatActions() {

        def apiKey = createStrictMock(ApiKey)
        def dataStore = createStrictMock(DataStore)
        String baseUrl = "http://localhost:8080/v1"

        def tenant = createStrictMock(Tenant)
        def application = createStrictMock(Application)
        def returnedApplication = createStrictMock(Application)

        def createApplicationRequest = createStrictMock(CreateApplicationRequest)
        def applicationList = createStrictMock(ApplicationList)
        def map = createStrictMock(Map)
        def applicationCriteria = createStrictMock(ApplicationCriteria)
        def directory = createStrictMock(Directory)
        def returnedDir = createStrictMock(Directory)
        def createDirectoryRequest = createStrictMock(CreateDirectoryRequest)
        def directoryList = createStrictMock(DirectoryList)
        def directoryCriteria = createStrictMock(DirectoryCriteria)
        def account = createStrictMock(Account)

        def tenantHref = "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"
        def token = "ExAmPleEmAilVeRiFiCaTiOnTokEnHeRE"

        //createApplication(Application)
        expect(dataStore.getResource("/tenants/current", Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.createApplication(application)).andReturn(returnedApplication)

        //createApplication(CreateApplicationRequest)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.createApplication(createApplicationRequest)).andReturn(returnedApplication)

        //getApplications()
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getApplications()).andReturn(applicationList)

        //getApplications(Map<String, Object>)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getApplications(map)).andReturn(applicationList)

        //getApplications(ApplicationCriteria)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getApplications(applicationCriteria)).andReturn(applicationList)

        //createDirectory(Directory)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.createDirectory(directory)).andReturn(returnedDir)

        //createDirectory(CreateDirectoryRequest)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.createDirectory(createDirectoryRequest)).andReturn(returnedDir)

        //getDirectories()
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getDirectories()).andReturn(directoryList)

        //getDirectories(Map<String, Object>)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getDirectories(map)).andReturn(directoryList)

        //getDirectories(ApplicationCriteria)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.getDirectories(directoryCriteria)).andReturn(directoryList)

        //verifyAccountEmail(String)
        expect(dataStore.getResource(tenantHref, Tenant)).andReturn(tenant)
        expect(tenant.getHref()).andReturn(tenantHref)
        expect(tenant.verifyAccountEmail(token)).andReturn(account)

        replay(apiKey, dataStore, tenant, application, returnedApplication, createApplicationRequest, applicationList,
                map, applicationCriteria, directory, returnedDir, createDirectoryRequest, directoryList, directoryCriteria, account)

        Client client = new DefaultClient(apiKey, baseUrl, null, null, null)
        setNewValue(client, "dataStore", dataStore)
        assertSame(client.createApplication(application), returnedApplication)
        assertSame(client.createApplication(createApplicationRequest), returnedApplication)
        assertSame(client.getApplications(), applicationList)
        assertSame(client.getApplications(map), applicationList)
        assertSame(client.getApplications(applicationCriteria), applicationList)
        assertSame(client.createDirectory(directory), returnedDir)
        assertSame(client.createDirectory(createDirectoryRequest), returnedDir)
        assertSame(client.getDirectories(), directoryList)
        assertSame(client.getDirectories(map), directoryList)
        assertSame(client.getDirectories(directoryCriteria), directoryList)
        assertSame(client.verifyAccountEmail(token), account)

        verify(apiKey, dataStore, tenant, application, returnedApplication, createApplicationRequest, applicationList,
                map, applicationCriteria, directory, returnedDir, createDirectoryRequest, directoryList, directoryCriteria, account)
    }


    /**
     * Allows to set a new value to a final property
     *
     * @since 1.0.RC
     */
    private void setNewValue(Object object, String fieldName, Object value){
        Field field = object.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        int modifiers = field.getModifiers();
        Field modifierField = field.getClass().getDeclaredField("modifiers");
        modifiers = modifiers & ~Modifier.FINAL;
        modifierField.setAccessible(true);
        modifierField.setInt(field, modifiers);
        field.set(object, value);
    }



}
