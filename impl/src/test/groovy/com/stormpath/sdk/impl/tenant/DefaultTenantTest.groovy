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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.ds.JacksonMapMarshaller
import com.stormpath.sdk.impl.http.Request
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.impl.provider.DefaultGoogleProvider
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationList
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.provider.Providers
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultTenantTest {

    //@since 1.0.0
    @Test
    void testGetPropertyDescriptors() {

        DefaultTenant defaultTenant = new DefaultTenant(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultTenant.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 8)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("key") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("applications") instanceof CollectionReference && propertyDescriptors.get("applications").getType().equals(ApplicationList))
        assertTrue(propertyDescriptors.get("directories") instanceof CollectionReference && propertyDescriptors.get("directories").getType().equals(DirectoryList))
        assertTrue(propertyDescriptors.get("customData") instanceof ResourceReference && propertyDescriptors.get("customData").getType().equals(CustomData))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("organizations") instanceof CollectionReference && propertyDescriptors.get("organizations").getType().equals(OrganizationList))
    }

    @Test
    void testCreateDirectory() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def directory = createStrictMock(Directory)
        def returnedDirectory = createStrictMock(Directory)

        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        expect(internalDataStore.create("/directories", directory)).andReturn(returnedDirectory)

        replay internalDataStore, directory, returnedDirectory

        assertEquals(defaultTenant.createDirectory(directory), returnedDirectory)

        verify internalDataStore, directory, returnedDirectory
    }

    /**
     * @since 1.0.RC5
     */
    @Test
    void testCreateOrganization() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                organizations: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/organizations"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def organization = createStrictMock(Organization)
        def returnedOrganization = createStrictMock(Organization)

        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        expect(internalDataStore.create("/organizations", organization)).andReturn(returnedOrganization)

        replay internalDataStore, organization, returnedOrganization

        assertEquals(defaultTenant.createOrganization(organization), returnedOrganization)

        verify internalDataStore, organization, returnedOrganization
    }

    @Test
    void testCreateDirectoryRequest() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]
        ]

        def providerProperties = [providerId: "google"]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultDirectory = createStrictMock(DefaultDirectory)
        def returnedDirectory = createStrictMock(Directory)
        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        def defaultProvider = new DefaultGoogleProvider(null, providerProperties)
        defaultProvider.setClientId("aClientId999").setClientSecret("aClientSecret111").setRedirectUri("http://someUrl:99999")

        expect(internalDataStore.create("/directories", defaultDirectory)).andReturn(returnedDirectory)
        expect(defaultDirectory.setProvider((Provider) reportMatcher(new ProviderEquals(defaultProvider)))).andReturn(defaultDirectory)

        replay internalDataStore, defaultDirectory, returnedDirectory

        def request = Directories.newCreateRequestFor(defaultDirectory).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId("aClientId999")
                        .setClientSecret("aClientSecret111")
                        .setRedirectUri("http://someUrl:99999")
                        .build()
                ).build();

        assertEquals(defaultTenant.createDirectory(request), returnedDirectory)

        verify(internalDataStore, defaultDirectory, returnedDirectory)
    }

    @Test
    void testUnrecognizedCreateDirectoryRequestType() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultDirectory = createStrictMock(Directory)
        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        def request = Directories.newCreateRequestFor(defaultDirectory).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId("aClientId999")
                        .setClientSecret("aClientSecret111")
                        .setRedirectUri("http://someUrl:99999")
                        .build()
                ).build()

        try {
            defaultTenant.createDirectory(request)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("the directory instance is of an unidentified type. The specified provider cannot be set to it: EasyMock for interface com.stormpath.sdk.directory.Directory"))
        }

    }

    /**
     * Asserts that both https://github.com/stormpath/stormpath-sdk-java/issues/60 and https://github.com/stormpath/stormpath-sdk-java/issues/140 have been fixed
     * @since 1.0.RC4
     */
    @Test
    void testVerifyAccountEmail() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                           applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                           directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]
        ]
        def returnedProperties = [ href: "https://api.stormpath.com/v1/accounts/2IEuQrTEPg43GRxcqYCZDN" ]

        String token = "fooVerificationEmail"
        def apiKey = ApiKeys.builder().setId('foo').setSecret('bar').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)

        def cacheManager = Caches.newCacheManager().build()
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def mapMarshaller = new JacksonMapMarshaller();
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(returnedProperties).getBytes());

        expect(requestExecutor.executeRequest((DefaultRequest) reportMatcher(new RequestMatcher(new DefaultRequest(HttpMethod.POST, "https://api.stormpath.com/v1/accounts/emailVerificationTokens/fooVerificationEmail"))))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)
        expect(response.getHttpStatus()).andReturn(200)

        replay requestExecutor, response

        def dataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials, cacheManager)

        //assert that the account is not already cached
        assertNull cacheManager.getCache(Account.name).get(returnedProperties.href)

        def tenant = new DefaultTenant(dataStore, properties)
        //Since this issue shows up only when the caching is enabled, let's make sure that it is indeed enabled, otherwise
        //we are not actually asserting that the issue has been fixed
        assertTrue(dataStore.isCachingEnabled())
        tenant.verifyAccountEmail(token)

        //assert that the account is not cached per https://github.com/stormpath/stormpath-sdk-java/issues/60:
        assertNull cacheManager.getCache(Account.name).get(returnedProperties.href)

        verify requestExecutor, response
    }

    //@since 1.0.beta
    static class ProviderEquals implements IArgumentMatcher {

        private Provider expected

        ProviderEquals(Provider provider) {
            expected = provider;

        }
        boolean matches(Object o) {
            if (o == null || ! Provider.isInstance(o)) {
                return false;
            }
            Provider actual = (Provider) o
            Map actualProperties = getValue(AbstractResource, actual, "properties")
            Map actualDirtyProperties = getValue(AbstractResource, actual, "dirtyProperties")
            Map expectedProperties = getValue(AbstractResource, expected, "properties")
            Map expectedDirtyProperties = getValue(AbstractResource, expected, "dirtyProperties")
            assertEquals(actualProperties, expectedProperties)
            assertEquals(actualDirtyProperties, expectedDirtyProperties)
            return true
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }

        private Object getValue(Class clazz, Object object, String fieldName){
            Field field = clazz.getDeclaredField(fieldName)
            field.setAccessible(true)
            return field.get(object)
        }
    }

    //@since 1.0.0
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

}