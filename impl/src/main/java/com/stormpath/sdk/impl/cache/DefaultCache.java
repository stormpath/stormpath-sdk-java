/*
 * Copyright 2013 Stormpath, Inc. and original authors.
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
import com.stormpath.sdk.impl.util.Duration;
import com.stormpath.sdk.impl.util.SoftHashMap;
import com.stormpath.sdk.lang.Assert;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A <code>DefaultCache</code> is a {@link Cache Cache} implementation that uses a backing {@link Map} instance to store
 * and retrieve cached data.
 * <h2>Thread Safety</h2>
 * This implementation is thread-safe <em>only</em> if the backing map is thread-safe.
 *
 * @since 1.0
 */
public class DefaultCache<K, V> implements Cache<K, V> {

    /**
     * Backing map instance that stores the cache entries.
     */
    private final Map<K, Entry<V>> map;

    /**
     * The amount of time allowed to pass since an entry was first created.  An entry older than this time,
     * <b>regardless of how often it might be used</b>, will be removed from the cache as soon as possible.
     */
    private volatile Duration timeToLive;

    /**
     * The amount of time allowed to pass since an entry was last used (inserted or accessed).  An entry that has not
     * been used in this amount of time will be removed from the cache as soon as possible.
     */
    private volatile Duration timeToIdle;

    /**
     * The name of this cache.
     */
    private final String name;

    private final AtomicLong accessCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    /**
     * Creates a new {@code DefaultCache} instance with the specified {@code name}, expected to be unique among all
     * other caches in the parent {@code CacheManager}.
     * <p/>
     * This constructor uses a {@link SoftHashMap} instance as the cache's backing map, which is thread-safe and
     * auto-sizes itself based on the application's memory constraints.
     * <p/>
     * Finally, the {@link #setTimeToIdle(com.stormpath.sdk.impl.util.Duration) timeToIdle} and
     * {@link #setTimeToLive(com.stormpath.sdk.impl.util.Duration) timeToLive} settings are both {@code null},
     * indicating that cache entries will live indefinitely (except due to memory constraints as managed by the
     * {@code SoftHashMap}).
     *
     * @param name the name to assign to this instance, expected to be unique among all other caches in the parent
     *             {@code CacheManager}.
     * @see SoftHashMap
     * @see #setTimeToIdle(com.stormpath.sdk.impl.util.Duration)
     * @see #setTimeToLive(com.stormpath.sdk.impl.util.Duration)
     */
    public DefaultCache(String name) {
        this(name, new SoftHashMap<K, Entry<V>>());
    }

    /**
     * Creates a new {@code DefaultCache} instance with the specified {@code name}, storing entries in the specified
     * {@code backingMap}.  It is expected that the {@code backingMap} implementation be thread-safe and preferrably
     * auto-sizing based on memory constraints (see {@link SoftHashMap} for such an implementation).
     * <p/>
     * The {@link #setTimeToIdle(com.stormpath.sdk.impl.util.Duration) timeToIdle} and
     * {@link #setTimeToLive(com.stormpath.sdk.impl.util.Duration) timeToLive} settings are both {@code null},
     * indicating that cache entries will live indefinitely (except due to memory constraints as managed by the
     * {@code backingMap} instance).
     *
     * @param name       name to assign to this instance, expected to be unique among all other caches in the parent
     *                   {@code CacheManager}.
     * @param backingMap the (ideally thread-safe) map instance to store the Cache entries.
     * @see SoftHashMap
     * @see #setTimeToIdle(com.stormpath.sdk.impl.util.Duration)
     * @see #setTimeToLive(com.stormpath.sdk.impl.util.Duration)
     */
    public DefaultCache(String name, Map<K, Entry<V>> backingMap) {
        this(name, backingMap, null, null);
    }

