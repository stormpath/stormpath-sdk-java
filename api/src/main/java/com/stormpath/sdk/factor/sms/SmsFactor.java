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
import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Map;

/**
 * An {@code SmsFactor} is a Factor that represents a phone used in SMS-based challenge workflows.
 * When issuing a challenge via an SmsFactor, a code is sent via SMS to the phone, and the user
 * can enter the received code back into the system to verify/complete the challenge.
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
     * Returns a paginated list of the sms factor's assigned challenges that match the specified query criteria.  The
     * {@link SmsFactors SmsFactors} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * smsFactor.getChallenges(Challenges.where(
     *     Challenges.status().eq(ChallengeStatus.WAITING_FOR_VALIDATION))
     *     .orderByStatus().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     * import static com.stormpath.sdk.challenge.Challenges.*;
     *
     * ...
     *
     * smsFactor.getChallenges(where(
     *     status().eq(ChallengeStatus.WAITING_FOR_VALIDATION))
     *     .orderByStatus().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the smsFactor's challenges that match the specified query criteria.
     * @since 1.1.0
     */
    ChallengeList getChallenges(ChallengeCriteria criteria);

    /**
     * Returns a paginated list of the sms factor's assigned challenges that match the specified query criteria.
     *
     * <p>This method is mostly provided as a non-type-safe alternative to the
     * {@link #getChallenges(ChallengeCriteria criteria)} method which might be useful in dynamic languages on
     * the
     * JVM (for example, with Groovy):
     * <pre>
     * def challenges = smsFactor.getChallenges([createdAt: '2016-01-01', orderBy: 'createdAt desc', limit: 5])
     * </pre>
     * The query parameter names and values must be equal to those documented in the Stormpath REST API product guide.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../factors/factorId/challenges?param1=value1&...
     * </pre>
     * </p>
     * If in doubt, use {@link #getChallenges(ChallengeCriteria criteria)}  as all possible query options are
     * available
     * via type-safe guarantees that can be auto-completed by most IDEs.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the smsFactor's challenges that match the specified query criteria.
     * @since 1.1.0
     */
    ChallengeList getChallenges(Map<String, Object> queryParams);

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
    Challenge createChallenge(Challenge challenge) throws ResourceException;

    /**
     * Creates a new {@link Challenge} assigned to this SmsFactor in the Stormpath server and returns the created resource
     * based on provided {@link CreateChallengeRequest}
     *
     * @param request {@link CreateChallengeRequest} used to create a challenge with.
     * @return the newly created {@link Challenge}.
     *
     * @since 1.1.0
     */
    Challenge createChallenge(CreateChallengeRequest request)throws ResourceException;
}
