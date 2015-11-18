/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for DefaultGrantAuthenticationToken class
 *
 * @since 1.0.RC7
 */
class DefaultGrantAuthenticationTokenTest {
    @Test
    void testGetPropertyDescriptors() {

        def defaultGrantAuthenticationToken = new DefaultGrantAuthenticationToken(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultGrantAuthenticationToken.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 5)

        assertTrue(propertyDescriptors.get("access_token") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("refresh_token") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("token_type") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("expires_in") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("stormpath_access_token_href") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        def properties = [
                access_token: "32J45K565JK3N4K5JN3K4QVMwOFFIRlhNTzdGNTY4Ukc2IiwiYWxnIjoiSFMyNT",
                refresh_token: "eyJraWQiOiI2UDVKTjRTQVMwOFFIRlhNTzdGNTY4Ukc2IiwiYWxnIjoiSFMyNT",
                token_type: "Bearer",
                stormpath_access_token_href: "https://api.stormpath.com/v1/accessTokens/5hFj6FUwNb28OQrp93phPP",
                expires_in: "3600",
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultGrantAuthenticationToken = new DefaultGrantAuthenticationToken(internalDataStore, properties)

        assertEquals(defaultGrantAuthenticationToken.getAccessToken(), properties.access_token)
        assertEquals(defaultGrantAuthenticationToken.getRefreshToken(), properties.refresh_token)
        assertEquals(defaultGrantAuthenticationToken.getExpiresIn(), properties.expires_in)
        assertEquals(defaultGrantAuthenticationToken.getTokenType(), properties.token_type)
        assertEquals(defaultGrantAuthenticationToken.getAccessTokenHref(), properties.stormpath_access_token_href)
    }
}
