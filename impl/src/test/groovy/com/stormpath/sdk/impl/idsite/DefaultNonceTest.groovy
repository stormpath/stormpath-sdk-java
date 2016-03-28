package com.stormpath.sdk.impl.idsite

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.AssertJUnit.fail

/**
 * @since 1.0.RC9
 */
class DefaultNonceTest {

    @Test
    void testNonceString() {
        def nonce = new DefaultNonce("http://myhref.com")

        assertEquals nonce.getHref(), "http://myhref.com"
        assertEquals nonce.getValue(), "http://myhref.com"
        assertEquals nonce.getProperties().size(), 1
    }

    @Test
    void testNonceBadString() {
        try {
            new DefaultNonce("")
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank"
        }
    }

    @Test
    void testNonceProperties() {
        def nonce = new DefaultNonce(["value":"http://myhref.com"])

        assertEquals nonce.getHref(), "http://myhref.com"
        assertEquals nonce.getValue(), "http://myhref.com"
        assertEquals nonce.getProperties().size(), 1
    }

    @Test
    void testNonceBadProperties() {
        try {
            new DefaultNonce([:])
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "[Assertion failed] - this map must not be empty; it must contain at least one entry"
        }

        try {
            new DefaultNonce(["key_must_be_value":""])
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "[Assertion failed] - this expression must be true"
        }

        try {
            new DefaultNonce(["value":"", "shouldnt_have_more_than_one_key":""])
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "[Assertion failed] - this expression must be true"
        }

        try {
            new DefaultNonce(["value":true])
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "Object of class [java.lang.Boolean] must be an instance of class java.lang.String"
        }
    }
}
