/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.group;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link Group} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Group-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. fpr example:
 * <pre>
 * <b>Groups.where(Groups.name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>Groups.status()</b>.eq(GroupStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.group.Groups.*;
 *
 * ...
 *
 * <b>where(name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>status()</b>.eq(GroupStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 0.8
 */
public final class Groups {

    private static final Class<CreateGroupRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.group.DefaultCreateGroupRequestBuilder");

    //prevent instantiation
    private Groups() {
    }

    /**
     * Returns a new {@link GroupOptions} instance, used to customize how one or more {@link Group}s are retrieved.
     *
     * @return a new {@link GroupOptions} instance, used to customize how one or more {@link Group}s are retrieved.
     */
    public static GroupOptions options() {
        return (GroupOptions) Classes.newInstance("com.stormpath.sdk.impl.group.DefaultGroupOptions");
    }

    /**
     * Returns a new {@link GroupCriteria} instance to use to formulate a Group query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Groups.criteria().add(Groups.name().eqIgnoreCase("Foo"))...
     * </pre>
     * versus:
     * <pre>
     * Groups.where(Groups.name().eqIgnoreCase("Foo"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(name().eqIgnoreCase("Foo"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link GroupCriteria} instance to use to formulate a Group query.
     */
    public static GroupCriteria criteria() {
        return (GroupCriteria) Classes.newInstance("com.stormpath.sdk.impl.group.DefaultGroupCriteria");
    }

    /**
     * Creates a new {@link GroupCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link GroupCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static GroupCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Group {@link Group#getName() name}
     * property, to be used to construct a name Criterion when building an {@link GroupCriteria} query.  For example:
     * <pre>
     * Groups.where(<b>Groups.name()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * GroupCriteria criteria = Groups.criteria();
     * StringExpressionFactory nameExpressionFactory = Groups.name();
     * Criterion nameStartsWithFoo = nameExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Group#getName() name}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link GroupCriteria} query.
     */
    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Group {@link Group#getDescription() description}
     * property, to be used to construct a description Criterion when building an {@link GroupCriteria} query.  For example:
     * <pre>
     * Groups.where(<b>Groups.description()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a description-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * GroupCriteria criteria = Groups.criteria();
     * StringExpressionFactory descriptionExpressionFactory = Groups.description();
     * Criterion descriptionStartsWithFoo = descriptionExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(descriptionStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Group#getDescription() description}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link GroupCriteria} query.
     */
    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Group {@link Group#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link GroupCriteria} query.  For example:
     * <pre>
     * Groups.where(<b>Groups.status()</b>.eq(GroupStatus.ENABLED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * GroupCriteria criteria = Groups.criteria();
     * StringExpressionFactory statusExpressionFactory = Groups.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(GroupStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Group#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link GroupCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * Creates a new {@link com.stormpath.sdk.group.CreateGroupRequestBuilder CreateGroupRequestBuilder}
     * instance reflecting the specified {@link com.stormpath.sdk.group.Group} instance.  The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param group the group to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.group.CreateGroupRequestBuilder CreateGroupRequestBuilder}
     *         instance reflecting the specified {@link com.stormpath.sdk.group.Group} instance.
     * @see com.stormpath.sdk.application.Application#createGroup(CreateGroupRequest)
     * @since 0.9
     */
    public static CreateGroupRequestBuilder newCreateRequestFor(Group group) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Group.class);
        return (CreateGroupRequestBuilder) Classes.instantiate(ctor, group);
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
