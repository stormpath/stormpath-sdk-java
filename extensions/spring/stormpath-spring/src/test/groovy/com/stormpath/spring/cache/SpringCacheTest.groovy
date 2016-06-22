/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.spring.cache

import org.springframework.cache.concurrent.ConcurrentMapCache
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.2.0
 */
class SpringCacheTest {

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullSpringCache() {
        new SpringCache(null)
    }

    @Test
    void testGet() {

        def springCache = createStrictMock(org.springframework.cache.Cache)
        def valueWrapper = createStrictMock(org.springframework.cache.Cache.ValueWrapper)

        def key = 'key'
        def value = 'value'

        expect(springCache.get(key)).andReturn valueWrapper
        expect(valueWrapper.get()) andReturn value

        replay(springCache, valueWrapper)

        def cache = new SpringCache(springCache)
        def retval = cache.get(key)

        assertSame value, retval

        verify(springCache, valueWrapper)
    }

    @Test
    void testGetNull() {

        def springCache = createStrictMock(org.springframework.cache.Cache)

        def key = 'key'
        def value = 'value'

        expect(springCache.get(key)).andReturn null

        replay springCache

        def cache = new SpringCache(springCache)
        def retval = cache.get(key)

        assertSame null, retval

        verify springCache
    }


    @Test
    void testPut() {

        def cache = new SpringCache(new ConcurrentMapCache('foo'))

        def key = 'key'
        def value = 'value1'
        def prev = 'value0'

        def val = cache.get(key)
        assertNull val
        assertNotNull cache.put(key, prev)

        val = cache.put(key, value)

        assertNotSame prev, val
    }

    @Test
    void testRemove() {

        def springCache = createStrictMock(org.springframework.cache.Cache)
        def valueWrapper = createStrictMock(org.springframework.cache.Cache.ValueWrapper)

        def key = 'key'
        def prev = 'value0'

        expect(springCache.get(key)) andReturn valueWrapper
        expect(springCache.evict(key))
        expect(valueWrapper.get()) andReturn prev

        replay(springCache, valueWrapper)

        def cache = new SpringCache(springCache)

        def retval = cache.remove(key)

        assertSame prev, retval

        verify(springCache, valueWrapper)
    }

}
