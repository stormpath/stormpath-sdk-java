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

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.lang.Assert;

/**
 * @param <T> The specific {@link Factor} class
 * @param <R> The specific {@link FactorOptions} class
 * @since 1.1.0
 */
public class DefaultCreateFactorRequest<T extends Factor, R extends FactorOptions> implements CreateFactorRequest {
    private final T factor;
    private final R options;
    private final boolean createChallenge;

    public DefaultCreateFactorRequest(T factor, R options, boolean createChallenge) {
        Assert.notNull(factor, "factor cannot be null.");
        this.factor = factor;
        this.options = options;
        this.createChallenge = createChallenge;
    }

    @Override
    public T getFactor() {
        return this.factor;
    }

    @Override
    public boolean hasFactorOptions() {
        return this.options != null;
    }

    @Override
    public boolean isCreateChallenge() {
        return createChallenge;
    }

    @Override
    public R getFactorOptions() throws IllegalStateException {
        if(this.options == null){
            throw new IllegalStateException("SmsFactorOptions has not been configured. Use the hasPhoneOptions method to check first before invoking this method.");
        }
        return this.options;
    }
}
