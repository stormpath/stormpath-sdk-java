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
package com.stormpath.sdk.hazelcast;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.Assert;

import java.util.concurrent.ConcurrentMap;

/**
 * A simple {@code Cache} implementation that merely wraps a {@link ConcurrentMap} obtained from a
 * {@link com.hazelcast.core.HazelcastInstance HazelcastInstance}.  The backing Hazelcast-based map is used for all
 * caching operations.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 */
public class HazelcastCache<K, V> implements Cache<K, V> {

    private final ConcurrentMap<K, V> HAZELCAST_MAP;

    /**
     * Creates a new instance, delegating all caching operations to the specified {@code hazelcastMap}.
     *
     * @param hazelcastMap the backing map instance that will be used to satisfy caching operations.
     */
    public HazelcastCache(ConcurrentMap<K, V> hazelcastMap) {
        Assert.notNull(hazelcastMap, "hazelcastMap argument cannot be null.");
        this.HAZELCAST_MAP = hazelcastMap;
    }

    @Override
    public V get(K key) {
        return HAZELCAST_MAP.get(key);
    }

    @Override
    public V put(K key, V value) {
        return HAZELCAST_MAP.put(key, value);
    }

    @Override
    public V remove(K key) {
        return HAZELCAST_MAP.remove(key);
    }
}
