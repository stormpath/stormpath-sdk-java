package com.stormpath.sdk.impl.query

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 0.8
 */
class CollectionExpansionTest {

    @Test
    void testDefault() {
        def e = new CollectionExpansion('foo', 2, 20)
        assertEquals 2, e.limit
        assertEquals 20, e.offset
        assertEquals e.toString(), 'foo(offset:20,limit:2)'
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullName() {
        new CollectionExpansion(null, 1, 1)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testZeroValues() {
        new CollectionExpansion('foo', 0, 0);
    }

}
