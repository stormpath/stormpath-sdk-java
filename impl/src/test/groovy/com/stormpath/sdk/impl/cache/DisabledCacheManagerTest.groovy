package com.stormpath.sdk.impl.cache

import org.testng.annotations.Test

import static org.testng.Assert.assertSame
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DisabledCacheManagerTest {

    @Test
    void testDefault() {

        DisabledCacheManager cacheManager = new DisabledCacheManager();

        def foo = cacheManager.getCache('foo')
        assertTrue foo instanceof DisabledCache
        assertSame foo, DisabledCacheManager.CACHE_INSTANCE

        def bar = cacheManager.getCache('bar')
        assertSame bar, DisabledCacheManager.CACHE_INSTANCE
    }
}
