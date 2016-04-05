package com.stormpath.sdk.impl.http

import com.stormpath.sdk.http.HttpMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 1.0.RC9
 */
class DefaultHttpRequestTest {

    @Test
    void testGetHeaderNoHeaders() {

        def httpRequest = new DefaultHttpRequest([:], HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Header")
    }

    @Test
    void testGetHeaderNullValue() {

        def headers = ["My-Header":null]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Header")
    }

    @Test
    void testGetHeaderNotFound() {

        def headers = ["My-Header":["My-Value"] as String[]]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Other-Header")
    }

    @Test
    void testGetHeaderFound() {

        def headers = ["My-Header":["My-Value"] as String[]]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertEquals httpRequest.getHeader("My-Header"), "My-Value"
    }

    @Test
    void testGetParameterNull() {

        def httpRequest = new DefaultHttpRequest([:], HttpMethod.GET, null, null)
        assertNull httpRequest.getParameter("My-Parameter")
    }
}
