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
package com.stormpath.sdk.challenge.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.sms.SmsFactor;

/**
 * This domain object represents a challenge of a {@link SmsFactor} for a Multi Factor Authentication.
 * <p/>
 * In a Multi Factor Authentication scenario authenticating a user is challenged by additional {@link Factor}s like an {@link SmsFactor}.
 *
 * For Example: Using an {@link SmsFactor} as an additional {@link Factor} for authentication the user would receive an sms including a multi-digit code within its message.
 * The user would verify the authentication challenge by entering the sms code back to the system.
 *
 * @since 1.1.0
 */
public interface SmsChallenge extends Challenge<SmsFactor, SmsChallengeStatus>{
    /**
     * Returns the message associated with this challenge.
     * The message contains a code sent to the user to be sent back
     * for authentication.
     *
     * @return message associated with this challenge
     */
    String getMessage();

    /**
     * Sets the message associated with this challenge.
     * This is ONLY to be used upon creation of a challenge if users want to overwrite the
     * default message used in Stormpath.
     *
     *
     * @param message the message associated with this challenge. Message hast to contain a the macro
     *                '${code}'. This would be replaced with the code sent out within the message.
     * @return this instance for method chaining.
     */
    SmsChallenge setMessage(String message);
}
