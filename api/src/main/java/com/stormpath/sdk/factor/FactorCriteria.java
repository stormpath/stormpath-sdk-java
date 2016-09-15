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

import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.phone.PhoneOptions;
import com.stormpath.sdk.phone.Phones;
import com.stormpath.sdk.query.Criteria;

// todo: mehrshad

public interface FactorCriteria extends Criteria<FactorCriteria>,  FactorOptions<FactorCriteria>{


    /**
     * Ensures that the query results are ordered by group {@link Phone#getStatus() status}.
     * <p/>
     * Please see the {@link FactorCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    FactorCriteria orderByType();

    /**
     * Ensures that the query results are ordered by group {@link Phone#getStatus() status}.
     * <p/>
     * Please see the {@link FactorCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    FactorCriteria orderByStatus();

    /**
     * Ensures that the query results are ordered by group {@link Phone#getVerificationStatus()} verificationStatus}.
     * <p/>
     * Please see the {@link FactorCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    FactorCriteria orderByVerificationStatus();



}
