package com.stormpath.sdk.impl.config

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

        def testStr = "stormpath.web.verifyEmail.nextUri=/login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)
            assertEquals(result.size(), 1)
            assertEquals(result.get("stormpath.web.verifyEmail.nextUri"), "/login?status=verified")
        }
    }

    @Test
    void testKeyValueLotsOfWhitespace() {

        def testStr = "stormpath.web.verifyEmail.nextUri                                                     =                                         /login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)
            assertEquals(result.size(), 1)
            assertEquals(result.get("stormpath.web.verifyEmail.nextUri"), "/login?status=verified")
        }
    }

    @Test
    void testKeyNoValue() {

        def testStr = "stormpath.web.verifyEmail.nextUri = "
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 1)
            assertEquals(result.get("stormpath.web.verifyEmail.nextUri"), null)
        }
    }

    @Test
    void testValueNoKey() {

        def testStr = " = /login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            try {
                parser.parse(it)
                fail()
            } catch (IllegalArgumentException iae) {
                assertEquals(iae.message, "Line argument must contain a key. None was found.")
            }
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testMultiLine() {

        def testStr =
            "stormpath.web.verifyEmail.nextUri=/login?status=verified\n" +
            "stormpath.web.login.nextUri=/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("stormpath.web.login.nextUri"), "/")
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testComments() {

        def testStr =
            "stormpath.web.verifyEmail.nextUri=/login?status=verified\n" +
            "# this is a comment\n" +
            "; this is also a comment\n" +
            "stormpath.web.login.nextUri=/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("stormpath.web.login.nextUri"), "/")
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testContinuation() {

        def testStr =
                "stormpath.web.verifyEmail.nextUri = \\\n" +
                    "/login?status=verified\n" +
                "stormpath.web.login.nextUri = \\\n" +
                    "/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("stormpath.web.login.nextUri"), "/")
        }
    }
}
