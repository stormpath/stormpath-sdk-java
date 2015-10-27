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
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.lang.Assert;
import org.springframework.beans.factory.InitializingBean;

/**
 * A Stormpath SDK {@link com.stormpath.sdk.cache.CacheManager} implementation that wraps a Spring
 * {@link org.springframework.cache.CacheManager CacheManager} instance.  This allows the Stormpath SDK to use your
 * existing Spring caching mechanism so you only need to configure one caching implementation.
 * <p/>
 * This implementation effectively acts as an adapter or bridge from the Stormpath SDK cacheManager API to the Spring
 * CacheManager API.
 *
 * @since 1.0.RC4
 */
public class SpringCacheManager implements CacheManager, InitializingBean {

    private org.springframework.cache.CacheManager springCacheManager;

    public SpringCacheManager(){}

    /**
     * Constructs a new {@code SpringCacheManager} instance that wraps (delegates to) the specified
     * Spring {@link org.springframework.cache.CacheManager CacheManager} instance.
     *
     * @param springCacheManager the target Spring cache manager to wrap.
     */
    public SpringCacheManager(org.springframework.cache.CacheManager springCacheManager) {
        Assert.notNull(springCacheManager, "CacheManager argument cannot be null.");
        this.springCacheManager = springCacheManager;
    }

    public void setSpringCacheManager(org.springframework.cache.CacheManager cacheManager) {
        this.springCacheManager = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(springCacheManager, "springCacheManager instance must be specified.");
    }

    /**
     * Consults the wrapped Spring {@link org.springframework.cache.CacheManager CacheManager} instance to obtain a
     * named Spring {@link org.springframework.cache.Cache Cache} instance.  The instance is wrapped and returned as a
     * {@link SpringCache} instance, which acts as a bridge/adapter over Spring's existing Cache API.
     *
     * @param name the name of the cache to acquire.
     * @param <K>  The cache key type
     * @param <V>  The cache value type
     * @return the Cache with the given name
     */
    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        org.springframework.cache.Cache springCache = this.springCacheManager.getCache(name);
        return new SpringCache<K,V>(springCache);
    }
}
