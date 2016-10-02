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
package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.factor.CreateFactorRequest;

/**
 * Represents an attempt to create a new {@link SmsFactor} record in Stormpath.
 *
 * @see com.stormpath.sdk.account.Account#createFactor(CreateFactorRequest)
 * @since 1.1.0
 */
public interface CreateSmsFactorRequest<T extends SmsFactor, O extends SmsFactorOptions> extends CreateFactorRequest<T,O>{
    /**
     * Returns true in case Factor should be challenged upon creation.
     * In which case a challenge resource is also created.
     *
     * @return rtue in case Factor should be challenged upon creation..
     */
    boolean isCreateChallenge();
}
