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
import com.stormpath.sdk.resource.*;

/**
 * This domain object represents a challenge of {@link com.stormpath.sdk.factor.sms.SmsFactor} smsFactor for a Multi Factor Authentication.
 * <p/>
 * In a Multi Factor Authentication scenario a challenge would be issued to challenge the authentication by an additional {@link Factor}
 * like and {@link com.stormpath.sdk.factor.sms.SmsFactor}
 *
 * @since 1.0.4
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
     * Tenant.
     *
     * @param message the message associated with this challenge.
     * @return this instance for method chaining.
     */
    Challenge setMessage(String message);

    /**
     * Returns the message id returned by external sms service provider with this challenge.
     * for a given sms message sent
     *
     * @return message id associated with this challenge
     */
    String getMessageId();

    /**
     * Sets the message id returned by external sms service provider with this challenge.
     * for a given sms message sent
     *
     * @param messageId associated with this challenge
     */
    Challenge setMessageId(String messageId);

    /**
     * Returns the status of this challenge object
     *
     * @return status associated with this challenge
     */
    ChallengeStatus getStatus();

    /**
     * Sets the status associated with this challenge.
     * Tenant.
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
     * @param account associated with this challenge
     */
    Challenge setAccount(Account account);

    /**
     * Returns the sms factor associated with this challenge
     *
     * @return sms factor associated with this challenge
     */
    Factor getFactor();

    /**
     * Sets the factor associated with this challenge.
     *
     * @param smsFactor associated with this challenge
     */
    Challenge setFactor(Factor smsFactor);

    /**
     * Returns the token associated with this challenge
     *
     * @return token associated with this challenge
     */
    String getToken();

    /**
     * Sets the token associated with this challenge.
     *
     * @param token associated with this challenge
     */
    Challenge setToken(String token);

    /**
     * Returns the sent code in the sms associated with this challenge
     *
     * @return code in the sms associated with this challenge
     */
    Challenge setCode(String code);

    /**
     * Returns true in case the challenge is validated with the given code
     * and false if otherwise
     *
     * @return true in case the challenge is validated with the given code
     */
    boolean validate(String code);
}
