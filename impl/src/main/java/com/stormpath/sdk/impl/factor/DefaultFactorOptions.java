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

import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 1.1.0
 */
public class DefaultFactorOptions<T extends FactorOptions> extends DefaultOptions<T> implements FactorOptions<T>{
    @Override
    public T withAccount() {
        return expand(AbstractFactor.ACCOUNT);
    }

    @Override
    public T withChallenges() {
        return expand(AbstractFactor.CHALLENGES);
    }

    @Override
    public T withMostRecentChallenge() {
        return expand(AbstractFactor.MOST_RECENT_CHALLENGE);
    }
}
