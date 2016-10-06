/*
* Copyright 2016 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.challenge.sms.SmsChallenge;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.phone.Phone;

/**
 * An {@code SmsFactor} is a Factor that represents a phone used in SMS-based challenge workflows.
 * When issuing a challenge via an SmsFactor, a code is sent via SMS to the phone, and the user
 * can enter the received code back into the system to verify/complete the challenge.
 *
 * @since 1.1.0
 */
public interface SmsFactor<T extends SmsChallenge> extends Factor<T> {

    /**
     * Returns the {@link Phone} resource associated with this {@code SmsFactor}.
     *
     * @return the {@link Phone} resource associated with this {@code SmsFactor}.
     */
    Phone getPhone();

    /**
     * Sets the {@link Phone} resource to be associated with this {@code SmsFactor}.
     * @param phone {@link Phone} resource to be associated with this {@code SmsFactor}
     *
     * @return this instance for method chaining.
     */
    SmsFactor setPhone(Phone phone);

    /**
     * Challenges this {@code SmsFactor}.
     *
     * @return this instance for method chaining.
     */
    SmsFactor challenge();
}
