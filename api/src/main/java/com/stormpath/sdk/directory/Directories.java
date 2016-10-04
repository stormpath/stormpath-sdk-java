/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link Directory} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Directory-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Directories.where(Directories.name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>Directories.status()</b>.eq(DirectoryStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.directory.Directories.*;
 *
 * ...
 *
 * <b>where(name()</b>.containsIgnoreCase("Foo")<b>)</b>
 *     .and(<b>status()</b>.eq(DirectoryStatus.ENABLED))
 *     .orderByName().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 0.8
 */
public final class Directories {

    private static final Class<CreateDirectoryRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.directory.DefaultCreateDirectoryRequestBuilder");

    /**
     * Returns a new {@link DirectoryOptions} instance, used to customize how one or more {@link Directory}(ies) are retrieved.
     *
     * @return a new {@link DirectoryOptions} instance, used to customize how one or more {@link Directory}(ies) are retrieved.
     */
    public static DirectoryOptions options() {
        return (DirectoryOptions) Classes.newInstance("com.stormpath.sdk.impl.directory.DefaultDirectoryOptions");
    }

    /**
     * Returns a new {@link DirectoryCriteria} instance to use to formulate a Directory query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * Directories.criteria().add(Directories.name().eqIgnoreCase("Foo"))...
     * </pre>
     * versus:
     * <pre>
     * Directories.where(Directories.name().eqIgnoreCase("Foo"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(name().eqIgnoreCase("Foo"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link DirectoryCriteria} instance to use to formulate a Directory query.
     */
    public static DirectoryCriteria criteria() {
        return (DirectoryCriteria) Classes.newInstance("com.stormpath.sdk.impl.directory.DefaultDirectoryCriteria");
    }

    /**
     * Creates a new {@link DirectoryCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link DirectoryCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static DirectoryCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Directory {@link Directory#getName() name}
     * property, to be used to construct a name Criterion when building an {@link DirectoryCriteria} query.  For example:
     * <pre>
     * Directories.where(<b>Directories.name()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * DirectoryCriteria criteria = Directories.criteria();
     * StringExpressionFactory nameExpressionFactory = Directories.name();
     * Criterion nameStartsWithFoo = nameExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(nameStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Directory#getName() name}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link DirectoryCriteria} query.
     */
    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Directory {@link Directory#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link DirectoryCriteria} query.  For example:
     * <pre>
     * Directories.where(<b>Directories.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * DirectoryCriteria criteria = Directories.criteria();
     * DateExpressionFactory createdAt = Directories.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link Directory#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link DirectoryCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the Directory {@link Directory#getModifiedAt() modifiedAt}
     * property, to be used to construct a modifiedAt Criterion when building an {@link DirectoryCriteria} query.  For example:
     * <pre>
     * Directories.where(<b>Directories.modifiedAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * DirectoryCriteria criteria = Directories.criteria();
     * DateExpressionFactory createdAt = Directories.modifiedAt();
     * Criterion modifiedAtMatches = modifiedAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(modifiedAtMatches);
     * </pre>
     *
     * @return a new {@link Directory#getModifiedAt() modifiedAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link DirectoryCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    /**
     * Creates a new {@link StringExpressionFactory} instance reflecting the Directory {@link Directory#getDescription() description}
     * property, to be used to construct a description Criterion when building an {@link DirectoryCriteria} query.  For example:
     * <pre>
     * Directories.where(<b>Directories.description()</b>.startsWithIgnoreCase("foo");
     * </pre>
     * The above example invokes the returned factory's <code>startsWithIgnoreCase("foo")</code> method.  This
     * produces a description-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * DirectoryCriteria criteria = Directories.criteria();
     * StringExpressionFactory descriptionExpressionFactory = Directories.description();
     * Criterion descriptionStartsWithFoo = descriptionExpressionFactory.startsWithIgnoreCase("foo");
     * criteria.add(descriptionStartsWithFoo);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Directory#getDescription() description}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link DirectoryCriteria} query.
     */
    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the Directory {@link Directory#getStatus() status}
     * property, to be used to construct a status Criterion when building an {@link DirectoryCriteria} query.  For example:
     * <pre>
     * Directories.where(<b>Directories.status()</b>.eq(DirectoryStatus.ENABLED);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * DirectoryCriteria criteria = Directories.criteria();
     * StringExpressionFactory statusExpressionFactory = Directories.status();
     * Criterion statusEqualsEnabled = statusExpressionFactory.eq(DirectoryStatus.ENABLED);
     * criteria.add(statusEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link Directory#getStatus() status}-specific {@link StringExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link DirectoryCriteria} query.
     */
    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    /**
     * This operation will ask the backend to do a case-insensitive matching query on all viewable attributes in all the resources in the Collection.
     *
     * So the following query:
     *
     * tenant.getDirectories(Directories.where(Directories.filter("FilterDirectories"));
     *
     * Returns all Directories where:
     *
     * Each Directory exists inside an DirectoryStore belonging to the Tenant `tenant`
     * The Directories’s name equals or contains “FilterDirectories” (case insensitive) OR
     * The Directories’s description equals or contains “FilterDirectories” (case insensitive) OR
     * And so on
     *
     * @param value The value to search for
     * @return A Criterion representing the filter query
     * @since 1.1.0
     */
    public static Criterion filter(String value){
        return newStringExpressionFactory("q").eqIgnoreCase(value);
    }

    public static CreateDirectoryRequestBuilder newCreateRequestFor(Directory directory) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Directory.class);
        return (CreateDirectoryRequestBuilder) Classes.instantiate(ctor, directory);
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
