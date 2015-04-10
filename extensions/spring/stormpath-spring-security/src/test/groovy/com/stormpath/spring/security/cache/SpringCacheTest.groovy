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
package com.stormpath.spring.security.cache

import org.easymock.EasyMock
import org.junit.Assert
import org.junit.Test

/**
 * @since 0.2.0
 */
class SpringCacheTest {

    @Test(expected = IllegalArgumentException)
    void testNullSpringCache() {
        new SpringCache(null)
    }

    @Test
    void testGet() {

        def springCache = EasyMock.createStrictMock(org.springframework.cache.Cache)
        def valueWrapper = EasyMock.createStrictMock(org.springframework.cache.Cache.ValueWrapper)

        def key = 'key'
        def value = 'value'

        EasyMock.expect(springCache.get(key)).andReturn valueWrapper
        EasyMock.expect(valueWrapper.get()) andReturn value

        EasyMock.replay(springCache, valueWrapper)

        def cache = new SpringCache(springCache)
        def retval = cache.get(key)

        Assert.assertSame value, retval

        EasyMock.verify(springCache, valueWrapper)
    }

    @Test
    void testGetNull() {

        def springCache = EasyMock.createStrictMock(org.springframework.cache.Cache)

        def key = 'key'
        def value = 'value'

        EasyMock.expect(springCache.get(key)).andReturn null

        EasyMock.replay springCache

        def cache = new SpringCache(springCache)
        def retval = cache.get(key)

        Assert.assertSame null, retval

        EasyMock.verify springCache
    }


    @Test
    void testPut() {

        def springCache = EasyMock.createStrictMock(org.springframework.cache.Cache )
        def valueWrapper = EasyMock.createStrictMock(org.springframework.cache.Cache.ValueWrapper)

        def key = 'key'
        def value = 'value1'
        def prev = 'value0'

        EasyMock.expect(springCache.get(key)) andReturn valueWrapper
        EasyMock.expect(springCache.put(EasyMock.same(key), EasyMock.same(value)))
        EasyMock.expect(valueWrapper.get()) andReturn prev

        EasyMock.replay(springCache, valueWrapper)

        def cache = new SpringCache(springCache)

        def retval = cache.put(key, value)

        Assert.assertSame prev, retval

        EasyMock.verify(springCache, valueWrapper)
    }

    @Test
    void testRemove() {

        def springCache = EasyMock.createStrictMock(org.springframework.cache.Cache)
        def valueWrapper = EasyMock.createStrictMock(org.springframework.cache.Cache.ValueWrapper)

        def key = 'key'
        def prev = 'value0'

        EasyMock.expect(springCache.get(key)) andReturn valueWrapper
        EasyMock.expect(springCache.evict(key))
        EasyMock.expect(valueWrapper.get()) andReturn prev

        EasyMock.replay(springCache, valueWrapper)

        def cache = new SpringCache(springCache)

        def retval = cache.remove(key)

        Assert.assertSame prev, retval

        EasyMock.verify(springCache, valueWrapper)
    }

}
