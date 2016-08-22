package com.stormpath.sdk.account;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;

/**
 * Static utility/helper methods for working with {@link AccountLinks} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * AccountLink-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>AccountLinks.where(AccountLinks.createdAt()</b>.equals("2016-01-01")<b>)</b>
 *     .orderByCreatedAt().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.account.AccountLinks.*;
 *
 * ...
 *
 * <b>where(createdAt()</b>.equals("2016-01-01")<b>)</b>
 *     .orderByCreatedAt().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 *
 */
public final class AccountLinks {

    /**
     * Returns a new {@link AccountLinkOptions} instance, used to customize how one or more {@link AccountLink}s are retrieved.
     *
     * @return a new {@link AccountLinkOptions} instance, used to customize how one or more {@link AccountLink}s are retrieved.
     */
    @SuppressWarnings("unchecked")
    public static AccountLinkOptions<AccountLinkOptions> options() {
        return (AccountLinkOptions) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountLinkOptions");
    }

    /**
     * Returns a new {@link AccountLinkCriteria} instance to use to formulate an AccountLink query.
     * <p/>
     * Note that it is usually more common to use the {@link #where(com.stormpath.sdk.query.Criterion) where} method
     * instead of this one as the {@code where} method usually lends to better readability.  For example:
     * <pre>
     * AccountLinks.criteria().add(AccountLinks.createdAt().eq("2016-01-01"))...
     * </pre>
     * versus:
     * <pre>
     * AccountLinks.where(AccountLinks.createdAt().eq("2016-01-01"))...
     * </pre>
     * or when using static imports:
     * <pre>
     * where(createdAt().eq("2016-01-01"))...
     * </pre>
     * While all three statements are equivalent, the second and third examples are shorter and probably more readable.
     *
     * @return a new {@link AccountLinkCriteria} instance to use to formulate an AccountLink query.
     */
    public static AccountLinkCriteria criteria() {
        return (AccountLinkCriteria) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountLinkCriteria");
    }

    /**
     * Creates a new {@link AccountLinkCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link AccountLinkCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static AccountLinkCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link DateExpressionFactory} instance reflecting the AccountLink {@link AccountLink#getCreatedAt() createdAt}
     * property, to be used to construct a createdAt Criterion when building an {@link AccountLinkCriteria} query.  For example:
     * <pre>
     * AccountsLinks.where(<b>AccountsLinks.createdAt()</b>.matches("[,2014-04-05T12:00:00]");
     * </pre>
     * The above example invokes the returned factory's <code>matches("[,2014-04-05T12:00:00]"))</code> method.  This
     * produces a name-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(Criterion) where} method).
     * <pre>
     * For example, the following code is equivalent:
     * <pre>
     * AccountsLinkCriteria criteria = AccountLinks.criteria();
     * DateExpressionFactory createdAt = AccountLinks.createdAt();
     * Criterion createdAtMatches = createdAt.matches("[,2014-04-05T12:00:00]");
     * criteria.add(createdAtMatches);
     * </pre>
     *
     * @return a new {@link AccountLink#getCreatedAt() createdAt}-specific {@link DateExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link AccountLinkCriteria} query.
     * @since 1.0.RC4.6
     */
    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }
}
