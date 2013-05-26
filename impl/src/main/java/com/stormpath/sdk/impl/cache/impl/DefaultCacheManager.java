package com.stormpath.sdk.impl.cache.impl;

import com.stormpath.sdk.impl.cache.Cache;
import com.stormpath.sdk.impl.cache.CacheManager;
import com.stormpath.sdk.impl.util.Assert;
import com.stormpath.sdk.impl.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Very simple abstract {@code CacheManager} implementation that retains all created {@link Cache Cache} instances in
 * an in-memory {@link ConcurrentMap ConcurrentMap}.  {@code Cache} instance creation is left to subclasses via
 * the {@link #createCache createCache} method implementation.
 *
 * @since 0.8
 */
public abstract class DefaultCacheManager implements CacheManager {

    /**
     * Retains all Cache objects maintained by this cache manager.
     */
    private final ConcurrentMap<String, Cache> caches;

    /**
     * Default no-arg constructor that instantiates an internal name-to-cache {@code ConcurrentMap}.
     */
    public DefaultCacheManager() {
        this.caches = new ConcurrentHashMap<String, Cache>();
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
        Assert.isTrue(StringUtils.hasText(name), "Cache name cannot be null or empty.");

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
     * <p/>
     *
     * @param name the name of the cache to create
     * @return a new {@code Cache} instance associated with the specified {@code name}.
     */
    protected abstract Cache createCache(String name);

    public String toString() {
        Collection<Cache> values = caches.values();
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append(" with ")
                .append(caches.size())
                .append(" cache(s)): [");
        int i = 0;
        for (Cache cache : values) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(cache.toString());
            i++;
        }
        sb.append("]");
        return sb.toString();
    }
}
