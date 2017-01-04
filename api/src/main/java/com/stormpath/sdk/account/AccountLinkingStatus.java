package com.stormpath.sdk.account;

/**
 * An {@code AccountLinkingStatus} represents whether Account Linking is ENABLED or DISABLED.
 *
 * @since 1.1.0
 */
public enum AccountLinkingStatus {

    /**
     * When ENABLED, the AccountLinking features (manual linking, automatic provisioning and linking)
     * are available for the Application/Organization
     * @see AccountLinkingPolicy
     */
    ENABLED,

    /**
     * When DISABLED, AccountLinking features (manual linking, automatic provisioning and linking)
     * are not available for the Application/Organization
     * @see AccountLinkingPolicy
     */
    DISABLED
}
