package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest
import com.stormpath.sdk.impl.util.Base64;
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

class AccessTokenAuthenticationRequestTest {

    def httpRequest = createMock(HttpRequest.class)
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

        expect(httpRequest.getParameters())
                .andReturn(params).times(6)
        expect(httpRequest.getMethod())
                .andReturn(HttpMethod.POST)
        expect(httpRequest.getHeader("Content-Type"))
                .andReturn("application/x-www-form-urlencoded")
        expect(httpRequest.getHeader("Authorization"))
                .andReturn("Basic " + Base64.encodeBase64String("id:secret".bytes)).times(2)

        replay httpRequest

        accessTokenAuthenticationRequest = new AccessTokenAuthenticationRequest(httpRequest)
    }

    @Test
    public void testGetHost() {

        try {
            accessTokenAuthenticationRequest.getHost()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "getHost() method hasn't been implemented."
        }
    }

    @Test
    public void testClear() {

        try {
            accessTokenAuthenticationRequest.clear()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "clear() method hasn't been implemented."
        }
    }

    @Test
    public void testGetResponseOptions() {

        try {
            accessTokenAuthenticationRequest.getResponseOptions()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest.getResponseOptions() is not supported."
        }
    }

    @Test
    public void testGetAccountStore() {

        try {
            accessTokenAuthenticationRequest.getAccountStore()
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "getAccountStore() method hasn't been implemented."
        }
    }

    @Test
    public void testDefaultTtl() {

        assertEquals accessTokenAuthenticationRequest.getTtl(), AccessTokenAuthenticationRequest.DEFAULT_TTL
    }

    @Test
    public void testDefaultScopeFactory() {

        assertEquals accessTokenAuthenticationRequest.getScopeFactory(), null
    }

    @Test
    public void testDefaultHasScopeFactory() {

        assertEquals accessTokenAuthenticationRequest.hasScopeFactory(), false
    }

    @Test
    public void testGetClentId() {

        assertEquals accessTokenAuthenticationRequest.getClientId(), "id"
    }

    @Test
    public void testGetClientSecret() {

        assertEquals accessTokenAuthenticationRequest.getClientSecret(), "secret"
    }

    @Test
    public void testGetPrincipals() {

        assertEquals accessTokenAuthenticationRequest.getPrincipals(), "id"
    }

    @Test
    public void testGetCredentials() {

        assertEquals accessTokenAuthenticationRequest.getCredentials(), "secret"
    }
}