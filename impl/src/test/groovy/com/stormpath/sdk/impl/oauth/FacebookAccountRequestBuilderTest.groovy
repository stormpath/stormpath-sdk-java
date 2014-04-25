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
package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.oauth.FacebookAccountRequestBuilder
import com.stormpath.sdk.oauth.ProviderAccountRequest
import com.stormpath.sdk.oauth.ProviderAccountRequestBuilder
import com.stormpath.sdk.oauth.Providers
import org.junit.Test

import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class FacebookAccountRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.FACEBOOK;
        def requestBuilder = providerRequest.accountRequest();
        assertTrue(requestBuilder instanceof FacebookAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "facebook")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultFacebookProviderData)
        providerData = (DefaultFacebookProviderData) providerData
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
    }

    @Test
    void testMissingAccessToken() {
        def requestBuilder = Providers.FACEBOOK.accountRequest();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "accessToken is a required property. It must be provided before building.")
        }
    }
}
