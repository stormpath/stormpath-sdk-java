/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.challenge;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.challenge.Challenge} record in Stormpath.
 *
 * @see com.stormpath.sdk.factor.sms.SmsFactor#createChallenge(Challenge)
 * @since 1.1.0
 */
public interface CreateChallengeRequest<T extends Challenge> {

    /**
     * Returns the Challenge instance for which a new record will be created in Stormpath.
     *
     * @return the Challenge instance for which a new record will be created in Stormpath.
     */
    T getChallenge();

    /**
     * Returns true in case Challenge has options.
     *
     * @return true in case Challenge has options.
     */
    boolean hasChallengeOptions();

    /**
     * Returns the {@link ChallengeOptions}.
     *
     * @return  {@link ChallengeOptions}.
     */
    ChallengeOptions getChallengeOptions() throws IllegalStateException;
}
