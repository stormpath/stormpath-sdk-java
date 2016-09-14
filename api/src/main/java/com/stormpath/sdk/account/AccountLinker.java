package com.stormpath.sdk.account;

/**
 * Any AccountStoreMappable that can have an AccountLinkingPolicy
 * should implement this interface to be able to get/update its
 * AccountLinkingPolicy. e.g. Applications, Organizations can have
 * a AccountLinkingPolicy and therefore should implement this interface.
 *
 * @since 1.1.0
 */
public interface AccountLinker {

    AccountLinkingPolicy getAccountLinkingPolicy();

}
