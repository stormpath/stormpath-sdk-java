/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.cache

import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC3
 */
class PropertiesCacheManagerFactoryTest {

    @Test
    void testWithNullArg() {
        def factory = new PropertiesCacheManagerFactory()
        def cacheManager = factory.createCacheManager(null);
        assertNotNull cacheManager
        assertTrue cacheManager instanceof DefaultCacheManager
    }

    @Test
    void testWithEmptyArg() {
        def factory = new PropertiesCacheManagerFactory()
        def cacheManager = factory.createCacheManager(new HashMap<String, String>());
        assertNotNull cacheManager
        assertTrue cacheManager instanceof DefaultCacheManager
    }

    @Test
    void testDefaultCreatedWithNullArg() {
        def factory = new PropertiesCacheManagerFactory()
        assertNotNull factory.createCacheManager();
    }

    @Test
    void testDefaultCreatedWithEmptyArg() {
        def factory = new PropertiesCacheManagerFactory()
        assertNotNull factory.createCacheManager(new HashMap<String, String>());
    }

    @Test
    void testEnabledConfig() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.enabled': 'true']);
        assertNotNull mgr
        assertTrue mgr instanceof DefaultCacheManager
    }

    @Test
    void testDisabledConfig() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.enabled': 'false']);
        assertNotNull mgr
        assertTrue mgr instanceof DisabledCacheManager
    }

    @Test
    void testEnabledConfigWithInvalidValue() {
        def factory = new PropertiesCacheManagerFactory()
        try {
            factory.createCacheManager(['stormpath.client.cacheManager.enabled': 'whatever']);
            fail('expected IllegalArgumentException')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'stormpath.client.cacheManager.enabled value must equal true or false'
        }
    }

    @Test
    void testWithDefaultTti() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.defaultTti': '1001']);
        assertNotNull mgr
        assertEquals mgr.defaultTimeToIdle.value, 1001
    }

    @Test
    void testWithDefaultTtl() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.defaultTtl': '2002']);
        assertNotNull mgr
        assertEquals mgr.defaultTimeToLive.value, 2002
    }

    @Test
    void testCacheRegionTtl() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.caches.foo.ttl': '3003']);
        assertNotNull mgr
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals cache.timeToLive.value, 3003
    }

    @Test
    void testCacheRegionTtlWithNonLongValue() {
        def factory = new PropertiesCacheManagerFactory()
        try {
            factory.createCacheManager(['stormpath.client.cacheManager.caches.foo.ttl': 'whatever']);
            fail('expected IllegalArgumentException')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'Unable to parse stormpath.client.cacheManager.caches.foo.ttl value to a long (milliseconds).'
        }
    }

    @Test
    void testCacheRegionTti() {
        def factory = new PropertiesCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.client.cacheManager.caches.foo.tti': '4004']);
        assertNotNull mgr
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals cache.timeToIdle.value, 4004
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCacheRegionWithNoSuffix() {
        def factory = new PropertiesCacheManagerFactory()
        factory.createCacheManager(['stormpath.client.cacheManager.caches.foo': '4004']);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCacheRegionWithUnrecognizedSuffix() {
        def factory = new PropertiesCacheManagerFactory()
        factory.createCacheManager(['stormpath.client.cacheManager.caches.foo.whatever': '4004']);
    }
}
