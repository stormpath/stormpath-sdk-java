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
package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.factor.*;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link SmsFactor} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * SmsFactor-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Factors.SMS.where(Factors.SMS.status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .and(<b>Factors.verificationsStatus()</b>.eq(FactorVerificationStatus.VERIFIED))
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.factor.Factors.SMS.*;
 *
 * ...
 *
 * <b>where(status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .and(<b>verificationStatus()</b>.eq(FactorVerificationStatus.VERIFIED))
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class SmsFactors extends Factors {

    private static final SmsFactors INSTANCE;

    static{
        INSTANCE = new SmsFactors();
    }

    private static final Class<CreateFactorRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.factor.sms.DefaultCreateSmsFactorRequestBuilder");

    //prevent instantiation outside of outer class.
    // Use getInstance() to retrieve the singleton instance.
    private SmsFactors() {
        super();
    }

    /**
     * Returns a new {@link SmsFactorOptions} instance, used to customize how one or more {@link SmsFactor}s are retrieved.
     *
     * @return a new {@link SmsFactorOptions} instance, used to customize how one or more {@link SmsFactor}s are retrieved.
     */
    public static SmsFactorOptions<SmsFactorOptions> options() {
        return (SmsFactorOptions) Classes.newInstance("com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorOptions");
    }

    public static final SmsFactors getInstance(){
        return INSTANCE;
    }

    /**
     * Returns a new {@link FactorCriteria} instance to use to formulate a Factor query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Factors.criteria().add(Factors.SMS.status().eq(FactorStatus.ENABLED))...
     * </pre>
     * versus:
     * <pre>
     * Factors.where(Factors.SMS.status().eq(FactorStatus.ENABLED))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(status().eq(FactorStatus.ENABLED))
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link FactorCriteria} instance to use to formulate a Factor query.
     */
    public static SmsFactorCriteria criteria() {
        try {
            Class defaultSmsFactorCriteriaClazz = Class.forName("com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorCriteria");
            Constructor c = defaultSmsFactorCriteriaClazz.getDeclaredConstructor(SmsFactorOptions.class);
            return (SmsFactorCriteria) c.newInstance(options());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a new {@link FactorCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link FactorCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static SmsFactorCriteria where(Criterion criterion) {
        return (SmsFactorCriteria) criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Group {@link Factor#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link FactorCriteria} query.  For example:
     * <pre>
     * Factors.where(Factors.SMS.status().eq(FactorStatus.ENABLED));
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.criteria();
     * StringExpressionFactory statusExpressionFactory = Factors.status();
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
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Group {@link Factor#getFactorVerificationStatus() status}
     * property, to be used to construct a status Criterion when building an {@link FactorCriteria} query.  For example:
     * <pre>
     * Factors.where(Factors.SMS.verificationStatus().eq(FactorVerificationStatus.VERIFIED));
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * FactorCriteria criteria = Factors.criteria();
     * StringExpressionFactory statusExpressionFactory = Factors.verificationStatus();
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
     * Creates a new {@link com.stormpath.sdk.factor.CreateFactorRequestBuilder CreateSmsFactorRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.factor.sms.SmsFactor} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param smsFactor the smsFactor to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.factor.CreateFactorRequestBuilder CreateSmsFactorRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.factor.sms.SmsFactor} instance.
     * @see com.stormpath.sdk.account.Account#createFactor(CreateFactorRequest)
     * @since 1.1.0
     */
    public static CreateSmsFactorRequestBuilder newCreateRequestFor(SmsFactor smsFactor) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, SmsFactor.class);
        return (CreateSmsFactorRequestBuilder) Classes.instantiate(ctor, smsFactor);
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Group {@link Factor#getCreatedAt() createdAt}
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

