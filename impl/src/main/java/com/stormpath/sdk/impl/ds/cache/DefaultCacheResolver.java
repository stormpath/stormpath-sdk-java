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
package com.stormpath.sdk.impl.ds.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.impl.ds.CacheRegionNameResolver;
import com.stormpath.sdk.lang.Assert;

import java.util.Map;

public class DefaultCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;
    private final CacheRegionNameResolver cacheRegionNameResolver;

    public DefaultCacheResolver(CacheManager cacheManager, CacheRegionNameResolver cacheRegionNameResolver) {
        Assert.notNull(cacheManager, "cacheManager cannot be null.");
        Assert.notNull(cacheRegionNameResolver, "cacheRegionNameResolver cannot be null.");
        this.cacheManager = cacheManager;
        this.cacheRegionNameResolver = cacheRegionNameResolver;
    }

    public Cache<String, Map<String, ?>> getCache(Class clazz) {
        Assert.notNull(clazz, "Class argument cannot be null.");
        String cacheRegionName = this.cacheRegionNameResolver.getCacheRegionName(clazz);
        return this.cacheManager.getCache(cacheRegionName);
    }
}
