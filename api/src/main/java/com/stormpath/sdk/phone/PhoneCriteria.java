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

import com.stormpath.sdk.query.Criteria;

/**
 * A {@link Phone}-specific {@link Criteria} class, enabling a Phone-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL. PhoneCriteria instances can be
 * constructed by using the {@link Phones} utility class, for example:
 * <pre>
 * Phones.where(Phones.name().containsIgnoreCase("my phone"))
 *     .and(Phones.status().eq(PhoneStatus.ENABLED))
 *     .orderByName()
 *     .limitTo(10));
 * </pre>
 * <h2>Sort Order</h2>
 * <p/>
 * All of the {@code orderBy*} methods append an {@code orderBy} clause to the query, ensuring the query results reflect
 * a particular sort order.
 * <p/>
 * The default sort order is always {@code ascending}, but can be changed to {@code descending} by calling the
 * {@link #descending()} method <em>immediately</em> after the {@code orderBy} method call.  For example:
 * <pre>
 * ...criteria.orderByName()<b>.descending()</b>...
 * </pre>
 * <h3>Multiple Order Statements</h3>
 * You may specify multiple {@code orderBy} clauses and the query results will ordered, reflecting {@code orderBy}
 * statements <em>in the order they are declared</em>.  For example, to order the results first by name (ascending)
 * and then further by status (descending), you would chain {@code orderBy} statements:
 * <pre>
 * ...criteria
 *     .orderByName()
 *     .orderByStatus().descending()
 *     ...
 * </pre>
 *
 * @since 1.1.0
 */
public interface PhoneCriteria extends Criteria<PhoneCriteria>,  PhoneOptions<PhoneCriteria>{

    /**
     * Ensures that the query results are ordered by group {@link Phone#getName() name}.
     * <p/>
     * Please see the {@link PhoneCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    PhoneCriteria orderByName();

    /**
     * Ensures that the query results are ordered by group {@link Phone#getDescription() description}.
     * <p/>
     * Please see the {@link PhoneCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    PhoneCriteria orderByDescription();

    /**
     * Ensures that the query results are ordered by group {@link Phone#getStatus() status}.
     * <p/>
     * Please see the {@link PhoneCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    PhoneCriteria orderByStatus();

    /**
     * Ensures that the query results are ordered by group {@link Phone#getVerificationStatus()} verificationStatus}.
     * <p/>
     * Please see the {@link PhoneCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    PhoneCriteria orderByVerificationStatus();

}
