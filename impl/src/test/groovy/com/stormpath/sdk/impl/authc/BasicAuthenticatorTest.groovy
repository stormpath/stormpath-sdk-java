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
package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequests
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.alpha
 */
class BasicAuthenticatorTest {

    @Test
    void testNullHref() {
        def internalDataStore = createMock(InternalDataStore)
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail("foo").setPassword("bar").build()

        try {
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
            basicAuthenticator.authenticate(null, request)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "href argument must be specified")
        }
    }

    @Test
    void testInvalidRequestClass() {
        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def internalDataStore = createMock(InternalDataStore)
        def request = createMock(AuthenticationRequest)

        try {
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
            basicAuthenticator.authenticate(appHref, request)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Only DefaultUsernamePasswordRequest instances are supported."))
        }
    }

    @Test
    void testAuthenticateWithoutAccountStore() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create((String) appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request)

        verify(internalDataStore, basicLoginAttempt, authenticationResult)

    }

    @Test
    void testAuthenticateAccountStoreNull() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create((String) appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request)

        verify(internalDataStore, basicLoginAttempt, authenticationResult)

    }

    @Test
    void testAuthenticateWithAccountStore() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def accountStore = createStrictMock(AccountStore)
        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).inAccountStore(accountStore).build()

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(basicLoginAttempt.setAccountStore(accountStore))
        expect(internalDataStore.create((String) appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request)

        verify(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

    }

    //@since 1.0.RC5
    @Test
    void testAuthenticateOptionsWithAccount() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def options = UsernamePasswordRequests.options().withAccount()
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).withResponseOptions(options).build()

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))

        expect(internalDataStore.create((String) appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class, options)).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request)

        verify(internalDataStore, basicLoginAttempt, authenticationResult)

    }

}