    /**
     * Creates a new {@code DefaultCache} instance with the specified {@code name}, storing entries in the specified
     * {@code backingMap}, using the specified {@code timeToLive} and {@code timeToIdle} settings.
     * <p/>
     * It is expected that the {@code backingMap} implementation be thread-safe and preferrably
     * auto-sizing based on memory constraints (see {@link SoftHashMap} for such an implementation).
     *
     * @param name       name to assign to this instance, expected to be unique among all other caches in the parent
     *                   {@code CacheManager}.
     * @param backingMap the (ideally thread-safe) map instance to store the Cache entries.
     * @param timeToIdle the amount of time cache entries may remain idle until they should be removed from the cache.
     * @param timeToLive the amount of time cache entries may exist until they should be removed from the cache.
     * @throws IllegalArgumentException if either {@code timeToLive} or {@code timeToIdle} are non-null <em>and</em>
     *                                  represent a non-positive (zero or negative) value. This is only enforced for
     *                                  non-null values - {@code null} values are allowed for either argument.
     * @see #setTimeToIdle(com.stormpath.sdk.impl.util.Duration)
     * @see #setTimeToLive(com.stormpath.sdk.impl.util.Duration)
     */
    public DefaultCache(String name, Map<K, Entry<V>> backingMap, Duration timeToLive, Duration timeToIdle) throws IllegalArgumentException {
        Assert.notNull(name, "Cache name cannot be null.");
        Assert.notNull(backingMap, "Backing map cannot be null.");
        assertTtl(timeToLive);
        assertTti(timeToIdle);
        this.name = name;
        this.map = backingMap;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
        this.accessCount = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
    }

    protected static void assertTtl(Duration ttl) throws IllegalArgumentException {
        if (ttl != null) {
            Assert.isTrue(ttl.getValue() > 0, "timeToLive duration must be greater than zero");
        }
    }

    protected static void assertTti(Duration tti) throws IllegalArgumentException {
        if (tti != null) {
            Assert.isTrue(tti.getValue() > 0, "timeToIdle duration must be greater than zero");
        }
    }

    public V get(K key) {

        this.accessCount.incrementAndGet();

        Entry<V> entry = map.get(key);

        if (entry == null) {
            missCount.incrementAndGet();
            return null;
        }

        long nowMillis = System.currentTimeMillis();

        Duration ttl = this.timeToLive;
        Duration tti = this.timeToIdle;

        if (ttl != null) {
            Duration sinceCreation = new Duration(nowMillis - entry.getCreationTimeMillis(), TimeUnit.MILLISECONDS);
            if (sinceCreation.isGreaterThan(ttl)) {
                map.remove(key);
                missCount.incrementAndGet(); //count an expired TTL as a miss
                return null;
            }
        }

        if (tti != null) {
            Duration sinceLastAccess = new Duration(nowMillis - entry.getLastAccessTimeMillis(), TimeUnit.MILLISECONDS);
            if (sinceLastAccess.isGreaterThan(tti)) {
                map.remove(key);
                missCount.incrementAndGet(); //count an expired TTI as a miss
                return null;
            }
        }

        entry.lastAccessTimeMillis = nowMillis;

        hitCount.incrementAndGet();

        return entry.getValue();
    }

