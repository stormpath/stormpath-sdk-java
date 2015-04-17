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
 * An implementation of the Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager} interface that delegates
 * to a Spring {@link org.springframework.cache.CacheManager org.springframework.cache.CacheManager} instance.
 *
 * @since 1.0.RC4
 */
public class SpringCacheManager implements CacheManager, InitializingBean {

    private org.springframework.cache.CacheManager springCacheManager;

    public SpringCacheManager(){}

    public SpringCacheManager(org.springframework.cache.CacheManager springCacheManager) {
        this.springCacheManager = springCacheManager;
    }

    public void setSpringCacheManager(org.springframework.cache.CacheManager cacheManager) {
        this.springCacheManager = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(springCacheManager, "springCacheManager instance must be specified.");
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        org.springframework.cache.Cache springCache = this.springCacheManager.getCache(name);
        return new SpringCache<K,V>(springCache);
    }
}
