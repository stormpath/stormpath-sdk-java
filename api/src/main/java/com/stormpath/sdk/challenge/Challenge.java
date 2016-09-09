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

//todo: mehrshad


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
     * Sets the message associated with this directory.
     * Tenant.
     *
     * @param message the message associated with this challenge.
     * @return this instance for method chaining.
     */
    Challenge setMessage(String message);
    String getMessageId();
    Challenge setMessageId(String messageId);
    ChallengeStatus getStatus();
    Challenge setStatus(ChallengeStatus status);
    Account getAccount();
    Challenge setAccount(Account account);
    Factor getFactor();
    Challenge setFactor(Factor smsFactor);
    String getToken();
    Challenge setToken(String token);
    Challenge setCode(String code);
    boolean validate(String code);
}
