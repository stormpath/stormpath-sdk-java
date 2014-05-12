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

import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.CreateProviderRequestBuilder
import com.stormpath.sdk.provider.GoogleCreateProviderRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultGoogleCreateProviderRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.GOOGLE;
        def requestBuilder = providerRequest.builder();
        assertTrue(requestBuilder instanceof GoogleCreateProviderRequestBuilder)
        assertTrue(CreateProviderRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder
                .setClientId("999999911111111")
                .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                .setRedirectUri("http://someUrl:8991/index.jsp")
                .build();
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.getProvider().getProviderId(), "google")
        def provider = request.getProvider()
        assertTrue(provider instanceof DefaultGoogleProvider)
        provider = (DefaultGoogleProvider) provider
        assertEquals(provider.getClientId(), "999999911111111")
        assertEquals(provider.getClientSecret(), "a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
        assertEquals(provider.getRedirectUri(), "http://someUrl:8991/index.jsp")
    }

    @Test
    void testMissingAllProperties() {
        def requestBuilder = Providers.GOOGLE.builder();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

    @Test
    void testOnlyClientIdSecret() {
        def requestBuilder = Providers.GOOGLE.builder();

        try {
            requestBuilder
                    .setClientId("999999911111111")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientSecret is a required property. It must be provided before building.")
        }
    }

    @Test
    void testOnlyClientSecret() {
        def requestBuilder = Providers.GOOGLE.builder();

        try {
            requestBuilder
                    .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

    @Test
    void testMissingRedirectUri() {
        def requestBuilder = Providers.GOOGLE.builder();

        try {
            requestBuilder
                    .setClientId("999999911111111")
                    .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "redirectUri is a required property. It must be provided before building.")
        }
    }

}
