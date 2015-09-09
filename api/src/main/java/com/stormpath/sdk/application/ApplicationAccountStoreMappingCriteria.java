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

import com.stormpath.sdk.query.Criteria;

/**
 * An {@link AccountStoreMapping}-specific {@link Criteria} class, enabling an AccountStoreMapping-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL.  ApplicationAccountStoreMappingCriteria instances can be
 * constructed by using the {@link ApplicationAccountStoreMappings} utility class, for example:
 * <pre>
 * ApplicationAccountStoreMappings.where(ApplicationAccountStoreMappings.listIndex().eq(4))
 *     .orderByListIndex().descending()
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
 * @since 0.9
 */
public interface ApplicationAccountStoreMappingCriteria extends Criteria<ApplicationAccountStoreMappingCriteria>, ApplicationAccountStoreMappingOptions<ApplicationAccountStoreMappingCriteria> {

    /**
     * Ensures that the query results are ordered by {@link AccountStoreMapping#getListIndex() listIndex}.
     * <p/>
     * Please see the {@link ApplicationAccountStoreMappingCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    ApplicationAccountStoreMappingCriteria orderByListIndex();
}
