package com.stormpath.sdk.account;

/**
 * Static utility/helper methods for working with {@link Account} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Account-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>Accounts.where(Accounts.surname()</b>.containsIgnoreCase("Smith")<b>)</b>
 *     .and(<b>Accounts.givenName()</b>.eqIgnoreCase("John"))
 *     .orderBySurname().descending()
 *     .withGroups(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.account.Accounts.*;
 *
 * ...
 *
 * <b>where(surname()</b>.containsIgnoreCase("Smith")<b>)</b>
 *     .and(<b>givenName()</b>.eqIgnoreCase("John"))
 *     .orderBySurname().descending()
 *     .withGroups(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 */
public final class AccountLinks {
}
