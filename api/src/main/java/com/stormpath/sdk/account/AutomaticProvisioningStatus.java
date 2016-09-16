package com.stormpath.sdk.account;

/**
 * An {@code AutomaticProvisioningStatus} represents whether to automatically
 * provision/create an account in the default Account Store, and link it with
 * the current account (used during login), when AccountLinking is enabled
 * for the Application/Organization.
 *
 * This takes effect only if the associated {@link AccountLinkingStatus accountLinkingStatus} is
 * {@link AccountLinkingStatus#ENABLED ENABLED} and when the current account (used during login)
 * is not already linked to an {@link Account) in the default Account Store of the Application/Organization.
 * When, AccountLinkingStatus is {@link AccountLinkingStatus#DISABLED DISABLED}, then the value
 * of AutomaticProvisioningStatus has no effect.
 *
 * @since 1.1.0
 */
public enum AutomaticProvisioningStatus {

    /**
     * When ENABLED, an account will be automatically provisioned/created
     * in the default Account Store of the Application/Organization.
     *
     * This takes effect only when AccountLinkingStatus is {@link AccountLinkingStatus#ENABLED ENABLED}
     * and if current account (used during login) is not already linked to an {@link Account) in the
     * default Account Store of the Application/Organization.
     * @see AccountLinkingPolicy
     */
    ENABLED,

    /**
     * When DISABLED, Automatic Provisioning and Linking will not take place.
     * Accounts will have to be linked {@link Account#link(Account) manually}, when automaticProvisioning is DISABLED.
     * @see AccountLinkingPolicy
     */
    DISABLED

}
