package com.stormpath.sdk.account;

/**
 * Implemented by any account store type that has an AccountLinkingPolicy.
 *
 * @since 1.1.0
 */
public interface AccountLinker {

    /**
     * Returns the {@link AccountLinkingPolicy accountLinkingPolicy} of
     * the {@link com.stormpath.sdk.directory.AccountStore accountStore}
     *
     * @return the {@link AccountLinkingPolicy accountLinkingPolicy} of
     * the {@link com.stormpath.sdk.directory.AccountStore accountStore}
     */
    AccountLinkingPolicy getAccountLinkingPolicy();

}
