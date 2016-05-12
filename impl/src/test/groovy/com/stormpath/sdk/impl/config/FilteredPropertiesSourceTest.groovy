package com.stormpath.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 1.0.RC9
 */
class FilteredPropertiesSourceTest {

    @Test
    void test() {

        def filteredPropertiesSource = new FilteredPropertiesSource(
            new OptionalPropertiesSource(
                new PropertiesSource() {
                    @Override
                    Map<String, String> getProperties() {
                        return [
                            "stormpath_key_one":"stormpath_value_one",
                            "my_special_key":"my_special_value"
                        ]
                    }
                }
            ),
            new FilteredPropertiesSource.Filter() {
                @Override
                public String[] map(String key, String value) {
                    // only return properties that start with stormpath_
                    if (key.startsWith("stormpath_")) {
                        return [key, value].toArray()
                    }
                    return null
                }
            }
        )
        def result = filteredPropertiesSource.getProperties()

        assertNull result.get("my_special_key")
        assertEquals result.get("stormpath_key_one"), "stormpath_value_one"
    }
}
