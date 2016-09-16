package com.stormpath.sdk.account;

import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

/**
 * An AccountLinkingPolicy resource is used to configure different aspects of account linking associated
 * with an {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
 *
 * @since 1.1.0
 */
public interface AccountLinkingPolicy extends Resource, Saveable {

    /**
     * Returns the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     */
    AccountLinkingStatus getStatus();

   /**
     * Sets the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * @param status - Possible values are : ENABLED, DISABLED.
     * @return this instance for method chaining.
     */
    AccountLinkingPolicy setStatus(AccountLinkingStatus status);

    /**
     * Returns the automaticProvisioning status of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return the automaticProvisioningStatus of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     */
    AutomaticProvisioningStatus getAutomaticProvisioning();

    /**
     * Sets the automaticProvisioningStatus of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return this instance for method chaining.
     */
    AccountLinkingPolicy setAutomaticProvisioning(AutomaticProvisioningStatus automaticProvisioningStatus);

    /**
     * Returns the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : "email", null. null by default.
     *
     * @return the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     */
    String getMatchingProperty();

    /**
     * Sets the the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : "email", null. null by default.
     *
     * @return this instance for method chaining.
     * @param matchingProperty - Possible values are : "email", null. null by default.
     */
    AccountLinkingPolicy setMatchingProperty(String matchingProperty);

    /**
     * Returns the parent {@link Tenant Tenant} of the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * associated to this {@link AccountLinkingPolicy accountLinkingPolicy}
     *
     * @return the parent {@link Tenant Tenant} of the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * associated to this {@link AccountLinkingPolicy accountLinkingPolicy}
     */
    Tenant getTenant();

    /**
     * Returns true if the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * is {@link AccountLinkingStatus#ENABLED ENABLED}, false otherwise
     * @return true if the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * is {@link AccountLinkingStatus#ENABLED ENABLED}, false otherwise
     */
    boolean isAccountLinkingEnabled ();

    /**
     * Returns true if the automaticProvisioningStatus of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * is {@link AutomaticProvisioningStatus#ENABLED ENABLED}, false otherwise
     * @return true if the automaticProvisioningStatus of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}
     * is {@link AutomaticProvisioningStatus#ENABLED ENABLED}, false otherwise
     */
    boolean isAutomaticProvisioningEnabled ();
}
