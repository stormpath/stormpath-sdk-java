package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import sun.misc.BASE64Encoder

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

class AccessTokenAuthenticationRequestTest {

    def httpServletRequest = createMock(HttpRequest.class)
    AccessTokenAuthenticationRequest accessTokenAuthenticationRequest

    @BeforeTest
    public void setup() {
        def params = [
                grant_type: ["password"] as String[],
                username: ["blarg@blargity.com"] as String[],
                password: ["password"] as String[],
                client_id: ["id"] as String[],
                client_secret: ["secret"] as String[]
        ]

        expect(httpServletRequest.getParameters())
                .andReturn(params).times(6)
        expect(httpServletRequest.getMethod())
                .andReturn(HttpMethod.POST)
        expect(httpServletRequest.getHeader("Content-Type"))
                .andReturn("application/x-www-form-urlencoded")
        expect(httpServletRequest.getHeader("Authorization"))
                .andReturn("Basic " + new BASE64Encoder().encode("a:b".bytes)).times(2)

        replay httpServletRequest

        accessTokenAuthenticationRequest = new AccessTokenAuthenticationRequest(httpServletRequest)
    }

    @Test
    public void testGetHost() {

        try {
            accessTokenAuthenticationRequest.getHost()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "getHost() method hasn't been implemented.")
        }
    }

    @Test
    public void testClear() {

        try {
            accessTokenAuthenticationRequest.clear()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "clear() method hasn't been implemented.")
        }
    }

    @Test
    public void testGetResponseOptions() {

        try {
            accessTokenAuthenticationRequest.getResponseOptions()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest.getResponseOptions() is not supported.")
        }
    }
}
