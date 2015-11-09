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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.provider.LinkedInProvider
import com.stormpath.sdk.provider.Provider
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.0
 */
class DefaultLinkedInProviderTest {

    @Test
    void testGetPropertyDescriptors() {

        def provider = new DefaultLinkedInProvider(createStrictMock(InternalDataStore))

        def propertyDescriptors = provider.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 6)

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("clientId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("clientSecret") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("redirectUri") instanceof StringProperty)
        assertTrue(Provider.isInstance(provider))
        assertTrue(LinkedInProvider.isInstance(provider))
    }

    @Test
    void testMethodsNoRedirectUri() {

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-02T23:38:55.000Z",
                clientId: "613598318417022",
                clientSecret: "c1ad951d45fdd0010c1c7d67c8f1d800"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultLinkedInProvider(internalDataStore, properties)

        assertEquals(provider.getHref(), "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider")
        assertEquals(provider.getProviderId(), "linkedin")
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getClientId(), "613598318417022")
        assertEquals(provider.getClientSecret(), "c1ad951d45fdd0010c1c7d67c8f1d800")

        provider.setClientId("999999999999")
        assertEquals(provider.getClientId(), "999999999999")
        provider.setClientSecret("AAAAA9999999")
        assertEquals(provider.getClientSecret(), "AAAAA9999999")
    }

    @Test
    void testMethodsWithRedirectUri() {

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider",
                          createdAt: "2013-10-01T23:38:55.000Z",
                          modifiedAt: "2013-10-02T23:38:55.000Z",
                          clientId: "613598318417022",
                          clientSecret: "c1ad951d45fdd0010c1c7d67c8f1d800",
                          redirectUri: "http://localhost:8080/test/linkedin"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultLinkedInProvider(internalDataStore, properties)

        assertEquals(provider.getHref(), "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider")
        assertEquals(provider.getProviderId(), "linkedin")
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getClientId(), "613598318417022")
        assertEquals(provider.getClientSecret(), "c1ad951d45fdd0010c1c7d67c8f1d800")
        assertEquals(provider.getRedirectUri(), "http://localhost:8080/test/linkedin")

        provider.setClientId("999999999999")
        assertEquals(provider.getClientId(), "999999999999")
        provider.setClientSecret("AAAAA9999999")
        assertEquals(provider.getClientSecret(), "AAAAA9999999")
    }

}
