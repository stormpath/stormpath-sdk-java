package com.stormpath.sdk.impl.cache.impl;

import com.stormpath.sdk.impl.cache.Cache;
import com.stormpath.sdk.impl.util.Assert;
import com.stormpath.sdk.impl.util.Duration;
import com.stormpath.sdk.impl.util.SoftHashMap;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A <code>DefaultCache</code> is a {@link Cache Cache} implementation that uses a backing {@link Map} instance to store
 * and retrieve cached data.
 * <p/>
 * This implementation is thread-safe <em>only</em> if the backing map is thread-safe.
 *
 * @since 1.0
 */
public class DefaultCache<K, V> implements Cache<K, V> {

    /**
     * Backing instance.
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

    public DefaultCache(String name) {
        this(name, new SoftHashMap<K, Entry<V>>());
    }

    public DefaultCache(String name, Map<K, Entry<V>> backingMap) {
        this(name, backingMap, null, null);
    }

    public DefaultCache(String name, Map<K, Entry<V>> backingMap, Duration timeToLive, Duration timeToIdle) {
        Assert.notNull(name, "Cache name cannot be null.");
        Assert.notNull(backingMap, "Backing map cannot be null.");
        this.name = name;
        this.map = backingMap;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
    }

    public V get(K key) {
        Entry<V> entry = map.get(key);

        if (entry == null) {
            return null;
        }

        long nowMillis = System.currentTimeMillis();

        Duration ttl = this.timeToLive;
        Duration tti = this.timeToIdle;

        if (ttl != null) {
            Duration sinceCreation = new Duration(nowMillis - entry.getCreationTimeMillis(), TimeUnit.MILLISECONDS);
            if (sinceCreation.isGreaterThan(ttl)) {
                map.remove(key);
                return null;
            }
        }

        if (tti != null) {
            Duration sinceLastAccess = new Duration(nowMillis - entry.getLastAccessTimeMillis(), TimeUnit.MILLISECONDS);
            if (sinceLastAccess.isGreaterThan(tti)) {
                map.remove(key);
                return null;
            }
        }

        entry.lastAccessTimeMillis = nowMillis;

        return entry.getValue();
    }

    public V put(K key, V value) {
        Entry<V> newEntry = new Entry<V>(value);
        Entry<V> previous = map.put(key,newEntry);
        if (previous != null) {
            return previous.value;
        }
        return null;
    }

    public Duration getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Duration getTimeToIdle() {
        return timeToIdle;
    }

    public void setTimeToIdle(Duration timeToIdle) {
        this.timeToIdle = timeToIdle;
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public String toString() {
        return new StringBuilder("DefaultCache '")
                .append(name).append("' (")
                .append(map.size())
                .append(" entries)")
                .toString();
    }

    public static class Entry<V> implements Serializable {

        private final V value;
        private final long creationTimeMillis;
        private volatile long lastAccessTimeMillis;

        public Entry(V value) {
            this.value = value;
            this.creationTimeMillis = System.currentTimeMillis();
            this.lastAccessTimeMillis = this.creationTimeMillis;
        }

        public V getValue() {
            return value;
        }

        public long getCreationTimeMillis() {
            return creationTimeMillis;
        }

        public long getLastAccessTimeMillis() {
            return lastAccessTimeMillis;
        }
    }
}
