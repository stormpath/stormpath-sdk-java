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
