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
package com.stormpath.sdk.challenge.google;

import com.stormpath.sdk.challenge.*;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link GoogleAuthenticatorChallenge} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * GoogleAuthenticatorChallenge-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Challenges.GOOGLE_AUTHENTICATOR.where(Challenges.GOOGLE_AUTHENTICATOR.status()</b>.eq(GoogleAuthenticatorChallengeStatus.SUCCESS)<b>)</b>
 *     .descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.challenge.Challenges.GOOGLE_AUTHENTICATOR.*;
 *
 * ...
 *
 * <b>where(status()</b>.eq(GoogleAuthenticatorChallengeStatus.SUCCESS)<b>)</b>
*      .descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class GoogleAuthenticatorChallenges extends Challenges {

    private static final GoogleAuthenticatorChallenges INSTANCE;

    static{
        INSTANCE = new GoogleAuthenticatorChallenges();
    }

    private static final Class<CreateChallengeRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequestBuilder");

    //Prevent instantiation outside of outer class.
    //Use getInstance() to retrieve the singleton instance.
    private GoogleAuthenticatorChallenges() {
        super();
    }

    /**
     * Returns a new {@link GoogleAuthenticatorChallengeOptions} instance, used to customize how one or more {@link GoogleAuthenticatorChallenge}s are retrieved.
     *
     * @return a new {@link GoogleAuthenticatorChallengeOptions} instance, used to customize how one or more {@link GoogleAuthenticatorChallenge}s are retrieved.
     */
    public static GoogleAuthenticatorChallengeOptions<GoogleAuthenticatorChallengeOptions> options() {
        return (GoogleAuthenticatorChallengeOptions) Classes.newInstance("com.stormpath.sdk.impl.challenge.google.DefaultGoogleAuthenticatorChallengeOptions");
    }

    public static final GoogleAuthenticatorChallenges getInstance(){
        return INSTANCE;
    }

    /**
     * Returns a new {@link ChallengeCriteria} instance to use to formulate a Challenge query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Challenges.GOOGLE_AUTHENTICATOR.criteria().add(Challenges.GOOGLE_AUTHENTICATOR.status().eq(ChallengeStatus.CREATED))...
     * </pre>
     * versus:
     * <pre>
     * Challenges.GOOGLE_AUTHENTICATOR.where(Challenges.GOOGLE_AUTHENTICATOR.status().eq(ChallengeStatus.CREATED))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(status().eq(ChallengeStatus.CREATED))
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link ChallengeCriteria} instance to use to formulate a Challenge query.
     */
    public static GoogleAuthenticatorChallengeCriteria criteria() {
        try {
            Class defaultGoogleAuthenticatorChallengeCriteriaClazz = Class.forName("com.stormpath.sdk.impl.challenge.google.DefaultGoogleAuthenticatorChallengeCriteria");
            Constructor c = defaultGoogleAuthenticatorChallengeCriteriaClazz.getDeclaredConstructor(GoogleAuthenticatorChallengeOptions.class);
            return (GoogleAuthenticatorChallengeCriteria) c.newInstance(options());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a new {@link GoogleAuthenticatorChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link GoogleAuthenticatorChallengeCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static GoogleAuthenticatorChallengeCriteria where(Criterion criterion) {return (GoogleAuthenticatorChallengeCriteria)criteria().add(criterion);}

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Challenge {@link Challenge#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link ChallengeCriteria} query.  For example:
     * <pre>
     * Challenges.GOOGLE_AUTHENTICATOR.where(<b>Challenges.GOOGLE_AUTHENTICATOR.status()</b>.eq(ChallengeStatus.CREATED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * ChallengeCriteria criteria = Challenges.criteria();
     * StringExpressionFactory statusExpressionFactory = Challenges.GOOGLE_AUTHENTICATOR.status();
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
     * Creates a new {@link CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     * instance reflecting the specified {@link Challenge} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param challenge the challenge to create a new record for within Stormpath
     * @return a new {@link CreateChallengeRequestBuilder CreateChallengeRequestBuilder}
     *         instance reflecting the specified {@link Challenge} instance.
     * @see com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor#createChallenge(CreateChallengeRequest)
     *
     * @since 1.1.0
     */
    public static CreateChallengeRequestBuilder<GoogleAuthenticatorChallenge> newCreateRequestFor(GoogleAuthenticatorChallenge challenge) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Challenge.class);
        return (CreateChallengeRequestBuilder<GoogleAuthenticatorChallenge>) Classes.instantiate(ctor, challenge);
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
