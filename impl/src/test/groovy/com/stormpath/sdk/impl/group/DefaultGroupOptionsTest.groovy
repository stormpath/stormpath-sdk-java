package com.stormpath.sdk.impl.group

import com.stormpath.sdk.group.Groups
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultGroupOptionsTest {

    @Test
    void testDefault() {

        def options = Groups.options();

        assertNotNull options
        assertTrue options instanceof DefaultGroupOptions
    }
}
