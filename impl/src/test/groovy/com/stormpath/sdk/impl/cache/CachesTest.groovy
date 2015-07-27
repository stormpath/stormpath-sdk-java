/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.cache

import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.lang.Duration
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static com.stormpath.sdk.cache.Caches.*
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

    @Test
    void testNewDisabledCacheManager() {

        CacheManager cm = newDisabledCacheManager();

        assertNotNull cm
        assertTrue cm instanceof DisabledCacheManager
    }
}
