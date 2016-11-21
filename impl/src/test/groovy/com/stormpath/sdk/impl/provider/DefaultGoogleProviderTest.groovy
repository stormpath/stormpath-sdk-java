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
import com.stormpath.sdk.impl.resource.EnumProperty
import com.stormpath.sdk.impl.resource.ListProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.GoogleProviderAccessType
import com.stormpath.sdk.provider.GoogleProviderDisplay
import com.stormpath.sdk.provider.Provider
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.beta
 */
class DefaultGoogleProviderTest {

    @Test
    void testGetPropertyDescriptors() {

        def provider = new DefaultGoogleProvider(createStrictMock(InternalDataStore))

        def propertyDescriptors = provider.getPropertyDescriptors()

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("clientId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("clientSecret") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("redirectUri") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("scope") instanceof ListProperty)
        assertTrue(propertyDescriptors.get("hd") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("accessType") instanceof EnumProperty)
        assertEquals(propertyDescriptors.get("accessType").getType(), GoogleProviderAccessType)
        assertTrue(propertyDescriptors.get("display") instanceof EnumProperty)
        assertEquals(propertyDescriptors.get("display").getType(), GoogleProviderDisplay)
        assertEquals(propertyDescriptors.size(), 10)

        assertTrue(Provider.isInstance(provider))
        assertTrue(GoogleProvider.isInstance(provider))
    }

    @Test
    void testMethods() {

        def properties = [href        : "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider",
                          createdAt   : "2013-10-01T23:38:55.000Z",
                          modifiedAt  : "2013-10-02T23:38:55.000Z",
                          clientId    : "122492918500.apps.googleusercontent.com",
                          clientSecret: "U-IolozazwLn1_2M4QjoulQj",
                          redirectUri : "https://myApplicationUrl.com:8900",
                          scope     : ["foo", "bar"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultGoogleProvider(internalDataStore, properties)

        assertEquals(provider.getHref(), "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider")
        assertEquals(provider.getProviderId(), "google")
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getClientId(), "122492918500.apps.googleusercontent.com")
        assertEquals(provider.getClientSecret(), "U-IolozazwLn1_2M4QjoulQj")
        assertEquals(provider.getRedirectUri(), "https://myApplicationUrl.com:8900")
        assertEquals(provider.getScope(), ["foo", "bar"])
        assertNull(provider.getHd())
        assertNull(provider.getDisplay())
        assertNull(provider.getAccessType())
        provider.setClientId("999999999999.apps.googleusercontent.com")
        assertEquals(provider.getClientId(), "999999999999.apps.googleusercontent.com")
        provider.setClientSecret("U-AAAAaaaa00011")
        assertEquals(provider.getClientSecret(), "U-AAAAaaaa00011")
        provider.setRedirectUri("http://myNewApplicationUrl.com")
        assertEquals(provider.getRedirectUri(), "http://myNewApplicationUrl.com")
    }

    @Test
    void testMethodsWithOptionalProperties() {

        def properties = [href        : "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider",
                          createdAt   : "2013-10-01T23:38:55.000Z",
                          modifiedAt  : "2013-10-02T23:38:55.000Z",
                          clientId    : "122492918500.apps.googleusercontent.com",
                          clientSecret: "U-IolozazwLn1_2M4QjoulQj",
                          redirectUri : "https://myApplicationUrl.com:8900",
                          scope       : ["foo", "bar"],
                          hd          : "example.com",
                          display     : "touch",
                          accessType  : "offline"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultGoogleProvider(internalDataStore, properties)

        assertEquals(provider.getHd(), "example.com")
        assertEquals(provider.getDisplay(), GoogleProviderDisplay.TOUCH)
        assertEquals(provider.getAccessType(), GoogleProviderAccessType.OFFLINE)
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getClientId(), "122492918500.apps.googleusercontent.com")
        assertEquals(provider.getClientSecret(), "U-IolozazwLn1_2M4QjoulQj")
        assertEquals(provider.getRedirectUri(), "https://myApplicationUrl.com:8900")
        assertEquals(provider.getScope(), ["foo", "bar"])
        provider.setClientId("999999999999.apps.googleusercontent.com")
        assertEquals(provider.getClientId(), "999999999999.apps.googleusercontent.com")
        provider.setClientSecret("U-AAAAaaaa00011")
        assertEquals(provider.getClientSecret(), "U-AAAAaaaa00011")
        provider.setRedirectUri("http://myNewApplicationUrl.com")
        assertEquals(provider.getRedirectUri(), "http://myNewApplicationUrl.com")
    }

}
