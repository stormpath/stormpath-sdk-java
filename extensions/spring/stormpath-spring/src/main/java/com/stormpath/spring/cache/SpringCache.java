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
package com.stormpath.spring.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.Assert;

/**
 * Implementation of the Stormpath {@link Cache} interface that delegates to a {@link org.springframework.cache.Cache
 * org.springframework.cache.Cache} instance.
 *
 * @param <K> The cache key type
 * @param <V> The cache value type
 * @since 1.0.RC4
 */
@SuppressWarnings("unchecked")
public class SpringCache<K, V> implements Cache<K, V> {

    private final org.springframework.cache.Cache springCache;

    public SpringCache(org.springframework.cache.Cache springCache) {
        Assert.notNull(springCache, "spring cache instance cannot be null.");
        this.springCache = springCache;
    }

    @Override
    public V get(K key) {
        org.springframework.cache.Cache.ValueWrapper vw = springCache.get(key);
        if (vw == null) {
            return null;
        }
        return (V) vw.get();
    }

    @Override
    public V put(K key, V value) {
        org.springframework.cache.Cache.ValueWrapper vw = springCache.putIfAbsent(key, value);
        if (vw == null) {
            return null;
        }
        return (V) vw.get();
    }

    @Override
    public V remove(K key) {
        V v = get(key);
        springCache.evict(key);
        return v;
    }
}
