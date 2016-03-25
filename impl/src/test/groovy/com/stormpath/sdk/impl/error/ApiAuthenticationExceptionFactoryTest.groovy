package com.stormpath.sdk.impl.error

import com.stormpath.sdk.error.authc.InvalidAuthenticationException
import com.stormpath.sdk.error.authc.OauthAuthenticationException
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
    void testOauthAuthenticationException() {

        def devMessage = "Testing OauthenticationException"

        def r = ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException, devMessage)

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
