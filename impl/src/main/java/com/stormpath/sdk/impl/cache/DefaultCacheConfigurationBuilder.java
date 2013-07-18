package com.stormpath.sdk.impl.cache;

import com.stormpath.sdk.cache.CacheConfigurationBuilder;
import com.stormpath.sdk.impl.util.Duration;
import com.stormpath.sdk.lang.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.8
 */
public class DefaultCacheConfigurationBuilder implements CacheConfigurationBuilder {

    private final String name;
    private Duration timeToLive;
    private Duration timeToIdle;

    public DefaultCacheConfigurationBuilder(String name) {
        Assert.hasText(name, "Cache Region name cannot be null or empty.");
        this.name = name;
    }

    @Override
    public CacheConfigurationBuilder withTimeToLive(long ttl, TimeUnit ttlTimeUnit) {
        this.timeToLive = DefaultCacheConfiguration.toDuration(ttl, ttlTimeUnit);
        return this;
    }

    @Override
    public CacheConfigurationBuilder withTimeToIdle(long tti, TimeUnit ttiTimeUnit) {
        this.timeToIdle = DefaultCacheConfiguration.toDuration(tti, ttiTimeUnit);
        return this;
    }

    public String getName() {
        return name;
    }

    public Duration getTimeToLive() {
        return timeToLive;
    }

    public Duration getTimeToIdle() {
        return timeToIdle;
    }

    public CacheConfiguration build() {
        return new DefaultCacheConfiguration(getName(), getTimeToLive(), getTimeToIdle());
    }
}
