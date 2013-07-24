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
package com.stormpath.sdk.cache;

/**
 * A Cache efficiently stores temporary objects primarily to improve an application's performance.
 * <p/>
 * This interface provides an abstraction (wrapper) API on top of an underlying
 * cache framework's cache instance (e.g. JCache, Ehcache, Hazelcast, JCS, OSCache, JBossCache, TerraCotta, Coherence,
 * GigaSpaces, etc, etc), allowing a Stormpath SDK user to configure any cache mechanism they choose.
 *
 * @since 0.8
 */
public interface Cache<K, V> {

    /**
     * Returns the cached value stored under the specified {@code key} or
     * {@code null} if there is no cache entry for that {@code key}.
     *
     * @param key the key that the value was previous added with
     * @return the cached object or {@code null} if there is no entry for the specified {@code key}
     */
    V get(K key);


    /**
     * Adds a cache entry.
     *
     * @param key   the key used to identify the object being stored.
     * @param value the value to be stored in the cache.
     * @return the previous value associated with the given {@code key} or {@code null} if there was no previous value
     */
    V put(K key, V value);


    /**
     * Removes the cached value stored under the specified {@code key}.
     *
     * @param key the key used to identify the object being stored.
     * @return the removed value or {@code null} if there was no value cached.
     */
    V remove(K key);
}
