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

import java.util.concurrent.TimeUnit;

/**
 * A Builder to specify configuration for {@link Cache} regions.  This is usually used while building a CacheManager via
 * the {@link CacheManagerBuilder}.  CacheConfigurationBuilders can be constructed with the {@link Caches Caches}
 * utility class.  For example:
 * <pre>
 * Caches.named("cacheRegionNameHere")
 *     .{@link #withTimeToLive(long, java.util.concurrent.TimeUnit) withTimeToLive(1, TimeUnit.DAYS)}
 *     .{@link #withTimeToIdle(long, java.util.concurrent.TimeUnit) withTimeToIdle(2, TimeUnit.HOURS)};
 * </pre>
 * or
 * <pre>
 * Caches.forResource(Account.class)
 *     .{@link #withTimeToLive(long, java.util.concurrent.TimeUnit) withTimeToLive(1, TimeUnit.DAYS)}
 *     .{@link #withTimeToIdle(long, java.util.concurrent.TimeUnit) withTimeToIdle(2, TimeUnit.HOURS)};
 * </pre>
 *
 * @see #withTimeToLive(long, java.util.concurrent.TimeUnit)
 * @see #withTimeToIdle(long, java.util.concurrent.TimeUnit)
 * @see Caches#forResource(Class)
 * @see Caches#named(String)
 * @since 0.8
 */
public interface CacheConfigurationBuilder {

    /**
     * Sets the associated {@code Cache} region's entry Time to Live (TTL).
     * <p/>
     * Time to Live is the amount of time a cache entry may exist after first being created before it will expire and no
     * longer be available.  If a cache entry ever becomes older than this amount of time (regardless of how often
     * it is accessed), it will be removed from the cache as soon as possible.
     * <p/>
     * If this value is not configured, it is assumed that the Cache's entries could potentially live indefinitely.
     * Note however that entries can still be expunged due to other conditions (e.g. memory constraints, Time to
     * Idle setting, etc).
     * <h3>Usage</h3>
     * <pre>
     *     ...withTimeToLive(30, TimeUnit.MINUTES)...
     *     ...withTimeToLive(1, TimeUnit.HOURS)...
     * </pre>
     *
     * @param ttl         Time To Live scalar value
     * @param ttlTimeUnit Time to Live unit of time
     * @return the builder instance for method chaining.
     */
    CacheConfigurationBuilder withTimeToLive(long ttl, TimeUnit ttlTimeUnit);

    /**
     * Sets the associated {@code Cache} region's entry Time to Idle (TTI).
     * <p/>
     * Time to Idle is the amount of time a cache entry may be idle (unused / not accessed) before it will expire and
     * no longer be available.  If a cache entry is not accessed at all after this amount of time, it will be removed
     * from the cache as soon as possible.
     * <p/>
     * If this value is not configured, it is assumed that the Cache's entries could potentially live indefinitely.
     * Note however that entries can still be expunged due to other conditions (e.g. memory constraints, Time to
     * Live setting, etc).
     * <h3>Usage</h3>
     * <pre>
     *     ...withTimeToIdle(30, TimeUnit.MINUTES)...
     *     ...withTimeToIdle(1, TimeUnit.HOURS)...
     * </pre>
     *
     * @param tti         Time To Idle scalar value
     * @param ttiTimeUnit Time to Idle unit of time
     * @return the builder instance for method chaining.
     */
    CacheConfigurationBuilder withTimeToIdle(long tti, TimeUnit ttiTimeUnit);

}
