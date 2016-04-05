package com.stormpath.sdk.impl.io

import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertNotNull

/**
 * @since 1.0.RC9
 */
class ClasspathResourceTest {

    @Test
    void testInputStream() {
        def classpathResource = new ClasspathResource("classpath:com/stormpath/sdk/impl/io/DefaultResourceFactory.class")

        assertNotNull classpathResource.inputStream
    }
}
