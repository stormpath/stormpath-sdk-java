package com.stormpath.sdk.impl.io

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertTrue

/**
 * @since 1.0.RC9
 */
class DefaultResourceFactoryTest {

    def resourceFactory

    @BeforeMethod
    void setup() {
        resourceFactory = new DefaultResourceFactory()
    }

    @Test
    void testCreateResourceClasspath() {
        def resource = resourceFactory.createResource("classpath:com/stormpath/sdk/impl/io/DefaultResourceFactory.class")

        assertTrue resource instanceof ClasspathResource
    }

    @Test
    void testCreateResourceUrl() {
        def resource = resourceFactory.createResource("https://www.google.com")

        assertTrue resource instanceof UrlResource
    }
}
