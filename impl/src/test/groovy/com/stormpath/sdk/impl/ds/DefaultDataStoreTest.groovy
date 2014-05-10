/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.ds

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.impl.provider.DefaultGoogleProviderData
import com.stormpath.sdk.impl.provider.IdentityProviderType
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GoogleProviderData
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.provider.ProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultDataStoreTest {

    @Test
    void testGetSpecificResourceInvalidArguments() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKey = createStrictMock(ApiKey)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKey)

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
            assertEquals(e.getMessage(), "parent class argument cannot be null.")
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
        def apiKey = createStrictMock(ApiKey)
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

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKey)
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
        def apiKey = createStrictMock(ApiKey)
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

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKey)
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
        def apiKey = createStrictMock(ApiKey)
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream("".getBytes());

        def childIdProperty = "providerId"
        def map = IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP

        expect(requestExecutor.executeRequest(anyObject(DefaultRequest))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)

        replay(requestExecutor, response, facebookProvider)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKey)
        try {
            defaultDataStore.getResource("https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider", Provider, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "providerId could not be found in: null.")
        }

        verify(requestExecutor, response, facebookProvider)
    }

    @Test
    void getSpecificResourceNonexistentPropertyException() {
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def facebookProvider = createStrictMock(FacebookProvider)
        def apiKey = createStrictMock(ApiKey)
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

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKey)
        try {
            defaultDataStore.getResource("https://api.stormpath.com/v1/directories/5fgF3o89Ph5nbJzY6EVSct/provider", Provider, childIdProperty, map)
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "No Class mapping could be found for nonexistentProperty.")
        }

        verify(requestExecutor, response, facebookProvider)
    }

}
