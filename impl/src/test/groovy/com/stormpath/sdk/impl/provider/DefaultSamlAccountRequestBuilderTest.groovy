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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.provider.saml.DefaultSamlProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.provider.saml.SamlAccountRequestBuilder
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Test for SamlAccountRequestBuilder class
 * 
 * @since 1.0.RC8
 */
class DefaultSamlAccountRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.SAML;
        def requestBuilder = providerRequest.account();
        providerRequest.builder()
        assertTrue(requestBuilder instanceof SamlAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.build();
        
        assertTrue(request instanceof ProviderAccountRequest)
        assertEquals(request.getProviderData().getProviderId(), "saml")
        def providerData = request.getProviderData()
        assertTrue(providerData instanceof DefaultSamlProviderData)
    }

    @Test
    void testTryToSetAccessToken() {
        def requestBuilder = Providers.SAML.account();

        try {
            requestBuilder.setAccessToken("test");
            fail("Should have failed")
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "This method is not supported in SamlAccountRequestBuilder class.")
        }
    }
}

