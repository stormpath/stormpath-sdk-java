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
