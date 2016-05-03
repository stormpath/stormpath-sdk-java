package com.stormpath.sdk.authc

import com.stormpath.sdk.api.ApiAuthenticationResult
import com.stormpath.sdk.oauth.AccessTokenResult
import com.stormpath.sdk.oauth.OAuthAuthenticationResult
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.easymock.EasyMock.createMock
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
class AuthenticationResultVisitorAdapterTest {

    def authenticationResultVisitorAdapter

    @BeforeTest
    public void setup() {
        authenticationResultVisitorAdapter = new AuthenticationResultVisitorAdapter()
    }

    @Test
    public void visitAuthenticationResult() {
        try {
            authenticationResultVisitorAdapter.visit(createMock(AuthenticationResult.class))
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "visit(AuthenticationResult) is not expected."
        }
    }

    @Test
    public void visitApiAuthenticationResult() {
        try {
            authenticationResultVisitorAdapter.visit(createMock(ApiAuthenticationResult.class))
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "visit(ApiAuthenticationResult) is not expected."
        }
    }

    @Test
    public void visitOAuthAuthenticationResult() {
        try {
            authenticationResultVisitorAdapter.visit(createMock(OAuthAuthenticationResult.class))
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "visit(OAuthAuthenticationResult) is not expected."
        }
    }

    @Test
    public void visitAccessTokenResult() {
        try {
            authenticationResultVisitorAdapter.visit(createMock(AccessTokenResult.class))
            fail("shouldn't be here")
        } catch (UnsupportedOperationException e) {
            assertEquals e.getMessage(), "visit(AccessTokenResult) is not expected."
        }
    }
}
