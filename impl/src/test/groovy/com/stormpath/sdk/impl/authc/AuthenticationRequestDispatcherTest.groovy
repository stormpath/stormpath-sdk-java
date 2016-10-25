package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.api.ApiAuthenticationResult
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationOptions
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.createStrictMock
import static org.powermock.api.easymock.PowerMock.expectNew
import static org.powermock.api.easymock.PowerMock.replay
import static org.powermock.api.easymock.PowerMock.verifyAll
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
@PrepareForTest(AuthenticationRequestDispatcher)
class AuthenticationRequestDispatcherTest extends PowerMockTestCase {

    @Test
    void testAuthenticateDefaultUsernamePasswordRequest() {

        def dataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(DefaultUsernamePasswordRequest)
        def application = createStrictMock(Application)
        def result = createStrictMock(AuthenticationResult)
        def basicAuthenticator = createStrictMock(BasicAuthenticator)

        def appHref = "https://api.stormpath.com/v1/applications/myapplication"

        def authenticationRequestDispatcher = new AuthenticationRequestDispatcher()

        expectNew(BasicAuthenticator, dataStore).andReturn(basicAuthenticator)
        expect(application.getHref()).andReturn(appHref)
        expect(basicAuthenticator.authenticate(appHref, request)).andReturn(result)

        replay dataStore, request, application, result, basicAuthenticator, BasicAuthenticator

        authenticationRequestDispatcher.authenticate(dataStore, application, request)

        verifyAll()
    }

    @Test
    void testAuthenticateBasicApiAuthenticationRequest() {

        def dataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(DefaultBasicApiAuthenticationRequest)
        def application = createStrictMock(Application)
        def result = createStrictMock(ApiAuthenticationResult)
        def basicApiAuthenticator = createStrictMock(BasicApiAuthenticator)

        def authenticationRequestDispatcher = new AuthenticationRequestDispatcher()

        expectNew(BasicApiAuthenticator, dataStore).andReturn(basicApiAuthenticator)
        expect(basicApiAuthenticator.authenticate(application, request)).andReturn(result)

        replay dataStore, request, application, result, basicApiAuthenticator, BasicApiAuthenticator

        authenticationRequestDispatcher.authenticate(dataStore, application, request)

        verifyAll()
    }

    @Test
    void testUnsupportedAuthenticationRequest() {

        def dataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(UnsupportedAuthenticationRequest)
        def application = createStrictMock(Application)

        def authenticationRequestDispatcher = new AuthenticationRequestDispatcher()

        try {
            authenticationRequestDispatcher.authenticate(dataStore, application, request)
            fail("should not be here")
        } catch (UnsupportedOperationException e) {
            assertTrue e.getMessage().contains("is not supported by this implementation")
        }
    }

    class UnsupportedAuthenticationRequest implements AuthenticationRequest {

        @Override
        Object getPrincipals() { return null }

        @Override
        Object getCredentials() { return null }

        @Override
        String getHost() { return null }

        @Override
        void clear() {}

        @Override
        AccountStore getAccountStore() { return null }

        @Override
        AuthenticationOptions getResponseOptions() { return null }

        @Override
        String getOrganizationNameKey() {
            return null
        }
    }
}
