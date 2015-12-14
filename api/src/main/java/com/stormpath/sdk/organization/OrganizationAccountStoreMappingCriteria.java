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

import com.stormpath.sdk.query.Criteria;

/**
 * An {@link OrganizationAccountStoreMapping}-specific {@link com.stormpath.sdk.query.Criteria} class, enabling an OrganizationAccountStoreMapping-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL. OrganizationAccountStoreMappingCriteria instances can be
 * constructed by using the {@link OrganizationAccountStoreMappings} utility class, for example:
 * <pre>
 * OrganizationAccountStoreMappings.where(OrganizationAccountStoreMappings.listIndex().eq(4))
 *     .withAccountStore()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * <h2>Sort Order</h2>
 * <p/>
 * All of the {@code orderBy*} methods append an {@code orderBy} clause to the query, ensuring the query results reflect
 * a particular sort order.
 * <p/>
 * The default sort order is always {@code ascending}, but can be changed to {@code descending} by calling the
 * {@link #descending()} method <em>immediately</em> after the {@code orderBy} method call.  For example:
 * <pre>
 * ...criteria().orderByListIndex()<b>.descending()</b>...
 * </pre>
 *
 * @since 1.0.RC7
 */
public interface OrganizationAccountStoreMappingCriteria extends Criteria<OrganizationAccountStoreMappingCriteria>, OrganizationAccountStoreMappingOptions<OrganizationAccountStoreMappingCriteria>{

    /**
     * Ensures that the query results are ordered by {@link OrganizationAccountStoreMapping#getListIndex() listIndex}.
     * <p/>
     * Please see the {@link OrganizationAccountStoreMappingCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    OrganizationAccountStoreMappingCriteria orderByListIndex();
}
