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
package com.stormpath.sdk.impl.oauth


import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * Test for IdSiteAuthenticationAttempt class
 *
 * @since 1.0.RC8.2
 */
class DefaultCreateIdSiteAuthenticationAttemptTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultIdSiteAuthenticationAttempt = new DefaultIdSiteAuthenticationAttempt(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultIdSiteAuthenticationAttempt.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 2)

        assertTrue(propertyDescriptors.get("token") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("grant_type") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        def properties = [
                token: "test_token",
                grant_type: "stormpath_token"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)

        def attempt = new DefaultIdSiteAuthenticationAttempt(internalDataStore, properties)

        replay internalDataStore

        attempt.setToken(properties.token)
        attempt.setGrantType(properties.grant_type)

        assertEquals(attempt.getToken(), properties.token)
        assertEquals(attempt.getGrantType(), properties.grant_type)
    }
}
