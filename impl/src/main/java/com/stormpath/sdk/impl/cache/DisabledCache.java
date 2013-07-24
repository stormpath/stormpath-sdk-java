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

/**
 * A disabled implementation that does nothing.  This is useful for a CacheManager implementation to return instead
 * of retuning null.  Non-null guarantees reduce a program's cyclomatic complexity.
 *
 * @since 0.8
 */
public class DisabledCache<K, V> implements Cache<K, V> {

    /**
     * This implementation does not do anything and always returns null.
     *
     * @return null always.
     */
    @Override
    public V get(K key) {
        return null;
    }

    /**
     * This implementation does not do anything (no caching) and always returns null.
     *
     * @param key   the key used to identify the object being stored.
     * @param value the value to be stored in the cache.
     * @return null always.
     */
    @Override
    public V put(K key, V value) {
        return null;
    }

    /**
     * This implementation does not do anything (no caching) and always returns null.
     *
     * @param key the key used to identify the object being stored.
     * @return null always.
     */
    @Override
    public V remove(K key) {
        return null;
    }
}
