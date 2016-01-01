

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
import com.stormpath.sdk.saml.SsoInitiationEndpoint
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * Test for DefaultSsoInitiationEndpoint class
 *
 * @since 1.0.RC8
 */
class DefaultSsoInitiationEndpointTest {

    @Test
    void testGetPropertyDescriptors() {

        SsoInitiationEndpoint endpoint = new DefaultSsoInitiationEndpoint(createStrictMock(InternalDataStore))

        def propertyDescriptors = endpoint.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 0)
    }

    @Test
    void testInstanceCreation(){

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/applications/iyk6s8gxX6NkXtiiLkh1EG/saml/sso/idpRedirect"
        ]

        SsoInitiationEndpoint ssoInitiationEndpoint = new DefaultSsoInitiationEndpoint(internalDataStore, properties)
        assertTrue(ssoInitiationEndpoint instanceof SsoInitiationEndpoint && ssoInitiationEndpoint.getHref().equals(properties.href))
    }
}
