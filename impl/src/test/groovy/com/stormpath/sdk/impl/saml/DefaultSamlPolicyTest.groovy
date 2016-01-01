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
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.saml.SamlPolicy
import com.stormpath.sdk.saml.SamlServiceProvider
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for SamlPolicy class
 *
 * @since 1.0.RC8
 */
class DefaultSamlPolicyTest {

    @Test
    void testGetPropertyDescriptors() {

        SamlPolicy policy = new DefaultSamlPolicy(createStrictMock(InternalDataStore))

        def propertyDescriptors = policy.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 3)

        assertTrue(propertyDescriptors.get("serviceProvider") instanceof ResourceReference && propertyDescriptors.get("serviceProvider").getType().equals(SamlServiceProvider.class))
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/applicationSamlPolicies/3Tj2L7gxX6NkXtiiLkh1WF",
                serviceProvider: [href: "https://api.stormpath.com/v1/samlServiceProviders/45YM3OwioW9PVtfLOh6q1e"]
        ]

        DefaultSamlPolicy samlPolicy = new DefaultSamlPolicy(internalDataStore, properties)

        expect(internalDataStore.instantiate(SamlServiceProvider, properties.serviceProvider)).
                andReturn(new DefaultSamlServiceProvider(internalDataStore, properties.serviceProvider))

        replay internalDataStore

        def serviceProvider = samlPolicy.getSamlServiceProvider()
        assertTrue(serviceProvider instanceof SamlServiceProvider && serviceProvider.getHref().equals(properties.serviceProvider.href))

        verify internalDataStore
    }
}
