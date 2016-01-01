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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.saml.SamlServiceProvider
import com.stormpath.sdk.saml.SsoInitiationEndpoint
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for SamlServiceProvider class
 *
 * @since 1.0.RC8
 */
class DefaultSamlServiceProviderTest {

    @Test
    void testGetPropertyDescriptors() {

        SamlServiceProvider provider = new DefaultSamlServiceProvider(createStrictMock(InternalDataStore))

        def propertyDescriptors = provider.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 3)

        assertTrue(propertyDescriptors.get("ssoInitiationEndpoint") instanceof ResourceReference && propertyDescriptors.get("ssoInitiationEndpoint").getType().equals(SsoInitiationEndpoint.class))
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/samlServiceProviders/3Tj2L7gxX6NkXtiiLkh1WF",
                ssoInitiationEndpoint: [href: "https://api.stormpath.com/v1/applications/iyk6s8gxX6NkXtiiLkh1EG/saml/sso/idpRedirect"],
                createdAt: "2015-01-01T00:00:00Z",
                modifiedAt: "2015-02-01T12:00:00Z"
        ]

        DefaultSamlServiceProvider provider = new DefaultSamlServiceProvider(internalDataStore, properties)

        expect(internalDataStore.instantiate(SsoInitiationEndpoint, properties.ssoInitiationEndpoint)).
                andReturn(new DefaultSsoInitiationEndpoint(internalDataStore, properties.ssoInitiationEndpoint))

        replay internalDataStore

        def ssoInitiationEndpoint = provider.getSsoInitiationEndpoint()
        assertTrue(ssoInitiationEndpoint instanceof SsoInitiationEndpoint && ssoInitiationEndpoint.getHref().equals(properties.ssoInitiationEndpoint.href))

        DateFormat df = new ISO8601DateFormat();

        assertEquals(df.format(provider.getCreatedAt()), "2015-01-01T00:00:00Z")
        assertEquals(df.format(provider.getModifiedAt()), "2015-02-01T12:00:00Z")

        verify internalDataStore
    }
}
