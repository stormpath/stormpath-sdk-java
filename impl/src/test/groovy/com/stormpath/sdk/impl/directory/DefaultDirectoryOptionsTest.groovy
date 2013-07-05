package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.Directories
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultDirectoryOptionsTest {

    @Test
    void testDefault() {

        def options = Directories.options();

        assertNotNull options
        assertTrue options instanceof DefaultDirectoryOptions
    }
}
