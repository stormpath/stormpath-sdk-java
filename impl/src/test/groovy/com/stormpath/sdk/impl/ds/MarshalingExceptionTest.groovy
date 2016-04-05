package com.stormpath.sdk.impl.ds

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class MarshalingExceptionTest {

    @Test
    void testConstructWithString() {

        def marshalingException = new MarshalingException("kaboom")
        assertEquals marshalingException.getMessage(), "kaboom"
    }

    @Test
    void testConstructWithThrowable() {

        def marshalingException = new MarshalingException(new IOException())
        assertEquals marshalingException.getCause().class, IOException
    }

    @Test
    void testConstructorWithStringAndThrowable() {

        def marshalingException = new MarshalingException("kaboom", new IOException())
        assertEquals marshalingException.getCause().class, IOException
        assertEquals marshalingException.getMessage(), "kaboom"
    }
}
