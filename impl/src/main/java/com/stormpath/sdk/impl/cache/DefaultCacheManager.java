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

import com.stormpath.sdk.cache.*;
import com.stormpath.sdk.impl.util.*;
import com.stormpath.sdk.lang.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Very simple default {@code CacheManager} implementation that retains all created {@link Cache Cache} instances in
 * an in-memory {@link ConcurrentMap ConcurrentMap}.  By default, this implementation creates thread-safe
 * {@link DefaultCache} instances via the {@link #createCache(String) createCache(name)} method, but this can be overridden
 * by subclasses that wish to provide different Cache implementations.
 * <h2>Clustering</h2>
 * <b>This implementation DOES NOT SUPPORT CLUSTERING</b>.
 * <p/>
 * If your application is deployed on multiple hosts, it is
 * <em>strongly</em> recommended that you configure the Stormpath SDK with a clustered {@code CacheManager}
 * implementation so all of your application instances can utilize the same cache policy and see the same
 * security/identity data.  Some example clusterable caching projects: Hazelcast, Ehcache+Terracotta, Coherence,
 * GigaSpaces, etc.
 * <p/>
 * <b>This implementation is production-quality, but only recommended for single-node/single-JVM applications.</b>
 * <h2>Time To Idle</h2>
 * Time to Idle is the amount of time a cache entry may be idle - unused (not accessed) - before it will expire and
 * no longer be available.  If a cache entry is not accessed at all after this amount of time, it will be
 * removed from the cache as soon as possible.
 * <p/>
 * This implementation's {@link #setDefaultTimeToIdle(com.stormpath.sdk.lang.Duration) defaultTimeToIdle}
 * is {@code null}, which means that cache entries can potentially remain idle indefinitely.  Note however that a
 * cache entry can still be expunged due to other conditions (e.g. memory constraints, Time to Live setting, etc).
 * <p/>
 * The {@link #setDefaultTimeToIdle(com.stormpath.sdk.lang.Duration) defaultTimeToIdle} setting is only
 * applied to newly created {@code Cache} instances.  It does not affect already existing {@code Cache}s.
 * <h2>Time to Live</h2>
 * Time to Live is the amount of time a cache entry may exist after first being created before it will expire and no
 * longer be available.  If a cache entry ever becomes older than this amount of time (regardless of how often
 * it is accessed), it will be removed from the cache as soon as possible.
 * <p/>
 * This implementation's {@link #setDefaultTimeToLive(com.stormpath.sdk.lang.Duration) defaultTimeToLive}
 * is {@code null}, which means that cache entries could potentially live indefinitely.  Note however that a
 * cache entry can still be expunged due to other conditions (e.g. memory constraints, Time to Idle setting, etc).
 * <p/>
 * The {@link #setDefaultTimeToLive(com.stormpath.sdk.lang.Duration) defaultTimeToLive} setting is only
 * applied to newly created {@code Cache} instances.  It does not affect already existing {@code Cache}s.
 * <h2>Thread Safety</h2>
 * This implementation and the cache instances it creates are thread-safe and usable in concurrent environments.
 *
 * @see #setDefaultTimeToIdle(com.stormpath.sdk.lang.Duration)
 * @see #setDefaultTimeToIdleSeconds(long)
 * @see #setDefaultTimeToLive(com.stormpath.sdk.lang.Duration)
 * @see #setDefaultTimeToLiveSeconds(long)
 * @since 0.8
 */
public class DefaultCacheManager implements CacheManager {

    /**
     * Retains any region-specific configuration that might be used when creating Cache instances.
     */
    protected final ConcurrentMap<String, CacheConfiguration> configs;

    /**
     * Retains all Cache objects maintained by this cache manager.
     */
    protected final ConcurrentMap<String, Cache> caches;

    private volatile Duration defaultTimeToLive;
    private volatile Duration defaultTimeToIdle;

    /**
     * Default no-arg constructor that instantiates an internal name-to-cache {@code ConcurrentMap}.
     */
    public DefaultCacheManager() {
        this.configs = new ConcurrentHashMap<String, CacheConfiguration>();
        this.caches = new ConcurrentHashMap<String, Cache>();
    }

    /**
     * Returns the default {@link DefaultCache#getTimeToLive() timeToLive} duration
     * to apply to newly created {@link DefaultCache} instances.  This setting does not affect existing
     * {@link DefaultCache} instances.
     *
     * @return the default {@link DefaultCache#getTimeToLive() timeToLive} duration
     *         to apply to newly created {@link DefaultCache} instances.
     * @see DefaultCache
     * @see DefaultCache#getTimeToLive()
     */
    public Duration getDefaultTimeToLive() {
        return defaultTimeToLive;
    }

    /**
     * Sets the default {@link DefaultCache#getTimeToLive() timeToLive} duration
     * to apply to newly created {@link DefaultCache} instances.  This setting does not affect existing
     * {@link DefaultCache} instances.
     *
     * @param defaultTimeToLive the default {@link DefaultCache#getTimeToLive() timeToLive}
     *                          duration to apply to newly created {@link DefaultCache} instances.
     */
    public void setDefaultTimeToLive(Duration defaultTimeToLive) {
        DefaultCache.assertTtl(defaultTimeToLive);
        this.defaultTimeToLive = defaultTimeToLive;
    }

    /**
     * Convenience method that sets the {@link #setDefaultTimeToLive(com.stormpath.sdk.lang.Duration) defaultTimeToLive}
     * value using a {@code TimeUnit} of {@link TimeUnit#SECONDS}.
     *
     * @param seconds the {@link #setDefaultTimeToLive(com.stormpath.sdk.lang.Duration) defaultTimeToLive} value in seconds.
     */
    public void setDefaultTimeToLiveSeconds(long seconds) {
        setDefaultTimeToLive(new Duration(seconds, TimeUnit.SECONDS));
    }

    /**
     * Returns the default {@link DefaultCache#getTimeToIdle() timeToIdle} duration
     * to apply to newly created {@link DefaultCache} instances.  This setting does not affect existing
     * {@link DefaultCache} instances.
     *
     * @return the default {@link DefaultCache#getTimeToIdle() timeToIdle} duration
     *         to apply to newly created {@link DefaultCache} instances.
     */
    public Duration getDefaultTimeToIdle() {
        return defaultTimeToIdle;
    }

    /**
     * Sets the default {@link DefaultCache#getTimeToIdle() timeToIdle} duration
     * to apply to newly created {@link DefaultCache} instances.  This setting does not affect existing
     * {@link DefaultCache} instances.
     *
     * @param defaultTimeToIdle the default {@link DefaultCache#getTimeToIdle() timeToIdle}
     *                          duration to apply to newly created {@link DefaultCache} instances.
     */
    public void setDefaultTimeToIdle(Duration defaultTimeToIdle) {
        DefaultCache.assertTti(defaultTimeToIdle);
        this.defaultTimeToIdle = defaultTimeToIdle;
    }

    /**
     * Convenience method that sets the {@link #setDefaultTimeToIdle(com.stormpath.sdk.lang.Duration) defaultTimeToIdle}
     * value using a {@code TimeUnit} of {@link TimeUnit#SECONDS}.
     *
     * @param seconds the {@link #setDefaultTimeToIdle(com.stormpath.sdk.lang.Duration) defaultTimeToIdle} value in seconds.
     */
    public void setDefaultTimeToIdleSeconds(long seconds) {
        setDefaultTimeToIdle(new Duration(seconds, TimeUnit.SECONDS));
    }

    /**
     * Sets cache-specific configuration entries, to be utilized when creating cache instances.
     *
     * @param configs cache-specific configuration entries, to be utilized when creating cache instances.
     */
    public void setCacheConfigurations(Collection<CacheConfiguration> configs) {
        Assert.notNull("Argument cannot be null.  To remove all configuration, set an empty collection.");
        this.configs.clear();

        for (CacheConfiguration config : configs) {
            this.configs.put(config.getName(), config);
        }
    }

    /**
     * Returns the cache with the specified {@code name}.  If the cache instance does not yet exist, it will be lazily
     * created, retained for further access, and then returned.
     *
     * @param name the name of the cache to acquire.
     * @return the cache with the specified {@code name}.
     * @throws IllegalArgumentException if the {@code name} argument is {@code null} or does not contain text.
     */
    public <K, V> Cache<K, V> getCache(String name) throws IllegalArgumentException {
        Assert.hasText(name, "Cache name cannot be null or empty.");

        Cache cache;

        cache = caches.get(name);
        if (cache == null) {
            cache = createCache(name);
            Cache existing = caches.putIfAbsent(name, cache);
            if (existing != null) {
                cache = existing;
            }
        }

        //noinspection unchecked
        return cache;
    }

    /**
     * Creates a new {@code Cache} instance associated with the specified {@code name}.
     *
     * @param name the name of the cache to create
     * @return a new {@code Cache} instance associated with the specified {@code name}.
     */
    @SuppressWarnings("unchecked")
    protected Cache createCache(String name) {
        Duration ttl = this.defaultTimeToLive != null ? this.defaultTimeToLive.clone() : null;
        Duration tti = this.defaultTimeToIdle != null ? this.defaultTimeToIdle.clone() : null;

        CacheConfiguration config = this.configs.get(name);
        if (config != null) {
            Duration d = config.getTimeToLive();
            if (d != null) {
                ttl = d;
            }
            d = config.getTimeToIdle();
            if (d != null) {
                tti = d;
            }
        }

        return new DefaultCache(name, new SoftHashMap(), ttl, tti);
    }

    public String toString() {
        Collection<Cache> values = caches.values();
        StringBuilder sb = new StringBuilder()
                .append("{\n")
                .append("  \"cacheCount\": ").append(caches.size()).append(",\n")
                .append("  \"defaultTimeToLive\": \"").append(toString(defaultTimeToLive)).append("\",\n")
                .append("  \"defaultTimeToIdle\": \"").append(toString(defaultTimeToIdle)).append("\",\n")
                .append("  \"caches\": [");

        if (!caches.isEmpty()) {
            sb.append("\n");
            int i = 0;
            for (Cache cache : values) {
                if (i > 0) {
                    sb.append(",\n");
                }
                sb.append(cache.toString());
                i++;
            }
            sb.append("\n  ");
        }

        sb.append("]\n}");
        return sb.toString();
    }

    private String toString(Duration d) {
        return d != null ? d.toString() : "indefinite";
    }
}
