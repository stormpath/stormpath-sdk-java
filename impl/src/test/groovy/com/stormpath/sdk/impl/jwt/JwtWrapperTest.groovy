package com.stormpath.sdk.impl.jwt

import com.stormpath.sdk.error.jwt.InvalidJwtException
import org.powermock.core.classloader.annotations.PrepareForTest
import org.testng.annotations.Test

import static org.powermock.api.easymock.PowerMock.createPartialMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
@PrepareForTest(JwtWrapper)
class JwtWrapperTest {

    @Test
    void testNullJwt() {
        try {
            new JwtWrapper(null)
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.JWT_REQUIRED_ERROR
        }
    }

    @Test
    void testgetJsonHeaderAsMapException() {
        // Cheating a little here - we don't really need getBase64JwtSignature to be a mocked method
        // but this will allow us to exercise null conditions in other methods that are not mocked
        def jwtWrapper = createPartialMock(JwtWrapper, 'getBase64JwtSignature')

        try {
            jwtWrapper.getJsonHeaderAsMap()
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.INVALID_JWT_HEADER_ENCODING_ERROR
        }
    }

    @Test
    void testGetJsonPayloadAsMapException() {
        // Cheating a little here - we don't really need getBase64JwtSignature to be a mocked method
        // but this will allow us to exercise null conditions in other methods that are not mocked
        def jwtWrapper = createPartialMock(JwtWrapper, 'getBase64JwtSignature')

        try {
            jwtWrapper.getJsonPayloadAsMap()
            fail "shouldn't be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.INVALID_JWT_BODY_ENCODING_ERROR
        }
    }
}
