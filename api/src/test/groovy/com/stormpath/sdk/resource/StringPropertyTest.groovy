package com.stormpath.sdk.resource

import org.testng.annotations.Test

import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertSame

/**
 *
 * @since 0.8
 */
class StringPropertyTest {

    @Test
    void testDefault() {
        def prop = new StringProperty('foo')
        assertSame prop.type, String.class
        assertFalse prop.required
    }
}
