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
package com.stormpath.sdk.factor.google;

import com.stormpath.sdk.factor.*;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link GoogleAuthenticatorFactor} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * GoogleAuthenticatorFactor-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Factors.GOOGLE_AUTHENTICATOR.where(Factors.GOOGLE_AUTHENTICATOR.status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.factor.Factors.GOOGLE_AUTHENTICATOR.*;
 *
 * ...
 *
 * <b>where(status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class GoogleAuthenticatorFactors {

    private static final GoogleAuthenticatorFactors INSTANCE;

    static{
        INSTANCE = new GoogleAuthenticatorFactors();
    }

    private static final Class<CreateGoogleAuthenticatorFactorRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.factor.google.DefaultCreateGoogleAuthenticatorFactorRequestBuilder");

    //prevent instantiation outside of outer class.
    // Use getInstance() to retrieve the singleton instance.
    private GoogleAuthenticatorFactors() {
    }

    /**
     * Returns a new {@link GoogleAuthenticatorFactorOptions} instance, used to customize how one or more {@link GoogleAuthenticatorFactor}s are retrieved.
     *
     * @return a new {@link GoogleAuthenticatorFactorOptions} instance, used to customize how one or more {@link GoogleAuthenticatorFactor}s are retrieved.
     */
    public static GoogleAuthenticatorFactorOptions<GoogleAuthenticatorFactorOptions> options() {
        return (GoogleAuthenticatorFactorOptions) Classes.newInstance("com.stormpath.sdk.impl.factor.google.DefaultGoogleAuthenticatorFactorOptions");
    }

    public static final GoogleAuthenticatorFactors getInstance(){
        return INSTANCE;
    }

    /**
     * Returns a new {@link GoogleAuthenticatorFactorCriteria} instance to use to formulate a Factor query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Factors.criteria().add(Factors.GOOGLE_AUTHENTICATOR.status().eq(FactorStatus.ENABLED))...
     * </pre>
     * versus:
     * <pre>
     * Factors.GOOGLE_AUTHENTICATOR.where(Factors.GOOGLE_AUTHENTICATOR.status().eq(FactorStatus.ENABLED))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(status().eq(FactorStatus.ENABLED))
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link GoogleAuthenticatorFactorCriteria} instance to use to formulate a Factor query.
     */
    public static GoogleAuthenticatorFactorCriteria criteria() {
        try {
            Class defaultGoogleAuthenticatorFactorCriteriaClazz = Class.forName("com.stormpath.sdk.impl.factor.google.DefaultGoogleAuthenticatorFactorCriteria");
            Constructor c = defaultGoogleAuthenticatorFactorCriteriaClazz.getDeclaredConstructor(GoogleAuthenticatorFactorOptions.class);
            return (GoogleAuthenticatorFactorCriteria) c.newInstance(options());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a new {@link GoogleAuthenticatorFactorCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link GoogleAuthenticatorFactorCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static GoogleAuthenticatorFactorCriteria where(Criterion criterion) {
        return (GoogleAuthenticatorFactorCriteria) criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the GoogleAuthenticatorFactor {@link Factor#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link GoogleAuthenticatorFactorCriteria} query.  For example:
     * <pre>
     * Factors.GOOGLE_AUTHENTICATOR.where(Factors.GOOGLE_AUTHENTICATOR.status().eq(FactorStatus.ENABLED));
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.criteria();
     * StringExpressionFactory statusExpressionFactory = Factors.GOOGLE_AUTHENTICATOR.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(FactorStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Factor#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     * used to construct a criterion when building an {@link FactorCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Factor {@link Factor#getFactorVerificationStatus() status}
     * property, to be used to construct a status Criterion when building an {@link FactorCriteria} query.  For example:
     * <pre>
     * Factors.GOOGLE_AUTHENTICATOR.where(Factors.GOOGLE_AUTHENTICATOR.verificationStatus().eq(FactorVerificationStatus.VERIFIED));
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.criteria();
     * StringExpressionFactory statusExpressionFactory = Factors.status();
     * Criterion statusEqualsVerified = statusExpressionFactory.eq(FactorStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Factor#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     * used to construct a criterion when building an {@link FactorCriteria} query.
     */
    public static EqualsExpressionFactory verificationStatus() {
        return newEqualsExpressionFactory("verificationStatus");
    }

    /**
     * Creates a new {@link CreateFactorRequestBuilder CreateGoogleAuthenticatorFactorRequestBuilder}
     * instance reflecting the specified {@link GoogleAuthenticatorFactor} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param googleAuthenticatorFactor the googleAuthenticatorFactor to create a new record for within Stormpath
     * @return a new {@link CreateFactorRequestBuilder CreateGoogleAuthenticatorFactorRequestBuilder}
     * instance reflecting the specified {@link GoogleAuthenticatorFactor} instance.
     * @see com.stormpath.sdk.account.Account#createFactor(CreateFactorRequest)
     * @since 1.1.0
     */
    public static CreateGoogleAuthenticatorFactorRequestBuilder newCreateRequestFor(GoogleAuthenticatorFactor googleAuthenticatorFactor) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, GoogleAuthenticatorFactor.class);
        return (CreateGoogleAuthenticatorFactorRequestBuilder) Classes.instantiate(ctor, googleAuthenticatorFactor);
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the GoogleAuthenticatorFactor {@link Factor#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link FactorCriteria} query. For example:
     * <pre>
     * Factors.where(<b>Factors.SMS.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.SMS.criteria();
     * DateExpressionFactory createdAt = Factors.SMS.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link Factor#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     * used to construct a criterion when building an {@link FactorCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory createdAt() {
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Group {@link Factor#getModifiedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link FactorCriteria} query. For example:
     * <pre>
     * Factors.where(<b>Factors.SMS.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.SMS.criteria();
     * DateExpressionFactory modifiedAt = Factors.SMS.getModifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link Factor#getCreatedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     * used to construct a criterion when building an {@link FactorCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory modifiedAt() {
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}

