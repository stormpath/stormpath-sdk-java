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
package com.stormpath.sdk.challenge;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.resource.*;

/**
 * This domain object represents a challenge of a {@link Factor} for a Multi Factor Authentication.
 * <p/>
 * In a Multi Factor Authentication scenario authenticating a user is challenged by additional {@link Factor}s like an {@link SmsFactor}.
 *
 * For Example: Using an {@link SmsFactor} as an additional {@link Factor} for authentication the user would receive an sms including a multi-digit code within its message.
 * The user would verify the authentication challenge by entering the sms code back to the system.
 *
 * @since 1.1.0
 */
public interface Challenge extends Resource, Saveable, Deletable, Auditable {

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
    Challenge setMessage(String message);

    /**
     * Returns the status of this challenge object
     *
     * @return status associated with this challenge
     */
    ChallengeStatus getStatus();

    /**
     * Sets the status associated with this challenge.
     *
     * @param status the status associated with this challenge.
     * @return this instance for method chaining.
     */
    Challenge setStatus(ChallengeStatus status);

    /**
     * Returns the account associated with this challenge
     *
     * @return account associated with this challenge
     */
    Account getAccount();

    /**
     * Sets the account associated with this challenge.
     *
     * @param account associated with this challenge.
     * @return this instance for method chaining.
     */
    Challenge setAccount(Account account);

    /**
     * Returns the factor associated with this challenge
     *
     * @return factor associated with this challenge
     */
    Factor getFactor();

    /**
     * Sets the factor associated with this challenge.
     *
     * @param factor associated with this challenge.
     * @return this instance for method chaining.
     */
    Challenge setFactor(Factor factor);

    /**
     * This is a convenience method to POST a code to an existing challenge resource in Stormpath for validation.
     * Returns true in case the challenge is validated with the given code
     * and false if otherwise.
     * <p><b>Immediate Execution:</b> Unlike other Challenge methods, you do <em>not</em> need to call {@link #save()}
     * {@link Challenge#getStatus()} method will return the submission status in the event of a failure.
     *
     * @parame code  The code to be validated with this challenge.
     * @return true in case the challenge is validated with the given code.
     */
    boolean validate(String code);
}
