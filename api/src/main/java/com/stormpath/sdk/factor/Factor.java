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
package com.stormpath.sdk.factor;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.resource.*;

import java.util.Map;

/**
 * A factor represents an additional step in authenticating a resource in the realm of
 * Multi Factor Authentication.
 *
 * @param <T> a subclass of {@link Challenge} specifying the kind of Challenge associated with this {@code Factor}.
 *
 * @since 1.1.0
 */
public interface Factor<T extends Challenge> extends Resource, Saveable, Deletable, Auditable {

    /**
     * Returns the {@link FactorStatus Factors's status}.
     *
     * @return the {@link FactorStatus Factors's status}.
     */
    FactorStatus getStatus();

    /**
     * Sets the Factor's status. {@link FactorStatus}
     *
     * @param status the Factor's status.
     * @return this instance for method chaining.
     */
    Factor setStatus(FactorStatus status);

    /**
     * Returns the {@link FactorVerificationStatus Factors's verification status}.
     *
     * @return the the {@link FactorVerificationStatus Factors's verification status}.
     */
    FactorVerificationStatus getFactorVerificationStatus();

    /**
     * Sets the {@link FactorVerificationStatus Factors's verification status}.
     *
     * @param verificationStatus the Factor's verification status.
     * @return this instance for method chaining.
     */
    Factor setFactorVerificationStatus(FactorVerificationStatus verificationStatus);

    /**
     * Returns the {@link Account} to which this Factor is associated.
     *
     * @return the {@link Account} to which this Factor is associated.
     */
    Account getAccount();

    /**
     * Sets the the {@link Account} associated with this Factor.
     *
     * @param account associated with this Factor.
     * @return this instance for method chaining.
     */
    Factor setAccount(Account account);

    /**
     * Returns the most recent {@link Challenge} resource associated with this {@code Factor}.
     *
     * @return the most recent {@link Challenge} resource associated with this {@code Factor}.
     */
    T getMostRecentChallenge();

    /**
     * Returns a paginated list of all {@link Challenge}es associated with this {@code Factor}.
     *
     * @return a paginated list of all {@link Challenge}es associated with this {@code Factor}.
     */
    ChallengeList<T> getChallenges();

    /**
     * Sets the {@link Challenge} resource to be associated with this {@code Factor}.
     * @param challenge {@link Challenge} resource to be associated with this {@code Factor}
     *
     * @return this instance for method chaining.
     */
    Factor setChallenge(T challenge);

    /**
     * Returns a paginated list of the factor's assigned challenges that match the specified query criteria.  The
     * {@link Factors Factors} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * factor.getChallenges(Challenges.SMS.where(
     *     Challenges.SMS.status().eq(ChallengeStatus.SMS.WAITING_FOR_VALIDATION))
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
     * factor.getChallenges(where(
     *     status().eq(ChallengeStatus.SMS.WAITING_FOR_VALIDATION))
     *     .orderByStatus().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the smsFactor's challenges that match the specified query criteria.
     * @since 1.1.0
     */
    ChallengeList<T> getChallenges(ChallengeCriteria criteria);

    /**
     * Returns a paginated list of the sms factor's assigned challenges that match the specified query criteria.
     *
     * <p>This method is mostly provided as a non-type-safe alternative to the
     * {@link #getChallenges(ChallengeCriteria criteria)} method which might be useful in dynamic languages on
     * the
     * JVM (for example, with Groovy):
     * <pre>
     * def challenges = factor.getChallenges([createdAt: '2016-01-01', orderBy: 'createdAt desc', limit: 5])
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
     * @return a paginated list of the factor's challenges that match the specified query criteria.
     * @since 1.1.0
     */
    ChallengeList<T> getChallenges(Map<String, Object> queryParams);

    /**
     * Challenges this {@code Factor} by passing a {@link Challenge} instance.
     *
     * @return created {@link Challenge}
     */
    T createChallenge(T challenge) throws ResourceException;

    /**
     * Creates a new {@link Challenge} assigned to this Factor in the Stormpath server and returns the created resource
     * based on provided {@link CreateChallengeRequest}
     *
     * @param request {@link CreateChallengeRequest} used to create a challenge with.
     * @return the newly created {@link Challenge}.
     *
     * @since 1.1.0
     */
    T createChallenge(CreateChallengeRequest request)throws ResourceException;
}
