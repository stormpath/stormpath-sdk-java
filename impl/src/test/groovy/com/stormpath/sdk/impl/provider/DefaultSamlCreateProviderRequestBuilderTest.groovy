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
import com.stormpath.sdk.impl.provider.saml.DefaultSamlProvider
import com.stormpath.sdk.impl.saml.DefaultAttributeStatementMappingRules
import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.saml.CreateSamlProviderRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * Test for CreateSamlProviderRequestBuilder class
 *
 * @since 1.0.RC8
 */
class DefaultSamlCreateProviderRequestBuilderTest {

    @Test
    void test() {
        def requestBuilder = Providers.SAML.builder();
        assertTrue(requestBuilder instanceof CreateSamlProviderRequestBuilder)

        def attributeStatementMappingRules  = new DefaultAttributeStatementMappingRules(createStrictMock(InternalDataStore))

        def request = requestBuilder
                .setRequestSignatureAlgorithm("testAlgorithm")
                .setAttributeStatementMappingRules(attributeStatementMappingRules)
                .setSsoLoginUrl("login URL")
                .setSsoLogoutUrl("logout URL")
                .setEncodedX509SigningCert("encoded CERT")
                .build()
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.getProvider().getProviderId(), "saml")

        def provider = request.getProvider()
        assertTrue(provider instanceof DefaultSamlProvider)
    }

    @Test
    void testMissingProperties() {
        // test missing encodedX509SigningCert
        try {
            def request = Providers.SAML.builder()
                .setRequestSignatureAlgorithm("algorithm")
                .setSsoLoginUrl("loginUrl")
                .setSsoLogoutUrl("logoutUrl")
                .build();
            fail("Should have failed.")
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "The encodedX509SigningCert property is missing.")
        }

        // test missing SsoLoginUrl
        try {
            def request = Providers.SAML.builder()
                .setRequestSignatureAlgorithm("algorithm")
                .setSsoLogoutUrl("logoutUrl")
                .setEncodedX509SigningCert("encodedCert")
                .build();
            fail("Should have failed.")
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "The ssoLoginUrl property is missing.")
        }

        // test missing SsoLogoutUrl
        try {
            def request = Providers.SAML.builder()
                .setRequestSignatureAlgorithm("algorithm")
                .setSsoLoginUrl("loginUrl")
                .setEncodedX509SigningCert("encodedCert")
                .build();
            fail("Should have failed.")
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "The ssoLogoutUrl property is missing.")
        }

        // test missing RequestSignatureAlgorithm
        try {
            def request = Providers.SAML.builder()
                .setSsoLoginUrl("loginUrl")
                .setSsoLogoutUrl("logoutUrl")
                .setEncodedX509SigningCert("encodedCert")
                .build();
            fail("Should have failed.")
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "The requestSignatureAlgorithm property is missing.")
        }
    }
}
