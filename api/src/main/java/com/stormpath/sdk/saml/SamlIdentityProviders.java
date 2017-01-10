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

/**
 * Static utility/helper methods for working with {@link SamlIdentityProvider} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * SamlIdentityProvider-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>SamlIdentityProviders.where(SamlIdentityProviders.createdAt()</b>.eq("2016-01-01")<b>)</b>
 *     .withRegisteredSamlServiceProviders()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.3.0
 */
public class SamlIdentityProviders {

    //prevent instantiation
    private SamlIdentityProviders() {
    }


    /**
     * Returns a new {@link SamlIdentityProviderOptions} instance, used to customize how one or more {@link SamlIdentityProvider}s are retrieved.
     *
     * @return a new {@link SamlIdentityProviderOptions} instance, used to customize how one or more {@link SamlIdentityProvider}s are retrieved.
     */
    public static SamlIdentityProviderOptions<SamlIdentityProviderOptions> options() {
        return (SamlIdentityProviderOptions) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlIdentityProviderOptions");
    }

    /**
     * Returns a new {@link SamlIdentityProviderCriteria} instance to use to formulate a SamlIdentityProvider query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * SamlIdentityProviders.criteria().add(SamlIdentityProviders.createdAt().eq("2016-01-01"))...
     * </pre>
     * versus:
     * <pre>
     * SamlIdentityProviders.where(SamlIdentityProviders.createdAt().eq("2016-01-01"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(createdAt().eq("2016-01-01"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link SamlIdentityProviderCriteria} instance to use to formulate a Phone query.
     */
    public static SamlIdentityProviderCriteria criteria() {
        return (SamlIdentityProviderCriteria) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlIdentityProviderCriteria");
    }

    /**
     * Returns a new {@link RegisteredSamlServiceProviderCriteria} instance to use to formulate a RegisteredSamlServiceProvide query.
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
    public static SamlIdentityProviderCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the SamlIdentityProvider {@link SamlIdentityProvider#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link SamlIdentityProviderCriteria} query.  For example:
     * <pre>
     * SamlIdentityProviders.where(<b>SamlIdentityProviders.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a createdAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * SamlIdentityProviderCriteria criteria = SamlIdentityProviders.criteria();
     * DateExpressionFactory createdAt = SamlIdentityProviders.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link SamlIdentityProvider#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlIdentityProviderCriteria} query.
     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the SamlIdentityProvider {@link SamlIdentityProvider#getCreatedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link SamlIdentityProviderCriteria} query.  For example:
     * <pre>
     * SamlIdentityProviders.where(<b>SamlIdentityProviders.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a modifiedAt-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * SamlIdentityProviderCriteria criteria = SamlIdentityProviders.criteria();
     * DateExpressionFactory modifiedAt = SamlIdentityProviders.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link SamlIdentityProvider#getModifiedAt()}  modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link SamlIdentityProviderCriteria} query.
     */
    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
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
