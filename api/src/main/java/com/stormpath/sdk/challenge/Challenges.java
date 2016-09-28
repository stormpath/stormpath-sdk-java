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
package com.stormpath.sdk.challenge;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link Challenge} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Group-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Challenges.where(Challenges.messageId()</b>.containsIgnoreCase("2345")<b>)</b>
 *     .and(<b>Challenges.status()</b>.eq(ChallengeStatus.DENIED))
 *     .orderByCode().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.challenge.Challenges.*;
 *
 * ...
 *
 * <b>where(code()</b>.containsIgnoreCase("3569")<b>)</b>
 *     .and(<b>status()</b>.eq(ChallengeStatus.WAITING_FOR_PROVIDER))
 *     .orderByName().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class Challenges {

    private static final Class<CreateChallengeRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequestBuilder");

    //prevent instantiation
    private Challenges() {
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
    public static ChallengeCriteria criteria() {
        return (ChallengeCriteria) Classes.newInstance("com.stormpath.sdk.impl.challengep.DefaultChallengeCriteria");
    }

    public static ChallengeOptions<ChallengeOptions> options() {
        return (ChallengeOptions) Classes.newInstance("com.stormpath.sdk.impl.challenge.DefaultChallengeOptions");
    }

    /**
     * Creates a new {@link ChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link ChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static ChallengeCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Challenge {@link Challenge#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link ChallengeCriteria} query.  For example:
     * <pre>
     * Challenges.where(<b>Challenges.status()</b>.eq(ChallengeStatus.CREATED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * ChallengeCriteria criteria = Challenges.criteria();
     * StringExpressionFactory statusExpressionFactory = Challenges.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(ChallengeStatus.CREATED);
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
     * Creates a new {@link com.stormpath.sdk.challenge.CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.challenge.Challenge} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param challenge the challenge to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.challenge.CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.challenge.Challenge} instance.
     * @see com.stormpath.sdk.factor.sms.SmsFactor#createChallenge(CreateChallengeRequest)
     *
     * @since 1.1.0
     */
    public static CreateChallengeRequestBuilder newCreateRequestFor(Challenge challenge) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Challenge.class);
        return (CreateChallengeRequestBuilder) Classes.instantiate(ctor, challenge);
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
