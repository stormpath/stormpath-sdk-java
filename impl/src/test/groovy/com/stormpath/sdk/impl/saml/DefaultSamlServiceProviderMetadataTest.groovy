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
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.saml.AssertionConsumerServicePostEndpoint
import com.stormpath.sdk.saml.SamlServiceProviderMetadata
import com.stormpath.sdk.cert.X509SigningCert
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for SamlServiceProviderMetadata class
 *
 * @since 1.0.RC8
 */
class DefaultSamlServiceProviderMetadataTest {

    @Test
    void testGetPropertyDescriptors() {

        SamlServiceProviderMetadata metadata = new DefaultSamlServiceProviderMetadata(createStrictMock(InternalDataStore))

        def propertyDescriptors = metadata.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 3)

        assertTrue(propertyDescriptors.get("entityId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("assertionConsumerServicePostEndpoint") instanceof ResourceReference && propertyDescriptors.get("assertionConsumerServicePostEndpoint").getType().equals(AssertionConsumerServicePostEndpoint.class))
        assertTrue(propertyDescriptors.get("x509CertificateId") instanceof ResourceReference && propertyDescriptors.get("x509CertificateId").getType().equals(X509SigningCert.class))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/samlServiceProviderMetadatas/iouertnw48ufsjnsDFSf",
                entityId: "urn:stormpath:directory:4VQxTP5I7Xio03QJTOwQy1:provider:sp",
                assertionConsumerServicePostEndpoint: [href: "https://api.stormpath.com/v1/directories/4VQxTP5I7Xio03QJTOwQy1/saml/sso/post"],
                x509CertificateId: [href: "https://api.stormpath.com/v1/x509certificates/7frJxiVEfZB9NaXw5vLvCA"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def metadata = new DefaultSamlServiceProviderMetadata(internalDataStore, properties)

        assertEquals(metadata.getEntityId(), properties.entityId)

        expect(internalDataStore.instantiate(AssertionConsumerServicePostEndpoint, properties.assertionConsumerServicePostEndpoint)).
                andReturn(new DefaultAssertionConsumerServicePostEndpoint(internalDataStore, properties.assertionConsumerServicePostEndpoint))

        expect(internalDataStore.instantiate(X509SigningCert, properties.x509CertificateId)).
                andReturn(new DefaultX509SigningCert(internalDataStore, properties.x509CertificateId))

        replay internalDataStore

        def resource = metadata.getAssertionConsumerServicePostEndpoint()
        assertTrue(resource instanceof AssertionConsumerServicePostEndpoint
                && resource.getHref().equals(properties.assertionConsumerServicePostEndpoint.href))

        resource = metadata.getX509SigningCert()
        assertTrue(resource instanceof X509SigningCert
                && resource.getHref().equals(properties.x509CertificateId.href))

        verify internalDataStore
    }
}
