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
 * @param <O> The specific {@link FactorOptions} class
 * @since 1.1.0
 */
public abstract class AbstractCreateFactorRequest<T extends Factor, O extends FactorOptions> implements CreateFactorRequest<T,O> {

    protected final T factor;
    protected final O options;

    public AbstractCreateFactorRequest(T factor, O options) {
        Assert.notNull(factor, "factor cannot be null.");
        this.factor = factor;
        this.options = options;
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
    public O getFactorOptions() throws IllegalStateException {
        if(this.options == null){
            throw new IllegalStateException("FactorOptions has not been configured.");
        }
        return this.options;
    }
}