    public V put(K key, V value) {
        Entry<V> newEntry = new Entry<V>(value);
        Entry<V> previous = map.put(key, newEntry);
        if (previous != null) {
            return previous.value;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        accessCount.incrementAndGet();
        Entry<V> previous = map.remove(key);
        if (previous != null) {
            hitCount.incrementAndGet();
            return previous.value;
        } else {
            missCount.incrementAndGet();
            return null;
        }
    }

    /**
     * Returns the amount of time a cache entry may exist after first being created before it will expire and no
     * longer be available.  If a cache entry ever becomes older than this amount of time (regardless of how often
     * it is accessed), it will be removed from the cache as soon as possible.
     *
     * @return the amount of time a cache entry may exist after first being created before it will expire and no
     *         longer be available.
     */
    public Duration getTimeToLive() {
        return timeToLive;
    }

    /**
     * Sets the amount of time a cache entry may exist after first being created before it will expire and no
     * longer be available.  If a cache entry ever becomes older than this amount of time (regardless of how often
     * it is accessed), it will be removed from the cache as soon as possible.
     *
     * @param timeToLive the amount of time a cache entry may exist after first being created before it will expire and
     *                   no longer be available.
     */
    public void setTimeToLive(Duration timeToLive) {
        assertTtl(timeToLive);
        this.timeToLive = timeToLive;
    }

    /**
     * Returns the amount of time a cache entry may be idle - unused (not accessed) - before it will expire and
     * no longer be available.  If a cache entry is not accessed at all after this amount of time, it will be
     * removed from the cache as soon as possible.
     *
     * @return the amount of time a cache entry may be idle - unused (not accessed) - before it will expire and
     *         no longer be available.
     */
    public Duration getTimeToIdle() {
        return timeToIdle;
    }

    /**
     * Sets the amount of time a cache entry may be idle - unused (not accessed) - before it will expire and
     * no longer be available.  If a cache entry is not accessed at all after this amount of time, it will be
     * removed from the cache as soon as possible.
     *
     * @param timeToIdle the amount of time a cache entry may be idle - unused (not accessed) - before it will expire
     *                   and no longer be available.
     */
    public void setTimeToIdle(Duration timeToIdle) {
        assertTti(timeToIdle);
        this.timeToIdle = timeToIdle;
    }

    public long getHitCount() {
        return hitCount.get();
    }

    public long getMissCount() {
        return missCount.get();
    }

    public double getHitRatio() {
        return hitCount.get() / accessCount.get();
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    /**
     * Returns this cache instance's name.
     *
     * @return this cache instance's name.
     */
    public String getName() {
        return this.name;
    }

    public String toString() {
        return new StringBuilder("DefaultCache '")
                .append(name).append("' (")
                .append(map.size())
                .append(" entries)")
                .toString();
    }

    /**
     * An Entry is a wrapper that encapsulates the actual {@code value} stored in the cache as well as
     * {@link #getCreationTimeMillis() creationTimeMillis} and {@link #getLastAccessTimeMillis() lastAccessTimeMillis}
     * metadata about the entry itself.  The {@code creationTimeMillis} and {@code lastAccessTimeMillis} values are used
     * to support expunging cache entries based on
     * {@link DefaultCache#getTimeToIdle() timeToIdle} and
     * {@link DefaultCache#getTimeToLive() timeToLive} settings, respectively.
     *
     * @param <V> the type of value that is stored in the cache.
     */
    public static class Entry<V> implements Serializable {

        private final V value;
        private final long creationTimeMillis;
        private volatile long lastAccessTimeMillis;

        /**
         * Creates a new Entry instance wrapping the specified {@code value}, defaulting both the
         * {@link #getCreationTimeMillis() creationTimeMillis} and the {@link #getLastAccessTimeMillis() lastAccessTimeMills}
         * to the current timestamp (i.e. {@link System#currentTimeMillis()}).
         *
         * @param value the cache entry to store.
         */
        public Entry(V value) {
            this.value = value;
            this.creationTimeMillis = System.currentTimeMillis();
            this.lastAccessTimeMillis = this.creationTimeMillis;
        }

        /**
         * Returns the actual value stored in the cache.
         *
         * @return the actual value stored in the cache.
         */
        public V getValue() {
            return value;
        }

        /**
         * Returns the creation time in millis since Epoch when this {@code Entry} instance was created.  This is used to
         * support expunging cache entries when this value is older than the cache's
         * {@link DefaultCache#getTimeToLive() timeToLive} setting.
         *
         * @return the creation time in millis since Epoch when this {@code Entry} instance was created.
         */
        public long getCreationTimeMillis() {
            return creationTimeMillis;
        }

        /**
         * Returns the time in millis since Epoch when this {@code Entry} instance was last accessed.  This is used to
         * support expunging cache entries when this value is older than the cache's
         * {@link DefaultCache#getTimeToIdle() timeToIdle} setting.
         *
         * @return the time in millis since Epoch when this {@code Entry} instance was last accessed.
         */
        public long getLastAccessTimeMillis() {
            return lastAccessTimeMillis;
        }
    }
}
