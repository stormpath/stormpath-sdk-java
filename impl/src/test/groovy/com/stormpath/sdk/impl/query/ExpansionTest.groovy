package com.stormpath.sdk.impl.query

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 0.8
 */
class ExpansionTest {

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullArgument() {
        new Expansion(null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testEmptyArgument() {
        new Expansion('')
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testOnlyWhitespaceArgument() {
        new Expansion('   ')
    }

    @Test
    void testDefault() {
        String name = 'foo'
        def expansion = new Expansion(name)

        assertEquals expansion.name, name
        assertEquals expansion.toString(), name
    }
}
