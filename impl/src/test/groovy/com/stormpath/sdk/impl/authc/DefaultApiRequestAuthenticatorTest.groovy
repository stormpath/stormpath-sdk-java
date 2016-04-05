package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.api.ApiAuthenticationResult
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.impl.http.ServletHttpRequest
import org.testng.annotations.Test

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.createPartialMock
import static org.powermock.api.easymock.PowerMock.createStrictMock
import static org.powermock.api.easymock.PowerMock.replayAll
import static org.powermock.api.easymock.PowerMock.verifyAll
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class DefaultApiRequestAuthenticatorTest {

    @Test
    void testConstructWithApplicationAndHttpRequest() {

        def application = createStrictMock(Application)
        def httpRequest = createStrictMock(HttpRequest)

        replayAll()

        new DefaultApiRequestAuthenticator(application, httpRequest)

        verifyAll()
    }

    @Test
    void testConstructWithApplicationAndServletHttpRequest() {

        def application = createStrictMock(Application)
        def httpRequest = createStrictMock(ServletHttpRequest)

        replayAll()

        new DefaultApiRequestAuthenticator(application, httpRequest)

        verifyAll()
    }

    @Test
    void testExecuteServletHttpRequest() {

        def httpRequest = createStrictMock(ServletHttpRequest)
        def expectedResult = createStrictMock(ApiAuthenticationResult)

        def defaultApiRequestAuthenticator = createPartialMock(DefaultApiRequestAuthenticator, 'execute')

        expect(defaultApiRequestAuthenticator.execute()).andReturn(expectedResult)

        replayAll()

        def actualResult = defaultApiRequestAuthenticator.authenticate(httpRequest)

        assertEquals actualResult, expectedResult
    }

    @Test
    void testExecuteHttpRequest() {

        def httpRequest = createStrictMock(HttpRequest)
        def expectedResult = createStrictMock(ApiAuthenticationResult)

        def defaultApiRequestAuthenticator = createPartialMock(DefaultApiRequestAuthenticator, 'execute')

        expect(defaultApiRequestAuthenticator.execute()).andReturn(expectedResult)

        replayAll()

        def actualResult = defaultApiRequestAuthenticator.authenticate(httpRequest)

        assertEquals actualResult, expectedResult
    }
}
