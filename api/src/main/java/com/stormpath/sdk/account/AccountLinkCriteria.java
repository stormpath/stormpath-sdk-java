package com.stormpath.sdk.account;

import com.stormpath.sdk.query.Criteria;

/**
 * An {@link Account}-specific {@link Criteria} class, enabling an Account-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL.  AccountCriteria instances can be
 * constructed by using the {@link Accounts} utility class, for example:
 * <pre>
 * <b>AccountLinks.where(AccountLinks.createdAt()</b>.equals("2016-01-01")<b>)</b>
 *     .orderByCreatedAt().descending()
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
 * ...criteria.orderByCreatedAt()<b>.descending()</b>...
 *
 * @since 1.1.0
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
    AccountLinkCriteria orderByCreatedAt();
}
