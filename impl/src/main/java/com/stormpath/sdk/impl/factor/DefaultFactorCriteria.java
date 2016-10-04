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
package com.stormpath.sdk.impl.factor;

import com.stormpath.sdk.factor.FactorCriteria;
import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 1.1.0
 */
public class DefaultFactorCriteria<T extends FactorCriteria, R extends FactorOptions>  extends DefaultCriteria<FactorCriteria, R> implements FactorCriteria {

    public DefaultFactorCriteria(R options) {
        super(options);
    }

    @Override
    public FactorCriteria orderByStatus() {
        return orderBy(AbstractFactor.STATUS);
    }

    @Override
    public FactorCriteria orderByVerificationStatus() {
        return orderBy(AbstractFactor.VERIFICATION_STATUS);
    }

    @Override
    public FactorCriteria orderByCreatedAt() {
        return orderBy(AbstractFactor.CREATED_AT);
    }

    @Override
    public FactorCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }

    @Override
    public FactorCriteria withChallenges() {
        getOptions().withChallenges();
        return this;
    }

    @Override
    public FactorCriteria withMostRecentChallenge() {
        getOptions().withMostRecentChallenge();
        return this;
    }
}
