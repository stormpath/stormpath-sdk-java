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

import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.impl.util.Duration
import org.junit.Before
import org.junit.Test

import java.util.concurrent.TimeUnit

import static org.junit.Assert.*

/**
 * @since 0.8
 */
class DefaultCacheManagerTest {

    DefaultCacheManager mgr;

    @Before
    void setUp() {
        this.mgr = new DefaultCacheManager();
    }

    @Test
    void testGetCache() {
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals 'foo', cache.name
        assertSame cache, mgr.getCache('foo')
    }

    @Test
    void testGetCachePutIfAbsent() {
        mgr = new DefaultCacheManager() {
            @Override
            protected Cache createCache(String name) {
                Cache first = super.createCache(name)

                //simulate something else putting a cache with the same name in
                //we should see this cache and not the first one created:
                Cache second = super.createCache(name);
                second.put('key', 'value');
                caches.put(name, second);

                return first;
            }
        }

        def cache = mgr.getCache('foo')
        assertNotNull cache
        assertEquals 'foo', cache.name
        assertEquals 'value', cache.get('key')
    }

    @Test
    void testDefaultTtl() {
        Duration ttl = new Duration(30, TimeUnit.SECONDS)
        mgr.setDefaultTimeToLive(ttl)

        def cache = mgr.getCache('foo')
        assertEquals ttl, cache.timeToLive
    }

    @Test
    void testDefaultTtlSeconds() {
        mgr.setDefaultTimeToLiveSeconds(30)
        assertEquals new Duration(30, TimeUnit.SECONDS), mgr.defaultTimeToLive
    }

    @Test
    void testDefaultTti() {
        Duration tti = new Duration(20, TimeUnit.SECONDS)
        mgr.setDefaultTimeToIdle(tti)

        def cache = mgr.getCache('foo')
        assertEquals tti, cache.timeToIdle
    }

    @Test
    void testDefaultTtiSeconds() {
        mgr.setDefaultTimeToIdleSeconds(30)
        assertEquals new Duration(30, TimeUnit.SECONDS), mgr.defaultTimeToIdle
    }

    @Test
    void testToString() {
        assertEquals 'DefaultCacheManager with 0 cache(s)): []', mgr.toString()
        Cache foo = mgr.getCache('foo')
        assertEquals "DefaultCacheManager with 1 cache(s)): [$foo]" as String, mgr.toString()
        Cache bar = mgr.getCache('bar')
        assertEquals "DefaultCacheManager with 2 cache(s)): [$foo, $bar]" as String, mgr.toString()
    }

}
