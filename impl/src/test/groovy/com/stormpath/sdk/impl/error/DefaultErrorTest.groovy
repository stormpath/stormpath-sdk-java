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
        finishTest(defaultError);
    }

    /*
     * see https://github.com/stormpath/stormpath-sdk-java/pull/770
     */
    @Test
    void testtestGetPropertyDescriptorsDefaultConstructor() {
        def defaultError = new DefaultError()
        finishTest(defaultError);
    }

    private void finishTest(DefaultError defaultError) {
        assertEquals defaultError.propertyDescriptors.keySet().size(), 5
        [
            DefaultError.STATUS, DefaultError.CODE, DefaultError.DEV_MESSAGE,
            DefaultError.MESSAGE, DefaultError.MORE_INFO
        ].each {
            assertTrue defaultError.propertyDescriptors.keySet().contains(it.name)
        }
    }
}
