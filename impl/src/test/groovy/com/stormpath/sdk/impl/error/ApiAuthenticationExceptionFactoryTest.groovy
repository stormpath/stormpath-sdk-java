package com.stormpath.sdk.impl.error

import com.stormpath.sdk.error.authc.InvalidAuthenticationException
import com.stormpath.sdk.error.authc.OAuthAuthenticationException
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class ApiAuthenticationExceptionFactoryTest {

    @Test
    void testInvalidAuthenticationException() {

        def devMessage = "Testing InvalidAuthenticationException"

        def r = ApiAuthenticationExceptionFactory.newApiAuthenticationException(
            InvalidAuthenticationException, devMessage
        )

        assertEquals r.code, ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_CODE
        assertEquals r.status, ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_STATUS
        assertEquals r.moreInfo, ApiAuthenticationExceptionFactory.MORE_INFO
        assertEquals r.message,
            "HTTP " + ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_CODE +
            ", Stormpath " + ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_STATUS +
            " (" + ApiAuthenticationExceptionFactory.MORE_INFO + "): " + devMessage
        assertEquals r.developerMessage, devMessage
    }
    @Test
    void testOAuthAuthenticationException() {

        def devMessage = "Testing OAuthenticationException"

        def r = ApiAuthenticationExceptionFactory.newOAuthException(OAuthAuthenticationException, devMessage)

        assertEquals r.code, ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_CODE
        assertEquals r.status, ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_STATUS
        assertEquals r.moreInfo, ApiAuthenticationExceptionFactory.MORE_INFO
        assertEquals r.message,
            "HTTP " + ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_CODE +
            ", Stormpath " + ApiAuthenticationExceptionFactory.AUTH_EXCEPTION_STATUS +
            " (" + ApiAuthenticationExceptionFactory.MORE_INFO + "): " + devMessage

        assertEquals r.developerMessage, devMessage
    }

}
