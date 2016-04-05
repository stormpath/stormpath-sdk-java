package com.stormpath.sdk.impl.io

import org.testng.annotations.Test

import java.nio.charset.Charset

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail
import static org.testng.AssertJUnit.assertNotNull

/**
 * @since 1.0.RC9
 */
class StringResourceTest {

    @Test
    void testInputStream() {
        def resource = new StringResource("My Special String")

        assertNotNull resource.getInputStream()
    }

    @Test
    void testWithNullString() {
        try {
            new StringResource(null, Charset.forName("UTF-8"))
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "String argument cannot be null or empty."
        }
    }

    @Test
    void testWithNullCharset() {
        try {
            new StringResource("My Special String", null)
            fail "shouldn't be here"
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "Charset argument cannot be null or empty."
        }
    }
}
