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
import com.stormpath.sdk.challenge.CreateChallengeRequestBuilder;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.1.0
 */
public class DefaultCreateChallengeRequestBuilder implements CreateChallengeRequestBuilder {

    private final Challenge challenge;
    private ChallengeOptions options;

    public DefaultCreateChallengeRequestBuilder(Challenge challenge) {
        Assert.notNull(challenge, "Challenge can't be null.");
        this.challenge = challenge;
    }

    @Override
    public CreateChallengeRequestBuilder withResponseOptions(ChallengeOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateChallengeRequest build() {
        return new DefaultCreateChallengeRequest(challenge, options);
    }
}
