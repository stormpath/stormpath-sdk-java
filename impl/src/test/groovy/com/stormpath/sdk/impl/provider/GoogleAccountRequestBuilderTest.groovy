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

import com.stormpath.sdk.provider.GoogleAccountRequestBuilder
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class GoogleAccountRequestBuilderTest {

    @Test
    void testWithAccessToken() {
        def providerRequest = Providers.GOOGLE;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof GoogleAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setAccessToken("y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "google")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultGoogleProviderData)
        providerData = (DefaultGoogleProviderData) providerData
        assertEquals(providerData.getAccessToken(), "y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
        assertNull(providerData.getCode())
    }

    @Test
    void testWithCode() {
        def providerRequest = Providers.GOOGLE;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof GoogleAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setCode("4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "google")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultGoogleProviderData)
        providerData = (DefaultGoogleProviderData) providerData
        assertEquals(providerData.getCode(), "4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
        assertNull(providerData.getAccessToken())
    }

    @Test
    void testInvalidStateProperties() {
        def requestBuilder = Providers.GOOGLE.account();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a Google account request.")
        }

        try {
            requestBuilder.setCode("4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
                    .setAccessToken("y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a Google account request.")
        }
    }
}
