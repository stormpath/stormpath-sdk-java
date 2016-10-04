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

import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 1.1.0
 */
public class DefaultChallengeCriteria<T extends ChallengeCriteria, R extends ChallengeOptions>  extends DefaultCriteria<ChallengeCriteria, R> implements ChallengeCriteria {

    public DefaultChallengeCriteria(R options) {
        super(options);
    }

    @Override
    public ChallengeCriteria orderByStatus() {
        return orderBy(AbstractChallenge.STATUS);
    }

    @Override
    public ChallengeCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }

    @Override
    public ChallengeCriteria withFactor() {
        getOptions().withFactor();
        return this;
    }
}
