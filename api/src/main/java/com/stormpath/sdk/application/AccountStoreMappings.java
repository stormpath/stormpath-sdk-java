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
package com.stormpath.sdk.application;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;

/**
 * Static utility/helper methods for working with {@link AccountStoreMapping} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * AccountStoreMapping-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>AccountStoreMappings.criteria()</b>
 *     .withApplication()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or
 * <pre>
 * <b>AccountStoreMappings.where(AccountStoreMappings.listIndex()</b>.eq(3))
 *     .withAccountStore()
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.account.AccountStoreMappings.*;
 *
 * ...
 *     criteria()
 *     .withApplication()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or
 * <pre>
 * import static com.stormpath.sdk.account.AccountStoreMappings.*;
 *
 * ...
 *
 * <b>where(listIndex()</b>.eq(3))
 *     .withAccountStore()
 * </pre>
 * 
 * 
 * @since 0.9
 */
public final class AccountStoreMappings {

    /**
     * Returns a new {@link AccountStoreMappingOptions} instance, used to customize how one or more {@link AccountStoreMapping}s are retrieved.
     *
     * @return a new {@link AccountStoreMappingOptions} instance, used to customize how one or more {@link AccountStoreMapping}s are retrieved.
     */
    public static AccountStoreMappingOptions options() {
        return (AccountStoreMappingOptions) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultAccountStoreMappingOptions");
    }

    /**
     * Returns a new {@link AccountStoreMappingCriteria} instance to use to formulate an Account query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * AccountStoreMappings.criteria().add(AccountStoreMappings.listIndex().eq(8))...
     * </pre>
     * versus:
     * <pre>
     * AccountStoreMappings.where(AccountStoreMappings.listIndex().eq(8))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(listIndex().eq(8))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link AccountStoreMappingCriteria} instance to use to formulate an AccountStoreMapping query.
     */
    public static AccountStoreMappingCriteria criteria() {
        return (AccountStoreMappingCriteria) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultAccountStoreMappingCriteria");
    }

    /**
     * Creates a new {@link AccountStoreMappingCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link AccountStoreMappingCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static AccountStoreMappingCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the AccountStoreMapping {@link AccountStoreMapping#getListIndex() listIndex}
     * property, to be used to construct a listIndex Criterion when building an {@link AccountStoreMappingCriteria} query.  For example:
     * <pre>
     * AccountStoreMappings.where(<b>AccountStoreMappings.listIndex()</b>.eq(12);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a listIndex-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * AccountStoreMappingCriteria criteria = AccountStoreMappings.criteria();
     * EqualsExpressionFactory listIndexExpressionFactory = AccountStoreMappings.listIndex();
     * Criterion listIndexEqualsTen = listIndexExpressionFactory.eq(10);
     * criteria.add(listIndexEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link AccountStoreMapping#getListIndex() listIndex}-specific {@link EqualsExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link AccountStoreMappingCriteria} query.
     */
    public static EqualsExpressionFactory listIndex() {
        return newEqualsExpressionFactory("listIndex");
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}
