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
import com.stormpath.sdk.provider.GoogleProviderData
import com.stormpath.sdk.provider.ProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultGoogleProviderDataTest {

    @Test
    void testGetPropertyDescriptors() {

        def providerData = new DefaultGoogleProviderData(createStrictMock(InternalDataStore))

        def propertyDescriptors = providerData.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 6)

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("accessToken") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("code") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("refreshToken") instanceof StringProperty)
        assertTrue(ProviderData.isInstance(providerData))
        assertTrue(GoogleProviderData.isInstance(providerData))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-02T23:38:55.000Z",
                accessToken: "y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4",
                refreshToken: "1/qQTS638g3ArE4U02FoiXL1yIh-OiPmhc"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultGoogleProviderData(internalDataStore, properties)

        assertEquals(providerData.getHref(), "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData")
        assertEquals(providerData.getProviderId(), "google")
        assertEquals(providerData.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(providerData.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(providerData.getAccessToken(), "y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
        assertEquals(providerData.getRefreshToken(), "1/qQTS638g3ArE4U02FoiXL1yIh-OiPmhc")

        providerData.setAccessToken("AAAAAAAAAAAA")
        assertEquals(providerData.getAccessToken(), "AAAAAAAAAAAA")
        assertNull(providerData.getCode())
        providerData.setCode("4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
        assertEquals(providerData.getCode(), "4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
        providerData.setRefreshToken("myNewRefreshToken")
        assertEquals(providerData.getRefreshToken(), "myNewRefreshToken")

    }

    @Test
    void testCode() {

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-02T23:38:55.000Z",
                code: "4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultGoogleProviderData(internalDataStore, properties)

        assertEquals(providerData.getHref(), "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData")
        assertEquals(providerData.getProviderId(), "google")
        assertEquals(providerData.getCode(), "4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
    }

    @Test
    void testConstructor() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultGoogleProviderData(internalDataStore)

        assertEquals(providerData.getProviderId(), "google")
    }

}
