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

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * Test for GrantAuthenticationAttempt class
 * @since 1.0.RC7
 */
class DefaultCreateOAuthPasswordGrantAuthenticationAttemptTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultCreateGrantAuthAttempt = new DefaultOAuthPasswordGrantAuthenticationAttempt(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultCreateGrantAuthAttempt.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 4)

        assertTrue(propertyDescriptors.get("username") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("password") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("accountStore") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("grant_type") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        def properties = [
                username: "user@test.com",
                password: "Something4!",
                grant_type: "password"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)

        def attempt = new DefaultOAuthPasswordGrantAuthenticationAttempt(internalDataStore, properties)
        def accountStore = createStrictMock(Directory)
        def accountStoreHref = "https://api.stormpath.com/v1/directories/928glsjeorigjj09etiij"

        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(attempt.getPassword()).andReturn(properties.password)
        expect(attempt.getLogin()).andReturn(properties.username)
        expect(attempt.getGrantType()).andReturn(properties.grant_type)
        expect(attempt.getAccountStoreHref()).andReturn(accountStoreHref)

        replay internalDataStore, accountStore

        attempt.setLogin(properties.username)
        attempt.setAccountStore(accountStore)
        attempt.setGrantType(properties.grant_type)
        attempt.setPassword(properties.password)

        assertEquals(attempt.getAccountStoreHref(), accountStoreHref)
        assertEquals(attempt.getPassword(), properties.password)
        assertEquals(attempt.getLogin(), properties.username)
        assertEquals(attempt.getGrantType(), properties.grant_type)
    }
}
