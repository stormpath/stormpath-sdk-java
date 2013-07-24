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
package com.stormpath.sdk.impl.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;

/**
 * A disabled implementation that does nothing.  This alleviates a CacheManager user (component) from ever needing to
 * check for null.  Non-null guarantees reduce a program's cyclomatic complexity and simplify testing.
 *
 * @since 0.8
 */
public class DisabledCacheManager implements CacheManager {

    private static final Cache CACHE_INSTANCE = new DisabledCache();

    /**
     * Always returns a {@link DisabledCache} instance to ensure non-null guarantees.
     *
     * @return returns a {@link DisabledCache} instance to ensure non-null guarantees.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return CACHE_INSTANCE;
    }
}
