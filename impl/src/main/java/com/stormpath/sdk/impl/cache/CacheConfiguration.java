package com.stormpath.sdk.impl.cache;

import com.stormpath.sdk.impl.util.Duration;

/**
 * Represents configuration settings for a particular {@link com.stormpath.sdk.cache.Cache Cache} region.
 *
 * @since 0.8
 */
public interface CacheConfiguration {

    /**
     * Returns the name of the {@code Cache} for which this configuration applies.
     *
     * @return the name of the {@code Cache} for which this configuration applies.
     */
    String getName();

    /**
     * Returns the Time-to-Live setting to apply for all entries in the associated {@code Cache}.
     *
     * @return the Time-to-Live setting to apply for all entries in the associated {@code Cache}.
     */
    Duration getTimeToLive();

    /**
     * Returns the Time-to-Idle setting to apply for all entries in the associated {@code Cache}.
     *
     * @return the Time-to-Idle setting to apply for all entries in the associated {@code Cache}.
     */
    Duration getTimeToIdle();
}
