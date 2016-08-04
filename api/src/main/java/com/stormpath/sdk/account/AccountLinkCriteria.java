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
 *     .withGroups(10, 10)
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
public interface AccountLinkCriteria extends Criteria<AccountLinkCriteria>, AccountLinkOptions<AccountLinkCriteria> {

    /**
     * Ensures that the query results are ordered by createdAt Date {@link AccountLink#getCreatedAt() createdAt}.
     * <p/>
     * Please see the {@link AccountLinkCriteria class-level documentation} for controlling sort order (ascending or
     * descending).
     *
     * @return this instance for method chaining
     */
    AccountCriteria orderByCreatedAt();
}
