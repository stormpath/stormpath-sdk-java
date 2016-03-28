package com.stormpath.sdk.impl.authc

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class BasicApiAuthenticationTokenTest {

    @Test
    void testBasicApiAuthenticationToken() {

        def token = new BasicApiAuthenticationToken("my id", "my secret")

        assertEquals token.id, "my id"
        assertEquals token.secret, "my secret"
    }
}
