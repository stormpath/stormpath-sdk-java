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
package com.stormpath.sdk.challenge.sms;

import com.stormpath.sdk.challenge.*;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link SmsChallenge} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * SmsChallenge-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Challenges.SMS.where(Challenges.SMS.status()</b>.eq(SmsChallengeStatus.WAITING_FOR_VALIDATION)<b>)</b>
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.challenge.Challenges.SMS.*;
 *
 * ...
 *
 * <b>where(status()</b>.eq(SmsChallengeStatus.WAITING_FOR_VALIDATION)<b>)</b>
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class SmsChallenges extends Challenges {

    private static final SmsChallenges INSTANCE;

    static{
        INSTANCE = new SmsChallenges();
    }

    private static final Class<CreateChallengeRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequestBuilder");

    //prevent instantiation outside of outer class.
    // Use getInstance() to retrieve the singleton instance.
    private SmsChallenges() {
        super();
    }

    /**
     * Returns a new {@link SmsChallengeOptions} instance, used to customize how one or more {@link SmsChallenge}s are retrieved.
     *
     * @return a new {@link SmsChallengeOptions} instance, used to customize how one or more {@link SmsChallenge}s are retrieved.
     */
    public static SmsChallengeOptions<SmsChallengeOptions> options() {
        return (SmsChallengeOptions) Classes.newInstance("com.stormpath.sdk.impl.challenge.sms.DefaultSmsChallengeOptions");
    }

    public static final SmsChallenges getInstance(){
        return INSTANCE;
    }

    /**
     * Returns a new {@link ChallengeCriteria} instance to use to formulate a Challenge query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Challenges.criteria().add(Challenges.status().eq(ChallengeStatus.CREATED))...
     * </pre>
     * versus:
     * <pre>
     * Challenges.where(Challenges.status().eq(ChallengeStatus.CREATED))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(status().eq(ChallengeStatus.CREATED))
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link ChallengeCriteria} instance to use to formulate a Challenge query.
     */
    public static SmsChallengeCriteria criteria() {
        try {
            Class defaultSmsChallengeCriteriaClazz = Class.forName("com.stormpath.sdk.impl.challenge.sms.DefaultSmsChallengeCriteria");
            Constructor c = defaultSmsChallengeCriteriaClazz.getDeclaredConstructor(SmsChallengeOptions.class);
            return (SmsChallengeCriteria) c.newInstance(options());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a new {@link SmsChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link SmsChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static SmsChallengeCriteria where(Criterion criterion) {return (SmsChallengeCriteria)criteria().add(criterion);}

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Challenge {@link Challenge#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link ChallengeCriteria} query.  For example:
     * <pre>
     * Challenges.SMS.where(<b>Challenges.SMS.status()</b>.eq(SmsChallengeStatus.WAITING_FOR_VALIDATION);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * ChallengeCriteria criteria = Challenges.criteria();
     * StringExpressionFactory statusExpressionFactory = Challenges.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(SmsChallengeStatus.CREATED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Challenge#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link ChallengeCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * Creates a new {@link CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.challenge.Challenge} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param challenge the challenge to create a new record for within Stormpath
     * @return a new {@link CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.challenge.Challenge} instance.
     * @see com.stormpath.sdk.factor.sms.SmsFactor#createChallenge(CreateChallengeRequest)
     *
     * @since 1.1.0
     */
    public static CreateChallengeRequestBuilder<SmsChallenge> newCreateRequestFor(SmsChallenge challenge) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Challenge.class);
        return (CreateChallengeRequestBuilder<SmsChallenge>) Classes.instantiate(ctor, challenge);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}
