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
 * Static utility/helper methods for working with {@link RegisteredSamlServiceProvider} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * RegisteredSamlServiceProvider-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.createdAt()</b>.eq("2016-01-01")<b>)</b>
 *     .withTenant()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.2.1
 */
public class RegisteredSamlServiceProviders {
    private static final Class<CreateRegisteredSamlServiceProviderRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.saml.DefaultCreateRegisteredSamlServiceProviderRequestBuilder");

    //prevent instantiation
    private RegisteredSamlServiceProviders() {
    }

    /**
     * Returns a new {@link RegisteredSamlServiceProviderOptions} instance, used to customize how one or more {@link RegisteredSamlServiceProvider}s are retrieved.
     *
     * @return a new {@link RegisteredSamlServiceProviderOptions} instance, used to customize how one or more {@link RegisteredSamlServiceProvider}s are retrieved.
     */
    public static RegisteredSamlServiceProviderOptions<RegisteredSamlServiceProviderOptions> options() {
        return (RegisteredSamlServiceProviderOptions) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultRegisteredSamlServiceProviderOptions");
    }

    /**
     * Returns a new {@link RegisteredSamlServiceProviderCriteria} instance to use to formulate a RegisteredSamlServiceProvider query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.criteria().add(RegisteredSamlServiceProviders.createdAt().eq("2016-01-01"))...
     * </pre>
     * versus:
     * <pre>
     * RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.createdAt().eq("2016-01-01"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(createdAt().eq("2016-01-01"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link RegisteredSamlServiceProviderCriteria} instance to use to formulate a Phone query.
     */
    public static RegisteredSamlServiceProviderCriteria criteria() {
        return (RegisteredSamlServiceProviderCriteria) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultRegisteredSamlServiceProviderCriteria");
    }

    /**
     * Creates a new {@link RegisteredSamlServiceProviderCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link RegisteredSamlServiceProviderCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static RegisteredSamlServiceProviderCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getName() name}
     * property, to be used to construct a name Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.name()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory nameExpressionFactory = RegisteredSamlServiceProviders.name();
     * Criterion nameStartsWithFoo = nameExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getName() name}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getDescription()} () description}
     * property, to be used to construct a description Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.description()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a description-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory nameExpressionFactory = RegisteredSamlServiceProviders.description();
     * Criterion descriptionStartsWithFoo = descriptionExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add( descriptionStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getDescription()}  description}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getAssertionConsumerServiceUrl()}  assertionConsumerServiceUrl}
     * property, to be used to construct a assertionConsumerServiceUrl Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.assertionConsumerServiceUrl()</b>.startsWithIgnoreCase("https://xyz");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("https://xyz")</code> method.  This
     * produces a assertionConsumerServiceUrl-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory assertionConsumerServiceUrlExpressionFactory = RegisteredSamlServiceProviders.assertionConsumerServiceUrl();
     * Criterion assertionConsumerServiceUrlStartsWithHttps = assertionConsumerServiceUrlExpressionFactory.startsWithIgnoreCase("https://xyz");
     * criteria.add(assertionConsumerServiceUrlStartsWithHttps);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getAssertionConsumerServiceUrl() assertionConsumerServiceUrl}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory assertionConsumerServiceUrl() {
        return newStringExpressionFactory("assertionConsumerServiceUrl");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getEntityId()} ()}  entityId}
     * property, to be used to construct a entityId Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.entityId()</b>.startsWithIgnoreCase("some:id");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("some:id")</code> method.  This
     * produces a entityId-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory entityIdExpressionFactory = RegisteredSamlServiceProviders.entityId();
     * Criterion entityIdStartsWithSomeId = entityIdExpressionFactory.startsWithIgnoreCase("some:id");
     * criteria.add(entityIdStartsWithSomeId);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getEntityId()} entityId}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory entityId() {
        return newStringExpressionFactory("entityId");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getEntityId()} ()}  entityId}
     * property, to be used to construct a entityId Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.entityId()</b>.startsWithIgnoreCase("some:id");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("some:id")</code> method.  This
     * produces a entityId-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory entityIdExpressionFactory = RegisteredSamlServiceProviders.entityId();
     * Criterion entityIdStartsWithSomeId = entityIdExpressionFactory.startsWithIgnoreCase("some:id");
     * criteria.add(entityIdStartsWithSomeId);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getEntityId()}  entityId}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory nameIdFormat() {
        return newStringExpressionFactory("nameIdFormat");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getEncodedX509SigningCert()} ()}  encodedX509SigningCert}
     * property, to be used to construct a entityId Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.encodedX509SigningCert()</b>.startsWithIgnoreCase("BEGIN--");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("BEGIN--")</code> method.  This
     * produces a encodedX509Certificate-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * StringExpressionFactory encodedX509CertificateFactory = RegisteredSamlServiceProviders.encodedX509Certificate();
     * Criterion encodedX509CertificateStartsWithBegin = encodedX509CertificateExpressionFactory.startsWithIgnoreCase("BEGIN--");
     * criteria.add(encodedX509CertificateStartsWithSomeId);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link RegisteredSamlServiceProvider#getEncodedX509SigningCert()}  encodedX509Certificate}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static StringExpressionFactory encodedX509Certificate() {
        return newStringExpressionFactory("encodedX509Certificate");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a createdAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * DateExpressionFactory createdAt = RegisteredSamlServiceProviders.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link RegisteredSamlServiceProvider#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the RegisteredSamlServiceProvider {@link RegisteredSamlServiceProvider#getCreatedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.  For example:
     * <pre>
     * RegisteredSamlServiceProviders.where(<b>RegisteredSamlServiceProviders.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a modifiedAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * RegisteredSamlServiceProviderCriteria criteria = RegisteredSamlServiceProviders.criteria();
     * DateExpressionFactory modifiedAt = RegisteredSamlServiceProviders.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link RegisteredSamlServiceProvider#getModifiedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link RegisteredSamlServiceProviderCriteria} query.
     */
    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    /**
     * Creates a new {@link com.stormpath.sdk.saml.CreateRegisteredSamlServiceProviderRequestBuilder createRegisteredSamlServiceProviderRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.saml.RegisteredSamlServiceProvider} instance. The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param registeredSamlServiceProvider the registeredSamlServiceProvider to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.saml.CreateRegisteredSamlServiceProviderRequestBuilder createRegisteredSamlServiceProviderRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.saml.RegisteredSamlServiceProvider} instance.
     ** @see com.stormpath.sdk.tenant.Tenant#createRegisterdSamlServiceProvider(RegisteredSamlServiceProvider)
     *
     */
    public static CreateRegisteredSamlServiceProviderRequestBuilder newCreateRequestFor(RegisteredSamlServiceProvider registeredSamlServiceProvider) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, RegisteredSamlServiceProvider.class);
        return (CreateRegisteredSamlServiceProviderRequestBuilder) Classes.instantiate(ctor, registeredSamlServiceProvider);
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
