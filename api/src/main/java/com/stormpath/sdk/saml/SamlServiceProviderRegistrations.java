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
package com.stormpath.sdk.saml;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link SamlServiceProviderRegistration} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Phone-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.createdAt()</b>.eq("2016-01-01")<b>)</b>
 *     .withServiceProvider()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.3.0
 */
public class SamlServiceProviderRegistrations {
    private static final Class<CreateSamlServiceProviderRegistrationRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.saml.DefaultCreateSamlServiceProviderRegistrationRequestBuilder");

    //prevent instantiation
    private SamlServiceProviderRegistrations() {
    }


    /**
     * Returns a new {@link SamlServiceProviderRegistrationOptions} instance, used to customize how one or more {@link SamlServiceProviderRegistration}s are retrieved.
     *
     * @return a new {@link SamlServiceProviderRegistrationOptions} instance, used to customize how one or more {@link SamlServiceProviderRegistration}s are retrieved.
     */
    public static SamlServiceProviderRegistrationOptions<SamlServiceProviderRegistrationOptions> options() {
        return (SamlServiceProviderRegistrationOptions) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlServiceProviderRegistrationOptions");
    }


    /**
     * Returns a new {@link SamlServiceProviderRegistrationCriteria} instance to use to formulate a SamlServiceProviderRegistration query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * SamlServiceProviderRegistrations.criteria().add(SamlServiceProviderRegistrations.createdAt().eq("2016-01-01"))...
     * </pre>
     * versus:
     * <pre>
     * SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.createdAt().eq("2016-01-01"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(createdAt().eq("2016-01-01"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link SamlServiceProviderRegistrationCriteria} instance to use to formulate a Phone query.
     */
    public static SamlServiceProviderRegistrationCriteria criteria() {
        return (SamlServiceProviderRegistrationCriteria) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlServiceProviderRegistrationCriteria");
    }

    /**
     * Creates a new {@link SamlServiceProviderRegistrationCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link SamlServiceProviderRegistrationCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static SamlServiceProviderRegistrationCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Phone {@link SamlServiceProviderRegistration#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link SamlServiceProviderRegistration} query.  For example:
     * <pre>
     * SamlServiceProviderRegistrations.where(<b>SamlServiceProviderRegistrations.status()</b>.eq(SamlServiceProviderRegistrationStatus.ENABLED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * SamlServiceProviderRegistrationCriteria criteria = SamlServiceProviderRegistrations.criteria();
     * StringExpressionFactory statusExpressionFactory = SamlServiceProviderRegistrations.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(SamlServiceProviderRegistrationStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link SamlServiceProviderRegistration#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Phone {@link SamlServiceProviderRegistration#getDefaultRelayState()}
     * property, to be used to construct a number Criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.  For example:
     * <pre>
     * SamlServiceProviderRegistrations.where(<b>SamlServiceProviderRegistrations.defaultRelayState()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a number-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * SamlServiceProviderRegistrationCriteria criteria = SamlServiceProviderRegistrations.criteria();
     * StringExpressionFactory numberExpressionFactory = SamlServiceProviderRegistrations.defaultRelayState();
     * Criterion defaultRelayStateStartsWithFoo = numberExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link SamlServiceProviderRegistration#getDefaultRelayState()}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.
     */
    public static EqualsExpressionFactory defaultRelayState() {
        return newEqualsExpressionFactory("defaultRelayState");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Phone {@link SamlServiceProviderRegistration#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.  For example:
     * <pre>
     * SamlServiceProviderRegistrations.where(<b>SamlServiceProviderRegistrations.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a createdAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * SamlServiceProviderRegistrationCriteria criteria = SamlServiceProviderRegistrations.criteria();
     * DateExpressionFactory createdAt = SamlServiceProviderRegistrations.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link SamlServiceProviderRegistration#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.
     */
    public static DateExpressionFactory createdAt() {
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Phone {@link SamlServiceProviderRegistration#getModifiedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.  For example:
     * <pre>
     * SamlServiceProviderRegistrations.where(<b>SamlServiceProviderRegistrations.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a modifiedAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * SamlServiceProviderRegistrationCriteria criteria = Phones.criteria();
     * DateExpressionFactory createdAt = SamlServiceProviderRegistrations.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link SamlServiceProviderRegistration#getModifiedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlServiceProviderRegistrationCriteria} query.
     */
    public static DateExpressionFactory modifiedAt() {
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    /**
     * Creates a new {@link com.stormpath.sdk.saml.CreateSamlServiceProviderRegistrationRequestBuilder createSamlServiceProviderRegistrationRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.saml.SamlServiceProviderRegistration} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param samlServiceProviderRegistration the samlServiceProviderRegistration to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.saml.CreateSamlServiceProviderRegistrationRequestBuilder createSamlServiceProviderRegistrationRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.saml.SamlServiceProviderRegistration} instance.
     *
     */
    public static CreateSamlServiceProviderRegistrationRequestBuilder newCreateRequestFor(SamlServiceProviderRegistration samlServiceProviderRegistration) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, SamlServiceProviderRegistration.class);
        return (CreateSamlServiceProviderRegistrationRequestBuilder) Classes.instantiate(ctor, samlServiceProviderRegistration);
    }

    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}
