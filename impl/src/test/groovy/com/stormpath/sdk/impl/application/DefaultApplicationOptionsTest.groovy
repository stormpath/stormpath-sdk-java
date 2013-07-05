package com.stormpath.sdk.impl.application

import com.stormpath.sdk.application.Applications
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultApplicationOptionsTest {

    @Test
    void testDefault() {

        def options = Applications.options();

        assertNotNull options
        assertTrue options instanceof DefaultApplicationOptions
    }
}
