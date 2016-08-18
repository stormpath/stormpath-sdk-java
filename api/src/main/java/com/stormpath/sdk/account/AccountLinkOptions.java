package com.stormpath.sdk.account;

import com.stormpath.sdk.query.Options;

/**
 * AccountLink-specific options that may be specified when retrieving {@link AccountLink} resources.
 *
 * @since 1.1.0
 */
public interface AccountLinkOptions<T> extends Options {

    /**
     * Ensures that when retrieving an AccountLink, the Account's {@link AccountLink#getLeftAccount() leftAccount} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withLeftAccount();

    /**
     * Ensures that when retrieving an Account, the Account's parent {@link AccountLink#getRightAccount() rightAccount} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withRightAccount();

}

