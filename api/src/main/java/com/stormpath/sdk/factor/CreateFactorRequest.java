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
package com.stormpath.sdk.factor;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.factor.Factor} record in Stormpath.
 *
 * @see com.stormpath.sdk.account.Account#createFactor(CreateFactorRequest)
 * @since 1.1.0
 */
public interface CreateFactorRequest<T extends Factor, R extends FactorOptions>{

    /**
     * Returns the Factor instance for which a new record will be created in Stormpath.
     *
     * @return the Factor instance for which a new record will be created in Stormpath.
     */
    T getFactor();

    /**
     * Returns true in case Factor has options.
     *
     * @return true in case Factor has options.
     */
    boolean hasFactorOptions();

    /**
     * Returns true in case Factor should be challenged upon creation.
     * In which case a challenge resource is also created.
     *
     * @return rtue in case Factor should be challenged upon creation..
     */
    boolean isCreateChallenge();

    /**
     * Returns the {@link FactorOptions}.
     *
     * @return  {@link FactorOptions}.
     */
    R getFactorOptions() throws IllegalStateException;
}
