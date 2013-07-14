package com.stormpath.sdk.account;

/**
 * An {@code AccountStatus} represents the various states an account may be in.
 *
 * @since 0.8
 */
public enum AccountStatus {

    /**
     * An enabled account may login to applications.
     */
    ENABLED,

    /**
     * A disabled account may not login to applications.
     */
    DISABLED,

    /**
     * An unverified account is a disabled account that does not have a verified email address.
     */
    UNVERIFIED
}
