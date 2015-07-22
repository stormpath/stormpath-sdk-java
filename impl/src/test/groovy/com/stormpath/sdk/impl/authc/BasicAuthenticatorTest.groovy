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
import com.stormpath.sdk.authc.BasicAuthenticationOptions
import com.stormpath.sdk.authc.Authentications
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.easymock.EasyMock
import org.easymock.IArgumentMatcher
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
        def request = new UsernamePasswordRequest("foo", "bar")

        try {
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
            basicAuthenticator.authenticate(null, request, null)
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
            basicAuthenticator.authenticate(appHref, request, null)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Only UsernamePasswordRequest instances are supported"))
            assertTrue(ex.getMessage().contains("must be an instance of class com.stormpath.sdk.authc.UsernamePasswordRequest"))
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

        def request = new UsernamePasswordRequest(username, password)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request, null)

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

        def request = new UsernamePasswordRequest(username, password, (AccountStore) null)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request, null)

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

        def request = new UsernamePasswordRequest(username, password, accountStore)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(basicLoginAttempt.setAccountStore(accountStore))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request, null)

        verify(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

    }

    //@since 1.0.RC4.6
    @Test
    void testAuthenticateOptionsWithAccount() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = new UsernamePasswordRequest(username, password)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))

        expect(internalDataStore.create(EasyMock.eq(appHref + "/loginAttempts"), (BasicLoginAttempt) EasyMock.eq(basicLoginAttempt), (Class) EasyMock.eq(AuthenticationResult), (BasicAuthenticationOptions) EasyMock.reportMatcher(new BasicAuthenticationOptionsMatcher(Authentications.BASIC.options().withAccount())))).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request, Authentications.BASIC.options().withAccount())

        verify(internalDataStore, basicLoginAttempt, authenticationResult)

    }

    //@since 1.0.RC4.6
    private static class BasicAuthenticationOptionsMatcher implements IArgumentMatcher {

        private BasicAuthenticationOptions expected

        BasicAuthenticationOptionsMatcher(BasicAuthenticationOptions request) {
            expected = request;
        }

        boolean matches(Object o) {
            if (o == null || ! BasicAuthenticationOptions.isInstance(o)) {
                return false;
            }
            BasicAuthenticationOptions actual = (BasicAuthenticationOptions) o
            return expected.getExpansions().toString().equals(actual.getExpansions().toString())
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }
    }



}
