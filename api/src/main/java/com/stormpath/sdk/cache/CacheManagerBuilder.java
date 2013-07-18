package com.stormpath.sdk.cache;

import java.util.concurrent.TimeUnit;

/**
 * Builder for creating simple {@link CacheManager} instances <b>suitable for SINGLE-JVM APPLICATIONS</b>.  If your
 * application is deployed (mirrored or clustered) across multiple JVMs, you might not
 * want to use this builder and use your own clusterable CacheManager implementation instead.  See Clustering below.
 * <h2>Clustering</h2>
 * <b>The default CacheManager instances created by this Builder DO NOT SUPPORT CLUSTERING</b>.
 * <p/>
 * If you use this Builder and your application is deployed on multiple JVMs, <b>each of your application instances will
 * have <em>their own</em> local cache of Stormpath data</b>.  Depending on your application requirements, and your
 * cache TTL and TTI settings, this could introduce a significant difference in cached data seen by your application
 * instances, which would likely impact user management behavior.  For example, one application instance could see an
 * account as ENABLED, but the other application instance could see it as DISABLED.
 * <p/>
 * For some applications, this discrepancy might be an acceptable trade-off, especially if you configure
 * {@link #withDefaultTimeToIdle(long, java.util.concurrent.TimeUnit) timeToIdle} and
 * {@link #withDefaultTimeToLive(long, java.util.concurrent.TimeUnit) timeToLive} settings low enough.  For example,
 * maybe a TTL of 5 or 10 minutes is an acceptable time to see 'stale' account data.  For other applications, this might
 * not be acceptable.  If it is acceptable, configuring the timeToIdle and timeToLive settings will allow you to
 * fine-tune how much variance you allow.
 * <p/>
 * However, if you are concerned about this difference in data and you want the Stormpath SDK's cache to be coherent
 * across your application nodes (typically a good thing to have), it is strongly recommended that you do not use this
 * Builder and instead configure the Stormpath SDK with a clustered {@code CacheManager} implementation of your choosing.
 * This approach still gives you excellent performance improvements and ensures that your cached data is coherent (seen
 * as the same) across all of your application instances.
 * <p/>
 * This comes with an increased cost of course: setting up a caching product and/or cluster.  However, this is not
 * much of a problem in practice: most multi-instance applications already leverage caching clusters for their own
 * application needs. In these environments, and with a proper {@code CacheManager} implementation leveraging a
 * clustered cache, the Stormpath Java SDK will live quite happily using this same caching infrastructure.
 * <p/>
 * A coherent cache deployment ensures all of your application instances/nodes can utilize the same cache policy and
 * see the same cached security/identity data.  Some example clustered caching solutions: Hazelcast,
 * Ehcache+Terracotta, Memcache, Redis, Coherence, GigaSpaces, etc.
 *
 * @since 0.8
 */
public interface CacheManagerBuilder {

    /**
     * Sets the default Time to Live (TTL) for all cache regions managed by the {@link #build() built}
     * {@code CacheManager}. You may override this default for individual cache regions by using the
     * {@link #withCache(CacheConfigurationBuilder) withCache} for each region you wish to configure.
     * <p/>
     * Time to Live is the amount of time a cache entry may exist after first being created before it will expire and no
     * longer be available.  If a cache entry ever becomes older than this amount of time (regardless of how often
     * it is accessed), it will be removed from the cache as soon as possible.
     * <p/>
     * If this value is not configured, it is assumed that cache entries could potentially live indefinitely.
     * Note however that entries can still be expunged due to other conditions (e.g. memory constraints, Time to
     * Idle setting, etc).
     * <h3>Usage</h3>
     * <pre>
     *     ...withDefaultTimeToLive(30, TimeUnit.MINUTES)...
     *     ...withDefaultTimeToLive(1, TimeUnit.HOURS)...
     * </pre>
     *
     * @param ttl      default Time To Live scalar value
     * @param timeUnit default Time to Live unit of time
     * @return the builder instance for method chaining.
     */
    CacheManagerBuilder withDefaultTimeToLive(long ttl, TimeUnit timeUnit);

    /**
     * Sets the default Time to Idle (TTI) for all cache regions managed by the {@link #build() built}
     * {@code CacheManager}. You may override this default for individual cache regions by using the
     * {@link #withCache(CacheConfigurationBuilder) withCache} for each region you wish to configure.
     * <p/>
     * Time to Idle is the amount of time a cache entry may be idle (unused / not accessed) before it will expire and
     * no longer be available.  If a cache entry is not accessed at all after this amount of time, it will be removed
     * from the cache as soon as possible.
     * <p/>
     * If this value is not configured, it is assumed that cache entries could potentially live indefinitely.
     * Note however that entries can still be expunged due to other conditions (e.g. memory constraints, Time to
     * Live setting, etc).
     * <h3>Usage</h3>
     * <pre>
     *     ...withDefaultTimeToLive(30, TimeUnit.MINUTES)...
     *     ...withDefaultTimeToLive(1, TimeUnit.HOURS)...
     * </pre>
     *
     * @param tti      default Time To Idle scalar value
     * @param timeUnit default Time to Idle unit of time
     * @return the builder instance for method chaining.
     */
    CacheManagerBuilder withDefaultTimeToIdle(long tti, TimeUnit timeUnit);

    /**
     * Adds configuration settings for a specific Cache region managed by the {@link #build() built}
     * {@code CacheManager}, like the region's Time to Live and Time to Idle.
     *
     * @param builder the CacheConfigurationBuilder instance that will be used to a cache's configuration.
     * @return this instance for method chaining.
     */
    CacheManagerBuilder withCache(CacheConfigurationBuilder builder);

    /**
     * Returns a new {@link CacheManager} instance reflecting Builder's current configuration.
     *
     * @return a new {@link CacheManager} instance reflecting Builder's current configuration.
     */
    CacheManager build();
}
