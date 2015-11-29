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
import com.stormpath.sdk.query.EqualsExpressionFactory;

/**
 * Static utility/helper methods for working with {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * OrganizationAccountStoreMapping-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>OrganizationAccountStoreMappings.criteria()</b>
 *     .withOrganization()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or
 * <pre>
 * <b>OrganizationAccountStoreMappings.where(OrganizationAccountStoreMappings.listIndex()</b>.eq(3))
 *     .withAccountStore()
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.organization.OrganizationAccountStoreMappings.*;
 *
 * ...
 *     criteria()
 *     .withOrganization()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or
 * <pre>
 * import static com.stormpath.sdk.organization.OrganizationAccountStoreMappings.*;
 *
 * ...
 *
 * <b>where(listIndex()</b>.eq(3))
 *     .withAccountStore()
 * </pre>
 *
 *
 * @since 1.0.RC7
 */
public final class OrganizationAccountStoreMappings {

    /**
     * Returns a new {@link OrganizationAccountStoreMappingOptions} instance, used to customize how one or more {@link OrganizationAccountStoreMapping}s are retrieved.
     *
     * @return a new {@link OrganizationAccountStoreMappingOptions} instance, used to customize how one or more {@link OrganizationAccountStoreMapping}s are retrieved.
     */
    public static OrganizationAccountStoreMappingOptions options() {
        return (OrganizationAccountStoreMappingOptions) Classes.newInstance("com.stormpath.sdk.impl.organization.DefaultOrganizationAccountStoreMappingOptions");
    }

    /**
     * Returns a new {@link OrganizationAccountStoreMappingCriteria} instance to use to formulate a query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * OrganizationAccountStoreMappings.criteria().add(OrganizationAccountStoreMappings.listIndex().eq(8))...
     * </pre>
     * versus:
     * <pre>
     * OrganizationAccountStoreMappings.where(OrganizationAccountStoreMappings.listIndex().eq(8))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(listIndex().eq(8))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link OrganizationAccountStoreMappingCriteria} instance to use to formulate an OrganizationAccountStoreMapping query.
     */
    public static OrganizationAccountStoreMappingCriteria criteria() {
        return (OrganizationAccountStoreMappingCriteria) Classes.newInstance("com.stormpath.sdk.impl.organization.DefaultOrganizationAccountStoreMappingCriteria");
    }

    /**
     * Creates a new {@link OrganizationAccountStoreMappingCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link OrganizationAccountStoreMappingCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static OrganizationAccountStoreMappingCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the OrganizationAccountStoreMapping {@link OrganizationAccountStoreMapping#getListIndex() listIndex}
     * property, to be used to construct a listIndex Criterion when building an {@link OrganizationAccountStoreMappingCriteria} query.  For example:
     * <pre>
     * OrganizationAccountStoreMappings.where(<b>OrganizationAccountStoreMappings.listIndex()</b>.eq(12);
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a listIndex-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * OrganizationAccountStoreMappingCriteria criteria = OrganizationAccountStoreMappings.criteria();
     * EqualsExpressionFactory listIndexExpressionFactory = OrganizationAccountStoreMappings.listIndex();
     * Criterion listIndexEqualsTen = listIndexExpressionFactory.eq(10);
     * criteria.add(listIndexEqualsEnabled);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link OrganizationAccountStoreMapping#getListIndex() listIndex}-specific {@link EqualsExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link OrganizationAccountStoreMappingCriteria} query.
     */
    public static EqualsExpressionFactory listIndex() {
        return newEqualsExpressionFactory("listIndex");
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}