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
import com.stormpath.sdk.impl.ds.Filter;
import com.stormpath.sdk.impl.ds.ResourceDataRequest;
import com.stormpath.sdk.impl.http.CanonicalUri;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

abstract class AbstractCacheFilter implements Filter {

    private final CacheResolver cacheResolver;
    private final boolean collectionCachingEnabled; //DEVELOPMENT PURPOSES ONLY! DOES NOT WORK FOR PRODUCTION!

    protected AbstractCacheFilter(CacheResolver resolver, boolean collectionCachingEnabled) {
        Assert.notNull(resolver, "cacheResolver cannot be null.");
        this.cacheResolver = resolver;
        this.collectionCachingEnabled = collectionCachingEnabled;
    }

    protected boolean isCollectionCachingEnabled() {
        return collectionCachingEnabled;
    }

    protected Map<String, ?> getCachedValue(String href, Class<? extends Resource> clazz) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Class argument cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(clazz);
        return cache.get(href);
    }

    protected String getCacheKey(ResourceDataRequest request) {

        final CanonicalUri uri = request.getUri();
        final String href = uri.getAbsolutePath();
        final QueryString query = uri.getQuery();
        final Class<? extends Resource> clazz = request.getResourceClass();

        return getCacheKey(href, query, clazz);
    }

    protected String getCacheKey(String href, QueryString query, Class<? extends Resource> clazz) {

        String key = href;

        if (collectionCachingEnabled && CollectionResource.class.isAssignableFrom(clazz) && !Collections.isEmpty(query)) {
            key = href + "?" + query.toString();
        }

        return key;
    }

    protected <T> Cache<String, Map<String, ?>> getCache(Class<T> clazz) {
        return this.cacheResolver.getCache(clazz);
    }
}
