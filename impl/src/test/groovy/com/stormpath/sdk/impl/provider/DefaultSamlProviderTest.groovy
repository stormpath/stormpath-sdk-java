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
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * Test for SamlProvider class
 *
 * @since 1.0.RC8
 */
class DefaultSamlProviderTest {

    @Test
    void testGetPropertyDescriptors() {

        def provider = new DefaultSamlProvider(createStrictMock(InternalDataStore))

        def propertyDescriptors = provider.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 9)

        assertTrue(propertyDescriptors.get("ssoLoginUrl") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("ssoLogoutUrl") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("requestSignatureAlgorithm") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("encodedX509SigningCert") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/directories/kljertnw48ufsjnsDFSf/provider",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-02T23:38:55.000Z",
                ssoLoginUrl: "https://idp.whatever.com/saml2/sso/login",
                ssoLogoutUrl: "https://idp.whatever.com/saml2/sso/logout",
                requestSignatureAlgorithm: "RSA-SHA256",
                encodedX509SigningCert: "-----BEGIN CERTIFICATE----- something here -----END CERTIFICATE-----"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultSamlProvider(internalDataStore, properties)

        assertEquals(provider.getHref(), properties.href)
        assertEquals(provider.getProviderId(), "saml")
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getSsoLoginUrl(), properties.ssoLoginUrl)
        assertEquals(provider.getSsoLogoutUrl(), properties.ssoLogoutUrl)
        assertEquals(provider.getRequestSignatureAlgorithm(), properties.requestSignatureAlgorithm)
        assertEquals(provider.getEncodedX509SigningCert(), properties.encodedX509SigningCert)

        provider.setEncodedX509SigningCert("new signing cert")
        provider.setSsoLoginUrl("new login url")
        provider.setSsoLogoutUrl("new logout url")
        provider.setRequestSignatureAlgorithm("new signing algorithm")

        assertEquals(provider.getEncodedX509SigningCert(), "new signing cert")
        assertEquals(provider.getSsoLoginUrl(), "new login url")
        assertEquals(provider.getSsoLogoutUrl(), "new logout url")
        assertEquals(provider.getRequestSignatureAlgorithm(), "new signing algorithm")
    }
}
