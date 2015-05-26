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
package com.stormpath.spring.security.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.Assert;

/**
 * A Stormpath SDK {@link com.stormpath.sdk.cache.Cache} implementation that wraps a Spring {@link org.springframework.cache.Cache Cache} instance.
 * This allows the Stormpath SDK to use your existing Spring caching mechanism so you only need to configure one
 * caching implementation.
 * <p/>
 * This implementation effectively acts as an adapter or bridge from the Stormpath SDK cache API to the Spring cache API.
 *
 * @since 0.2.0
 */
public class SpringCache<K, V> implements Cache<K, V> {

    private final org.springframework.cache.Cache SPRING_CACHE;

    /**
     * Constructs a new {@code SpringCache} instance that wraps (delegates to) the specified
     * Spring {@link org.springframework.cache.Cache Cache} instance.
     *
     * @param springCache the target Spring cache to wrap.
     */
    public SpringCache(final org.springframework.cache.Cache springCache) {
        Assert.notNull(springCache, "Spring cache instance cannot be null.");
        this.SPRING_CACHE = springCache;
    }

    @Override
    public V get(K key) {
        org.springframework.cache.Cache.ValueWrapper valueWrapper = SPRING_CACHE.get(key);
        if(valueWrapper != null) {
            return (V) valueWrapper.get();
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        V previousValue = get(key);
        SPRING_CACHE.put(key, value);
        return previousValue;
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        SPRING_CACHE.evict(key);
        return value;
    }

}
