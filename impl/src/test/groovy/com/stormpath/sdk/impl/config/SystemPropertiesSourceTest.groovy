package com.stormpath.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class SystemPropertiesSourceTest {

    static {
        System.setProperty("MY_SPECIAL_PROPERTY", "MY_SPECIAL_VALUE")
    }

    @Test
    void test() {

        def systemProperties = new SystemPropertiesSource()
        def properties = systemProperties.getProperties()

        assertEquals properties.get("MY_SPECIAL_PROPERTY"), "MY_SPECIAL_VALUE"
    }
}
