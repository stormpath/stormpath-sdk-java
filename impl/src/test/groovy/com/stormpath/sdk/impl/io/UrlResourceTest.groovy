package com.stormpath.sdk.impl.io

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

/**
 * @since 1.0.RC9
 */
class UrlResourceTest {

    @Test
    void testLocation() {
        def resource = new UrlResource("url:https://www.google.com")

        assertEquals resource.location, "https://www.google.com"

        // This doesn't seem right, but it's how the code is setup right now
        assertEquals resource.toString(), "urlhttps://www.google.com"
    }

    @Test
    void testInputStream() {
        def resource = new UrlResource("url:https://www.google.com")

        assertNotNull resource.inputStream
    }
}
