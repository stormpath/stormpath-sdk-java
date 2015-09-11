/*
* Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.organization;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link Organization} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Organization-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Organizations.where(Organizations.name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>Organizations.status()</b>.eq(OrganizationStatus.ENABLED))
 *     .orderByName().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.organization.Organizations.*;
 *
 * ...
 *
 * <b>where(name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>status()</b>.eq(OrganizationStatus.ENABLED))
 *     .orderByName().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.0.RC5
 */
public final class Organizations {

    /**
     * Returns a new {@link OrganizationOptions} instance, used to customize how one or more {@link Organization}(s) are retrieved.
     *
     * @return a new {@link OrganizationOptions} instance, used to customize how one or more {@link Organization}(s) are retrieved.
     */

    private static final Class<CreateOrganizationRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.organization.DefaultCreateOrganizationRequestBuilder");
       
    /**
     * Returns a new {@link OrganizationOptions} instance, used to customize how one or more {@link Organization}(s) are retrieved.
     *
     * @return a new {@link OrganizationOptions} instance, used to customize how one or more {@link Organization}(s) are retrieved.
     */
    public static OrganizationOptions options() {
        return (OrganizationOptions) Classes.newInstance("com.stormpath.sdk.impl.organization.DefaultOrganizationOptions");
    }

    /**
     * Returns a new {@link OrganizationCriteria} instance to use to formulate an Organization query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Organizations.criteria().add(Organizations.name().eqIgnoreCase("Foo"))...
     * </pre>
     * versus:
     * <pre>
     * Organizations.where(Organizations.name().eqIgnoreCase("Foo"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(name().eqIgnoreCase("Foo"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link OrganizationCriteria} instance to use to formulate an Organization query.
     */
    public static OrganizationCriteria criteria() {
        return (OrganizationCriteria) Classes.newInstance("com.stormpath.sdk.impl.organization.DefaultOrganizationCriteria");
    }

    /**
     * Creates a new {@link OrganizationCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link OrganizationCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static OrganizationCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Organization {@link Organization#getName() name}
     * property, to be used to construct a name Criterion when building an {@link OrganizationCriteria} query.  For example:
     * <pre>
     * Organizations.where(<b>Organizations.name()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * OrganizationCriteria criteria = Organizations.criteria();
     * StringExpressionFactory nameExpressionFactory = Organizations.name();
     * Criterion nameStartsWithFoo = nameExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Organization#getName() name}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationCriteria} query.
     */
    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Organization {@link Organization#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link OrganizationCriteria} query.  For example:
     * <pre>
     * Organizations.where(<b>Organizations.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * OrganizationCriteria criteria = Organizations.criteria();
     * DateExpressionFactory createdAt = Organizations.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link Organization#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationCriteria} query.

     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Organization {@link Organization#getModifiedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link OrganizationCriteria} query.  For example:
     * <pre>
     * Organizations.where(<b>Organizations.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * OrganizationCriteria criteria = Organizations.criteria();
     * DateExpressionFactory createdAt = Organizations.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link Organization#getModifiedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationCriteria} query.
     */
    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Organization {@link Organization#getDescription() description}
     * property, to be used to construct a description Criterion when building an {@link OrganizationCriteria} query. For example:
     * <pre>
     * Organizations.where(<b>Organizations.description()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a description-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * OrganizationCriteria criteria = Organizations.criteria();
     * StringExpressionFactory descriptionExpressionFactory = Organizations.description();
     * Criterion descriptionStartsWithFoo = descriptionExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(descriptionStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Organization#getDescription() description}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationCriteria} query.
     */
    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Organization {@link Organization#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link OrganizationCriteria} query.  For example:
     * <pre>
     * Organizations.where(<b>Directories.status()</b>.eq(DirectoryStatus.ENABLED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * OrganizationCriteria criteria = Organizations.criteria();
     * StringExpressionFactory statusExpressionFactory = Organizations.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(DirectoryStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Organization#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    public static CreateOrganizationRequestBuilder newCreateRequestFor(Organization organization) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Organization.class);
        return (CreateOrganizationRequestBuilder) Classes.instantiate(ctor, organization);
    }

    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory) Classes.newInstance(FQCN, propName);
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
