package com.stormpath.sdk.impl.cache

import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.impl.util.Duration
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static com.stormpath.sdk.cache.Caches.named
import static com.stormpath.sdk.cache.Caches.newCacheManager
import static org.testng.Assert.*

/**
 *
 * @since 0.8
 */
class CachesTest {

    @Test
    void testBuild() {

        def defaultTtl = new Duration(10, TimeUnit.MINUTES)
        def defaultTti = new Duration(5, TimeUnit.HOURS)

        CacheManager m = newCacheManager()
                .withDefaultTimeToLive(-1, defaultTtl.timeUnit)
                .withDefaultTimeToLive(defaultTtl.value, defaultTtl.timeUnit)
                .withDefaultTimeToIdle(defaultTti.value, defaultTti.timeUnit)
                .withCache(named('foo').withTimeToLive(20, TimeUnit.MINUTES).withTimeToIdle(15, TimeUnit.MINUTES))
                .withCache(named('bar').withTimeToLive(-1, TimeUnit.HOURS).withTimeToIdle(0, TimeUnit.HOURS))
                .build()

        assertNotNull m
        assertTrue m instanceof DefaultCacheManager
        DefaultCacheManager manager = (DefaultCacheManager)m

        assertEquals manager.defaultTimeToLive, defaultTtl
        assertEquals manager.defaultTimeToIdle, defaultTti

        def c = manager.getCache('foo')
        assertNotNull c
        assertTrue c instanceof DefaultCache
        DefaultCache cache = (DefaultCache)c

        assertEquals cache.timeToLive, new Duration(20, TimeUnit.MINUTES)
        assertEquals cache.timeToIdle, new Duration(15, TimeUnit.MINUTES)

        cache = (DefaultCache)manager.getCache('bar')

        assertEquals cache.timeToLive, defaultTtl
        assertEquals cache.timeToIdle, defaultTti
    }
}
