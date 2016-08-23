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
package com.stormpath.sdk.impl.ds

import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.impl.provider.DefaultGoogleProviderData
import com.stormpath.sdk.impl.provider.IdentityProviderType
import com.stormpath.sdk.impl.query.DefaultOptions
import com.stormpath.sdk.provider.*
import com.stormpath.sdk.query.Options
import com.stormpath.sdk.resource.Resource
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultDataStoreTest {

    @Test
    void testGetSpecificResourceInvalidArguments() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)

        def href = "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"
        def providerData = ProviderData
        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP

        try {
            defaultDataStore.getResource(null, providerData, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "href argument cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource("", providerData, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "href argument cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource(href, null, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Resource class argument cannot be null.")
        }

        try {
            defaultDataStore.getResource(href, providerData, null, map)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "childIdProperty cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource(href, providerData, "", map)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "childIdProperty cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource(href, providerData, childIdProperty, null)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "idClassMap cannot be null or empty.")
        }

        try {
            def emptyMap = Collections.<String, Class<ProviderData>>emptyMap();
            defaultDataStore.getResource(href, providerData, childIdProperty, emptyMap)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "idClassMap cannot be null or empty.")
        }
    }

    @Test
    void getSpecificResourceFacebookProvider() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def responseMap = [href: "https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider",
                        createdAt: "2014-04-01T22:05:25.661Z",
                        modifiedAt: "2014-04-01T22:05:53.177Z",
                        clientId: "237396459765014",
                        clientSecret: "a93fae44d2a4f21d4de6201aae9b849a",
                        providerId: "facebook"
        ]
        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(responseMap).getBytes());

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)

        replay(requestExecutor, response)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        def returnedResource = defaultDataStore.getResource(responseMap.href, Provider, childIdProperty, map)
        assertEquals(returnedResource.getHref(), responseMap.href)
        assertTrue(returnedResource instanceof FacebookProvider)
        assertEquals(((FacebookProvider) returnedResource).getProviderId(), responseMap.providerId)
        assertEquals(((FacebookProvider) returnedResource).getClientId(), responseMap.clientId)
        assertEquals(((FacebookProvider) returnedResource).getClientSecret(), responseMap.clientSecret)
        assertNotNull(((FacebookProvider)returnedResource).getCreatedAt())
        assertNotNull(((FacebookProvider)returnedResource).getModifiedAt())

        verify(requestExecutor, response)
    }

    @Test
    void getSpecificResourceGoogleProviderData() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def googleProviderData = createStrictMock(GoogleProviderData)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def responseMap = [href: "https://api.stormpath.com/v1/accounts/7SSk4JuumsZ5qwKV0H2yeD/providerData",
                createdAt: "2014-04-01T22:05:25.661Z",
                modifiedAt: "2014-04-01T22:05:53.177Z",
                accessToken: "ya29.1.AADtN_UitLg2whMbNYYjekdSXKm1dVp_jfdjrdvKoQ-g-ADhuBFNtXlSmq2m_gR",
                refreshToken: "null",
                providerId: "google"
        ]
        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(responseMap).getBytes());

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)

        replay(requestExecutor, response, googleProviderData)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        def returnedResource = defaultDataStore.getResource(responseMap.href, ProviderData, childIdProperty, map)
        assertTrue(returnedResource instanceof DefaultGoogleProviderData)
        assertEquals(returnedResource.getHref(), responseMap.href)
        assertEquals(returnedResource.getProviderId(), responseMap.providerId)
        assertEquals(((GoogleProviderData)returnedResource).getAccessToken(), responseMap.accessToken)

        verify(requestExecutor, response, googleProviderData)
    }

    @Test
    void getSpecificResourceEmptyResponseException() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def facebookProvider = createStrictMock(FacebookProvider)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(false)

        replay(requestExecutor, response, facebookProvider)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        try {
            defaultDataStore.getResource("https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider", Provider, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Unable to obtain resource data from the API server or from cache.")
        }

        verify(requestExecutor, response, facebookProvider)
    }

    @Test
    void getSpecificResourceNonexistentPropertyException() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def facebookProvider = createStrictMock(FacebookProvider)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def responseMap = [href: "https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider",
                createdAt: "2014-04-01T22:05:25.661Z",
                modifiedAt: "2014-04-01T22:05:53.177Z",
                clientId: "237396459765014",
                clientSecret: "a93fae44d2a4f21d4de6201aae9b849a",
                providerId: "facebook"
        ]
        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(responseMap).getBytes());

        def childIdProperty = "nonexistentProperty"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)

        replay(requestExecutor, response, facebookProvider)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        try {
            defaultDataStore.getResource("https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider", Provider, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "No Class mapping could be found for nonexistentProperty.")
        }

        verify(requestExecutor, response, facebookProvider)
    }

    @Test
    void testProviderAccountResultNotCached() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def providerResponseMap = [href: "https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider",
                           createdAt: "2014-04-01T22:05:25.661Z",
                           modifiedAt: "2014-04-01T22:05:53.177Z",
                           clientId: "237396459765014",
                           clientSecret: "a93fae44d2a4f21d4de6201aae9b849a",
                           providerId: "google"
        ]
        def providerAccountResponseMap = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                                          fullName: "Mel Ben Smuk",
                                          emailVerificationToken: [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"],
                                          directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                                          tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                                          groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                                          groupMemberships: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"],
                                          providerData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData"],
                                          apiKeys: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/apiKeys"]
        ]

        def appProperties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                          accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                          passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"],
                          defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                          defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                          accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"]
        ]

        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream providerResponseIS = new ByteArrayInputStream(mapMarshaller.marshal(providerResponseMap).getBytes());
        InputStream providerAccountResponseIS = new ByteArrayInputStream(mapMarshaller.marshal(providerAccountResponseMap).getBytes());

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(providerResponseIS)
        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(providerAccountResponseIS)
        expect(response.getHttpStatus()).andReturn(201)

        replay(requestExecutor, response)

        def cache = Caches.newCacheManager()
                .withDefaultTimeToIdle(1, TimeUnit.HOURS)
                .withDefaultTimeToLive(1, TimeUnit.HOURS)
                .build();
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials, cache)
        def app = new DefaultApplication(defaultDataStore, appProperties)

        defaultDataStore.getResource(providerResponseMap.href, Provider, childIdProperty, map)
        assertEquals(defaultDataStore.cacheManager.getCache("com.stormpath.sdk.provider.Provider").get("https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider"), providerResponseMap)

        def request = Providers.GOOGLE.account().setCode("4/MZ-Z4Xr-V6K61-Y0CE-ifJlyIVwY.EqwqoikzZTUSaDn_5y0ZQNiQIAI2iwI").build();
        def account = app.getAccount(request)

        assertTrue(account.isNewAccount())

        assertEquals(defaultDataStore.cacheManager.caches.size(), 1)

        verify(requestExecutor, response)
    }

    //@since 1.0.0
    @Test
    void getSpecificResourceGithubProvider() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def responseMap = [href: "https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider",
                           createdAt: "2014-04-01T22:05:25.661Z",
                           modifiedAt: "2014-04-01T22:05:53.177Z",
                           clientId: "237396459765014",
                           clientSecret: "a93fae44d2a4f21d4de6201aae9b849a",
                           providerId: "github"
        ]
        def mapMarshaller = new JacksonMapMarshaller();
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(responseMap).getBytes());

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)

        replay(requestExecutor, response)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        def returnedResource = defaultDataStore.getResource(responseMap.href, Provider, childIdProperty, map)
        assertEquals(returnedResource.getHref(), responseMap.href)
        assertTrue(returnedResource instanceof GithubProvider)
        assertEquals(((GithubProvider) returnedResource).getProviderId(), responseMap.providerId)
        assertEquals(((GithubProvider) returnedResource).getClientId(), responseMap.clientId)
        assertEquals(((GithubProvider) returnedResource).getClientSecret(), responseMap.clientSecret)
        assertNotNull(((GithubProvider)returnedResource).getCreatedAt())
        assertNotNull(((GithubProvider)returnedResource).getModifiedAt())

        verify(requestExecutor, response)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testGetResource_Expanded_InvalidArguments() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)
        def emptyOptions = createStrictMock(DefaultOptions)
        def resourceData = Resource
        def href = "http://api.stormpath.com/v1/directories/2B6PLkZ8AGvWlziq18JJ62"

        try {
            defaultDataStore.getResource(null, resourceData, emptyOptions)
            fail("should have thrown due to empty href")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "href argument cannot be null or empty.")
        }

        try {
            defaultDataStore.getResource(href, null, emptyOptions)
            fail("should have thrown due to empty class")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Resource class argument cannot be null.")
        }

        try {
            defaultDataStore.getResource(href, resourceData, (Options) null)
            fail("should have thrown due to empty options")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "The com.stormpath.sdk.impl.ds.DefaultDataStore implementation only functions with com.stormpath.sdk.impl.query.DefaultOptions instances.Object of class [null] must be an instance of class com.stormpath.sdk.impl.query.DefaultOptions")
        }
    }

}
