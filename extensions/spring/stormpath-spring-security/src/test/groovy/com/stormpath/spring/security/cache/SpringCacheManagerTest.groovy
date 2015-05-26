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
class SpringCacheManagerTest {

    @Test(expected = IllegalArgumentException)
    void testNullCacheManager() {
        new SpringCacheManager(null)
    }

    @Test
    void testGetCache() {

        def springCacheManager = EasyMock.createStrictMock(org.springframework.cache.CacheManager)
        def springCache = EasyMock.createStrictMock(org.springframework.cache.Cache )

        def cacheName = 'name'

        EasyMock.expect(springCacheManager.getCache(EasyMock.same(cacheName))).andReturn springCache

        EasyMock.replay springCache, springCacheManager

        SpringCacheManager cacheManager = new SpringCacheManager(springCacheManager)
        def cache = cacheManager.getCache(cacheName)
        Assert.assertNotNull(cache)
        Assert.assertSame springCache, cache.SPRING_CACHE

        EasyMock.verify springCache, springCacheManager
    }

}
