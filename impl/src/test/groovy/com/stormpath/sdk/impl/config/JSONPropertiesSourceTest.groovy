package com.stormpath.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail
import static org.testng.AssertJUnit.assertTrue

/**
 * @since 1.0
 */
class JSONPropertiesSourceTest {

    @Test
    void testGetProperties() {
        def testStr = "{ \n" +
                "  \"stormpath\": {\n" +
                "    \"web\": {\n" +
                "      \"verify\": {\n" +
                "        \"uri\": \"/double-check\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        def properties = new JSONPropertiesSource(new TestStringResource(testStr)).properties
        assertEquals properties.get("stormpath.web.verify.uri"), "/double-check"
    }

    @Test
    void testGetCollectionProperties() {
        def yamlCollection = "{\n" +
                "  \"collection\": [\n" +
                "    {\"name\": \"Item 1\"}, {\"name\": \"Item 2\"}, {\"name\": \"Item 3\"}\n" +
                "  ]\n" +
                "}"
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlCollection)).properties

        assertEquals properties.get("collection"), "{name=Item 1},{name=Item 2},{name=Item 3}"
    }

    @Test
    void testInvalidProperties() {
        try {
            new JSONPropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}
