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
