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
package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationOptions
import com.stormpath.sdk.authc.Authentications
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC4.6
 */
class AuthenticationRequestDispatcherTest {

    @Test
    void testWrongOptionsInstanceForUsernamePassword() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(UsernamePasswordRequest)
        def options = createMock(AuthenticationOptions)

        try {
            def authenticationRequestDispatcher = new AuthenticationRequestDispatcher();
            authenticationRequestDispatcher.authenticate(internalDataStore, application, request, options)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "options must be an instance of BasicAuthenticationOptions.")
        }
    }

    @Test
    void testOptionsNull() {
        def internalDataStore = createMock(InternalDataStore)
        def application = createMock(Application)
        def request = createMock(DefaultBasicApiAuthenticationRequest)

        try {
            def authenticationRequestDispatcher = new AuthenticationRequestDispatcher();
            authenticationRequestDispatcher.authenticate(internalDataStore, application, request, Authentications.BASIC.options())
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "expansion is not supported for ApiAuthenticationRequests.")
        }
    }

}
