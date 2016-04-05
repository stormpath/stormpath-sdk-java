package com.stormpath.sdk.impl.config

import com.stormpath.sdk.impl.io.Resource
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail
import static org.testng.AssertJUnit.assertTrue

/**
 * @since 1.0.RC9
 */
class ResourcePropertiesSourceTest {

    @Test
    void testGetProperties() {

        def testStr = "stormpath.web.verify.nextUri=/login?status=verified"
        def properties = new ResourcePropertiesSource(new TestStringResource(testStr)).properties

        assertEquals properties.get("stormpath.web.verify.nextUri"), "/login?status=verified"
    }

    @Test
    void testInvalidProperties() {

        try {
            new ResourcePropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}

class BadResource implements Resource {

    @Override
    InputStream getInputStream() throws IOException {
        throw new IOException("BadResource")
    }
}