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
package com.stormpath.sdk.impl.challenge;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.1.0
 */
public class DefaultCreateChallengeRequest implements CreateChallengeRequest {

    private final Challenge challenge;
    private final ChallengeOptions options;

    public DefaultCreateChallengeRequest(Challenge challenge, ChallengeOptions options) {
        Assert.notNull(challenge, "challenge cannot be null.");
        this.challenge = challenge;
        this.options = options;
    }

    @Override
    public Challenge getChallenge() {
        return this.challenge;
    }

    @Override
    public boolean hasChallengeOptions() {
        return this.options != null;
    }

    @Override
    public ChallengeOptions getChallengeOptions() throws IllegalStateException {
        return options;
    }

}
