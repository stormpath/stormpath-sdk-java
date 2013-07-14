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
package com.stormpath.sdk.account;

import com.stormpath.sdk.query.Criteria;

/**
 * An {@link Account}-specific {@link Criteria} class, enabling an Account-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL.  AccountCriteria instances can be
 * constructed by using the {@link Accounts} utility class, for example:
 * <pre>
 * Accounts.where(Accounts.surname().containsIgnoreCase("Smith"))
 *     .and(Accounts.givenName().eqIgnoreCase("John"))
 *     .orderBySurname().descending()
 *     .expandGroups(10, 10)
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
 * ...criteria.orderByEmail()<b>.descending()</b>...
 * </pre>
 * <h3>Multiple Order Statements</h3>
 * You may specify multiple {@code orderBy} clauses and the query results will ordered, reflecting {@code orderBy}
 * statements <em>in the order they are declared</em>.  For example, to order the results first by email (ascending)
 * and then further by surname (descending), you would chain {@code orderBy} statements:
 * <pre>
 * ...criteria
 *     .orderByEmail()
 *     .orderBySurname().descending()
 *     ...
 * </pre>
 *
 * @since 0.8
 */
public interface AccountCriteria extends Criteria<AccountCriteria>, AccountOptions<AccountCriteria> {

    /**
     * Ensures that the query results are ordered by account {@link Account#getEmail() email}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByEmail();

    /**
     * Ensures that the query results are ordered by account {@link Account#getUsername() username}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByUsername();

    /**
     * Ensures that the query results are ordered by account {@link Account#getGivenName() givenName}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByGivenName();

    /**
     * Ensures that the query results are ordered by account {@link Account#getMiddleName() middleName}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByMiddleName();

    /**
     * Ensures that the query results are ordered by account {@link Account#getSurname() surname}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderBySurname();

    /**
     * Ensures that the query results are ordered by account {@link Account#getStatus() status}.
     * <p/>
     * Please see the {@link AccountCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByStatus();
}
