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
 * @since 1.RC5.1
 */
class DefaultLinkedInAccountRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.LINKEDIN;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof LinkedInAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
        assertTrue(request instanceof ProviderAccountRequest)
        assertEquals(request.getProviderData().getProviderId(), "linkedin")
        def providerData = request.getProviderData()
        assertTrue(providerData instanceof DefaultLinkedInProviderData)
        providerData = (DefaultLinkedInProviderData) providerData
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
    }

    @Test
    void testMissingAccessTokenAndCode() {
        def requestBuilder = Providers.LINKEDIN.account();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a LinkedIn account request.")
        }
    }

    @Test
    void testAddingAccessTokenAndCode() {
        def requestBuilder = Providers.LINKEDIN.account();

        try {
            requestBuilder.setAccessToken("abc").setCode("sdc").build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a LinkedIn account request.")
        }
    }

}
