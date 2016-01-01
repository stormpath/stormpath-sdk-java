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
import com.stormpath.sdk.saml.AssertionConsumerServicePostEndpoint
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for AssertionConsumerServicePostEndpointTest class
 *
 * @since 1.0.RC8
 */
class DefaultAssertionConsumerServicePostEndpointTest {

    @Test
    void testGetPropertyDescriptors() {

        AssertionConsumerServicePostEndpoint consumerServicePostEndpoint = new DefaultAssertionConsumerServicePostEndpoint(createStrictMock(InternalDataStore))

        def propertyDescriptors = consumerServicePostEndpoint.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 0)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/directories/45YM3OwioW9PVtfLOh6q1e/saml/sso/post"]

        AssertionConsumerServicePostEndpoint consumerServicePostEndpoint = new DefaultAssertionConsumerServicePostEndpoint(internalDataStore, properties)

        replay internalDataStore

        assertTrue(consumerServicePostEndpoint.getHref().equals(properties.href))

        verify internalDataStore
    }
}
