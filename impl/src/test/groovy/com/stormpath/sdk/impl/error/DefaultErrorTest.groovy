package com.stormpath.sdk.impl.error

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC9
 */
class DefaultErrorTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultError = new DefaultError(new HashMap<String, Object>())

        assertEquals defaultError.propertyDescriptors.keySet().size(), 5
        [
            DefaultError.STATUS, DefaultError.CODE, DefaultError.DEV_MESSAGE,
            DefaultError.MESSAGE, DefaultError.MORE_INFO
        ].each {
            assertTrue defaultError.propertyDescriptors.keySet().contains(it.name)
        }
    }
}
