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
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationCriteria
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.CreateApplicationRequest
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.CreateDirectoryRequest
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryCriteria
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.api.ApiKeyCredentials
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.JacksonMapMarshaller
import com.stormpath.sdk.impl.ds.ResourceFactory
import com.stormpath.sdk.http.Request
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.tenant.Tenant
import org.apache.http.client.params.AllClientPNames
import org.easymock.IArgumentMatcher
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

        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        String baseUrl = "http://localhost:8080/v1"
        def proxy = createStrictMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createStrictMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1
        def connectionTimeout = 990011

        expect(proxy.getHost()).andReturn("192.168.2.110")
        expect(proxy.getPort()).andReturn(777)
        expect(proxy.isAuthenticationRequired()).andReturn(false)

        replay(apiKeyCredentials, proxy, cacheManager)

        Client client = new DefaultClient(apiKeyCredentials, baseUrl, proxy, cacheManager, authcScheme, null, connectionTimeout)

        assertEquals(client.dataStore.requestExecutor.clientCredentials, apiKeyCredentials)
        assertEquals(client.dataStore.requestExecutor.httpClient.getParams().getParameter(AllClientPNames.SO_TIMEOUT), connectionTimeout * 1000)
        assertEquals(client.dataStore.requestExecutor.httpClient.getParams().getParameter(AllClientPNames.CONNECTION_TIMEOUT), connectionTimeout * 1000)

        verify(apiKeyCredentials, proxy, cacheManager)
    }

    @Test
    void testConstructorApiKeyNull() {

        String baseUrl = "http://localhost:8080/v1"
        def proxy = createMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1

        try {
            new DefaultClient(null, baseUrl, proxy, cacheManager, authcScheme, null, 10000)
            fail("Should have thrown due to null ApiKey")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "apiKeyCredentials argument cannot be null.")
        }
    }

    @Test
    void testGetDataStore() {

        def apiKeyCredentials = createNiceMock(ApiKeyCredentials)
        String baseUrl = "http://localhost:8080/v1"
        def authcScheme = AuthenticationScheme.SAUTHC1
        def cacheManager = Caches.newDisabledCacheManager();

        Client client = new DefaultClient(apiKeyCredentials, baseUrl, null, cacheManager , authcScheme, null,  20000)

        assertNotNull(client.getDataStore())
        assertEquals(client.getDataStore().baseUrl, baseUrl)
    }

    //@since 1.0.RC3
    @Test
    void testDirtyPropertiesNotSharedAmongDifferentResourcesWithSameHref() {
        def apiKeyCredentials = createNiceMock(ApiKeyCredentials)
        def requestExecutor = createStrictMock(RequestExecutor)
        def resourceFactory = createStrictMock(ResourceFactory)
        def response = createStrictMock(Response)

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                fullName: "Mel Ben Smuk",
                email: 'my@email.com',
                givenName: 'Given Name',
                emailVerificationToken: [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"],
                directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                groupMemberships: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"],
                providerData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData"],
                customData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/customData"]
        ]

        def propertiesAfterSave = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                fullName: "Mel Ben Smuk",
                email: 'my@email.com',
                givenName: 'My New Given Name',
                emailVerificationToken: [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"],
                directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                groupMemberships: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"],
                providerData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData"],
                customData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/customData"]
        ]
        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream is01 = new ByteArrayInputStream(mapMarshaller.marshal(properties).getBytes());
        InputStream is02 = new ByteArrayInputStream(mapMarshaller.marshal(properties).getBytes());
        InputStream is01AfterSave = new ByteArrayInputStream(mapMarshaller.marshal(propertiesAfterSave).getBytes());

        def dataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)

        //Server call for Resource01
        expect(requestExecutor.executeRequest((DefaultRequest) reportMatcher(new RequestMatcher(new DefaultRequest(HttpMethod.GET, properties.href))))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is01)

        //Server call for Resource02
        expect(requestExecutor.executeRequest((DefaultRequest) reportMatcher(new RequestMatcher(new DefaultRequest(HttpMethod.GET, properties.href))))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is02)

        expect(requestExecutor.executeRequest((DefaultRequest) reportMatcher(new RequestMatcher(new DefaultRequest(HttpMethod.POST, properties.href))))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is01AfterSave)
        expect(response.getHttpStatus()).andReturn(200)

        replay requestExecutor, response, resourceFactory

        //Let's start
        def client = new DefaultClient(apiKeyCredentials, "https://api.stormpath.com/v1/accounts/", null, Caches.newDisabledCacheManager(), null, null, 20000)
        setNewValue(client, "dataStore", dataStore)
        def account01 = client.getResource(properties.href, Account)
        def account02 = client.getResource(properties.href, Account)

        Field propertiesField = account01.getClass().superclass.superclass.superclass.getDeclaredField("properties")
        propertiesField.setAccessible(true);

        //let check both resources share the very same backing data instance
        assertSame(propertiesField.get(account01), propertiesField.get(account02))
        assertSame(account01.getGivenName(), account02.getGivenName())

        //We will update givenName of account01, account02 mut not have the givenName value changed as it was not saved yet
        assertEquals(account01.dirtyProperties, Collections.emptyMap())
        account01.setGivenName("My New Given name")
        assertNotEquals(account01.getGivenName(), account02.getGivenName())
        assertEquals(account01.dirtyProperties, [givenName: "My New Given name"])
        assertEquals(account02.dirtyProperties, Collections.emptyMap())
        assertSame(propertiesField.get(account01), propertiesField.get(account02))

        //Saving now, both resources must have the same data now
        account01.save()
        assertSame(account01.getGivenName(), account02.getGivenName())
        assertEquals(account01.dirtyProperties, Collections.emptyMap())
        assertEquals(account02.dirtyProperties, Collections.emptyMap())
        assertSame(propertiesField.get(account01), propertiesField.get(account02))

        verify requestExecutor, response, resourceFactory
    }

    //@since 1.0.RC3
    static class RequestMatcher implements IArgumentMatcher {

        private Request expected

        RequestMatcher(Request request) {
            expected = request;

        }
        boolean matches(Object o) {
            if (o == null || ! Request.isInstance(o)) {
                return false;
            }
            Request actual = (Request) o
            return expected.getMethod().equals(actual.getMethod()) && expected.getResourceUrl().equals(actual.getResourceUrl()) && expected.getQueryString().equals(actual.getQueryString())
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testTenantActions() {

        def apiKey = createStrictMock(ApiKey)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

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

        replay(apiKeyCredentials, dataStore, tenant, application, returnedApplication, createApplicationRequest, applicationList,
                map, applicationCriteria, directory, returnedDir, createDirectoryRequest, directoryList, directoryCriteria, account)

        Client client = new DefaultClient(apiKeyCredentials, baseUrl, null, Caches.newDisabledCacheManager(), null, null, 20000)
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

        verify(apiKeyCredentials, dataStore, tenant, application, returnedApplication, createApplicationRequest, applicationList,
                map, applicationCriteria, directory, returnedDir, createDirectoryRequest, directoryList, directoryCriteria, account)
    }


    /**
     * Allows to set a new value to a final property
     *
     * @since 1.0.RC3
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
