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

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.resource.ResourceException;

/**
 * An {@code SmsFactor} is a {@link Factor} that uses sms messages to challenge the authentication.
 * Upon challenging an SmsFactor an sms is send to the{@code SmsFactor}'s {@link Phone} including
 * a code in its text for the user to enter it back to the system for verification.
 *
 * @since 1.1.0
 */
public interface SmsFactor extends Factor {

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
     * Returns the most recent {@link Challenge} resource associated with this {@code SmsFactor}.
     *
     * @return the most recent {@link Challenge} resource associated with this {@code SmsFactor}.
     */
    Challenge getMostRecentChallenge();

    /**
     * Sets the {@link Challenge} resource to be associated with this {@code SmsFactor}.
     * @param challenge {@link Challenge} resource to be associated with this {@code SmsFactor}
     *
     * @return this instance for method chaining.
     */
    SmsFactor setChallenge(Challenge challenge);

    /**
     * Returns a paginated list of all {@link Challenge}es associated with this {@code SmsFactor}.
     *
     * @return a paginated list of all {@link Challenge}es associated with this {@code SmsFactor}.
     */
    ChallengeList getChallenges();

    /**
     * Challenges this {@code SmsFactor}.
     *
     * @return this instance for method chaining.
     */
    SmsFactor challenge();

    /**
     * Challenges this {@code SmsFactor} by passing a {@link Challenge} instance.
     *
     * @return created {@link Challenge}
     */
    Challenge createChallenge(Challenge challenge)throws ResourceException;
}
