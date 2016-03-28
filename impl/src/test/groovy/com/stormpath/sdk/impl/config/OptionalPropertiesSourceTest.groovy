package com.stormpath.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
class OptionalPropertiesSourceTest {

    @Test
    void testGetPropertiesSuccess() {

        def properties = new OptionalPropertiesSource(new PropertiesSource() {
            @Override
            Map<String, String> getProperties() {
                return ["my_special_key":"my_special_value"]
            }
        })

        assertEquals properties.getProperties().get("my_special_key"), "my_special_value"
    }

    @Test
    void testGetPropertiesFail() {

        def properties = new OptionalPropertiesSource(new PropertiesSource() {
            @Override
            Map<String, String> getProperties() {
                throw new Exception("Fell down, go boom")
            }
        })

        def result = properties.getProperties()
        assertEquals result.size(), 0
    }
}
