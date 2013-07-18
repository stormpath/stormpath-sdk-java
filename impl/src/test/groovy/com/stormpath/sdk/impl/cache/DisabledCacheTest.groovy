package com.stormpath.sdk.impl.cache

import org.testng.annotations.Test

import static org.testng.Assert.assertNull

/**
 * @since 0.8
 */
class DisabledCacheTest {

    @Test
    void testDefault() {

        DisabledCache cache = new DisabledCache();

        def returned = cache.put('foo', 'bar')
        assertNull returned

        returned = cache.get('foo')
        assertNull returned

        cache.put('foo', 'bar')
        assertNull cache.remove('foo')
    }
}
