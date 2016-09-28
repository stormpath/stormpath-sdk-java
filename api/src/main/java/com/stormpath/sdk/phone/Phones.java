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
package com.stormpath.sdk.phone;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link Phone} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Phone-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Phones.where(Phones.name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>Phones.status()</b>.eq(PhoneStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccount()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.phone.Phones.*;
 *
 * ...
 *
 * <b>where(name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>status()</b>.eq(PhoneStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccount()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class Phones {

    private static final Class<CreatePhoneRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.phone.DefaultCreatePhoneRequestBuilder");

    //prevent instantiation
    private Phones() {
    }

    /**
     * Returns a new {@link PhoneOptions} instance, used to customize how one or more {@link Phone}s are retrieved.
     *
     * @return a new {@link PhoneOptions} instance, used to customize how one or more {@link Phone}s are retrieved.
     */
    public static PhoneOptions<PhoneOptions> options() {
        return (PhoneOptions) Classes.newInstance("com.stormpath.sdk.impl.phone.DefaultPhoneOptions");
    }

    /**
     * Returns a new {@link PhoneCriteria} instance to use to formulate a Phone query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Phones.criteria().add(Phones.name().eqIgnoreCase("Foo"))...
     * </pre>
     * versus:
     * <pre>
     * Phones.where(Phones.name().eqIgnoreCase("Foo"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(name().eqIgnoreCase("Foo"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link PhoneCriteria} instance to use to formulate a Phone query.
     */
    public static PhoneCriteria criteria() {
        return (PhoneCriteria) Classes.newInstance("com.stormpath.sdk.impl.phone.DefaultPhoneCriteria");
    }

    /**
     * Creates a new {@link PhoneCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link PhoneCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static PhoneCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Phone {@link Phone#getName() name}
     * property, to be used to construct a name Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.name()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * StringExpressionFactory nameExpressionFactory = Phones.name();
     * Criterion nameStartsWithFoo = nameExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Phone#getName() name}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Phone {@link Phone#getNumber() number}
     * property, to be used to construct a number Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.number()</b>.startsWithIgnoreCase("201");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a number-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * StringExpressionFactory numberExpressionFactory = Phones.number();
     * Criterion numberStartsWithFoo = numberExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Phone#getNumber() number}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static StringExpressionFactory number() {
        return newStringExpressionFactory("number");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Phone {@link Phone#getDescription() description}
     * property, to be used to construct a description Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.description()</b>.startsWithIgnoreCase("This is a description");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a description-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * StringExpressionFactory descriptionExpressionFactory = Phones.description();
     * Criterion descriptionStartsWithFoo = descriptionExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(descriptionStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Phone#getNumber() number}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Phone {@link Phone#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.status()</b>.eq(PhoneStatus.ENABLED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * StringExpressionFactory statusExpressionFactory = Phones.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(PhoneStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Phone#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Phone {@link Phone#getVerificationStatus() verificationStatus}
     * property, to be used to construct a status Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.verificationStatus()</b>.eq(PhoneVerificationStatus.VERIFIED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a verificationStatus-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * StringExpressionFactory statusExpressionFactory = Phones.verificationStatus();
     * Criterion verificationStatusEqualsEnabled = verificationStatusExpressionFactory.eq(PhoneVerificationStatus.VERIFIED);
     * criteria.add(verificationStatusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Phone#getVerificationStatus() verificationStatus}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static EqualsExpressionFactory verificationStatus() {
        return newEqualsExpressionFactory("verificationStatus");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Phone {@link Phone#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * DateExpressionFactory createdAt = Phones.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link Phone#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Phone {@link Phone#getModifiedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link PhoneCriteria} query.  For example:
     * <pre>
     * Phones.where(<b>Phones.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * PhoneCriteria criteria = Phones.criteria();
     * DateExpressionFactory createdAt = Phones.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link Phone#getModifiedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link PhoneCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    /**
     * Creates a new {@link com.stormpath.sdk.phone.CreatePhoneRequestBuilder CreatePhoneRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.phone.Phone} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param phone the phone to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.phone.CreatePhoneRequestBuilder CreatePhoneRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.phone.Phone} instance.
     ** @see com.stormpath.sdk.account.Account#createPhone(CreatePhoneRequest)
     *
     * @since 1.1.0
     */
    public static CreatePhoneRequestBuilder newCreateRequestFor(Phone phone) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Phone.class);
        return (CreatePhoneRequestBuilder) Classes.instantiate(ctor, phone);
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
