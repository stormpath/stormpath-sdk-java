package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.junit.Test

import static org.testng.Assert.*
import static org.easymock.EasyMock.*


/**
 * @since 0.9.4
 */
class BasicAuthenticatorTest {

    @Test
    void testNullHref() {
        def internalDataStore = createMock(InternalDataStore)
        def request = new UsernamePasswordRequest("foo", "bar")

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

        def request = new UsernamePasswordRequest(username, password)
        request.setAccountStore(null)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

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

        def request = new UsernamePasswordRequest(username, password)
        request.setAccountStore(accountStore)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(basicLoginAttempt.setAccountStore(accountStore))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResult.class)).andReturn(authenticationResult)

        replay(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        basicAuthenticator.authenticate(appHref, request)

        verify(accountStore, internalDataStore, basicLoginAttempt, authenticationResult)

    }


}
