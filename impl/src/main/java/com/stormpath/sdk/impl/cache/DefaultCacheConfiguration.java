package com.stormpath.sdk.impl.cache;

import com.stormpath.sdk.impl.util.Duration;
import com.stormpath.sdk.lang.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @since 0.8
 */
public class DefaultCacheConfiguration implements CacheConfiguration {

    private final String name;
    private final Duration timeToLive;
    private final Duration timeToIdle;

    public DefaultCacheConfiguration(String name, Duration timeToLive, Duration timeToIdle) {
        Assert.hasText(name, "Cache Region name cannot be null or empty.");
        this.name = name;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
    }

    static Duration toDuration(long value, TimeUnit tu) {
        if (value > 0) {
            return new Duration(value, tu);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Duration getTimeToLive() {
        return this.timeToLive;
    }

    @Override
    public Duration getTimeToIdle() {
        return this.timeToIdle;
    }
}
