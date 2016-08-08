package com.stormpath.sdk.account;

import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;

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
 */
public final class AccountLinks {

    @SuppressWarnings("unchecked")
    private static final Class<CreateAccountLinkRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.account.DefaultCreateAccountLinkRequestBuilder");

    /**
     * Creates a new {@link com.stormpath.sdk.account.CreateAccountLinkRequestBuilder CreateAccountLinkRequestBuilder}
     * instance reflecting the specified {@link AccountLink} instance.  The builder can be used to customize any
     * creation request options as necessary.
     *
     * @param accountLink the accountLink to create a new record for within Stormpath
     * @return a new {@link com.stormpath.sdk.account.CreateAccountLinkRequestBuilder CreateAccountLinkRequestBuilder}
     *         instance reflecting the specified {@link AccountLink} instance.
     * @see //TODO
     * @since //TODO
     */
    public static CreateAccountLinkRequestBuilder newCreateRequestFor(AccountLink accountLink) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, AccountLink.class);
        return (CreateAccountLinkRequestBuilder) Classes.instantiate(ctor, accountLink);
    }
}
