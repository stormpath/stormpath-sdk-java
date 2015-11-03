package com.stormpath.sdk.impl.config

import com.stormpath.sdk.impl.io.Resource
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

class DefaultPropertiesParserTest {
    def parser

    @BeforeTest
    void setup() {
        parser = new DefaultPropertiesParser()
    }

    @Test
    void testKeyValueNoWhitespace() {
        def resource = new StringResource("stormpath.web.verify.nextUri=/login?status=verified")
        def result = parser.parse(resource)

        assertEquals(result.size(), 1)
        assertEquals(result.get("stormpath.web.verify.nextUri"), "/login?status=verified")
    }

    @Test
    void testKeyValueLotsOfWhitespace() {
        def resource = new StringResource("stormpath.web.verify.nextUri                                                     =                                         /login?status=verified")
        def result = parser.parse(resource)

        assertEquals(result.size(), 1)
        assertEquals(result.get("stormpath.web.verify.nextUri"), "/login?status=verified")
    }

    @Test
    void testKeyNoValue() {
        def resource = new StringResource("stormpath.web.verify.nextUri = ")
        def result = parser.parse(resource)

        assertEquals(result.size(), 1)
        assertEquals(result.get("stormpath.web.verify.nextUri"), null)
    }

    @Test
    void testValueNoKey() {
        def resource = new StringResource(" = /login?status=verified")

        try {
            parser.parse(resource)
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals(iae.message, "Line argument must contain a key. None was found.")
        }
    }
}

class StringResource implements Resource {
    private String string

    StringResource(String string) {
        this.string = string
    }

    @Override
    InputStream getInputStream() throws IOException {
        new ByteArrayInputStream(string.bytes)
    }
}
