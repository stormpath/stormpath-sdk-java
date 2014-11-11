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

import com.stormpath.sdk.provider.LinkedInAccountRequestBuilder
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.0
 */
class LinkedInAccountRequestBuilderTest {

    @Test
    void testWithAccessToken() {
        def providerRequest = Providers.LINKEDIN;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof LinkedInAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setAccessToken("y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "linkedin")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultLinkedInProviderData)
        providerData = (DefaultLinkedInProviderData) providerData
        assertEquals(providerData.getAccessToken(), "y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
    }

    @Test
    void testInvalidStateProperties() {
        def requestBuilder = Providers.LINKEDIN.account();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "accessToken is a required property. It must be provided before building.")
        }
    }
}
