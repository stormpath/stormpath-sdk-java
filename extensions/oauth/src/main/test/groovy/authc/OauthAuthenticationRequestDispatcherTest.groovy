/*
 * Copyright 2015 Stormpath, Inc.
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
package authc

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.impl.authc.DefaultBasicApiAuthenticationRequest
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest
import com.stormpath.sdk.impl.oauth.authc.OauthAuthenticationRequestDispatcher
import com.stormpath.sdk.impl.oauth.authc.ResourceAuthenticationRequest
import com.stormpath.sdk.impl.query.DefaultOptions
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC4.6
 */
class OauthAuthenticationRequestDispatcherTest {

    @Test
    void testWrongOptionsInstanceForUsernamePassword() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(UsernamePasswordRequest)

        try {
            def oauthAuthenticationRequestDispatcher = new OauthAuthenticationRequestDispatcher();
            oauthAuthenticationRequestDispatcher.authenticate(internalDataStore, application, request, new DefaultOptions())
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "options must be an instance of BasicAuthenticationOptions.")
        }
    }

    @Test
    void testOptionsNullForAccessToken() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(AccessTokenAuthenticationRequest)

        try {
            def oauthAuthenticationRequestDispatcher = new OauthAuthenticationRequestDispatcher();
            oauthAuthenticationRequestDispatcher.authenticate(internalDataStore, application, request, new DefaultOptions())
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "expansion is not supported for AccessTokenAuthenticationRequest.")
        }
    }

    @Test
    void testOptionsNullForResource() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(ResourceAuthenticationRequest)

        try {
            def oauthAuthenticationRequestDispatcher = new OauthAuthenticationRequestDispatcher();
            oauthAuthenticationRequestDispatcher.authenticate(internalDataStore, application, request, new DefaultOptions())
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "expansion is not supported for ResourceAuthenticationRequest.")
        }
    }

    @Test
    void testOptionsNullForBasicApi() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(DefaultBasicApiAuthenticationRequest)

        try {
            def oauthAuthenticationRequestDispatcher = new OauthAuthenticationRequestDispatcher();
            oauthAuthenticationRequestDispatcher.authenticate(internalDataStore, application, request, new DefaultOptions())
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "expansion is not supported for BasicApiAuthenticationRequest.")
        }
    }

}
