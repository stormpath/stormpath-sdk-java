package com.stormpath.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail
import static org.testng.AssertJUnit.assertTrue
/**
 * @since 1.0
 */
class YAMLPropertiesSourceTest {

    @Test
    void testGetProperties() {
        def testStr = "stormpath:\n  web:\n    verify:\n      nextUri: /register?status=verified"
        def properties = new YAMLPropertiesSource(new TestStringResource(testStr)).properties

        assertEquals properties.get("stormpath.web.verify.nextUri"), "/register?status=verified"
    }

    @Test
    void testInvalidProperties() {
        try {
            new YAMLPropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}