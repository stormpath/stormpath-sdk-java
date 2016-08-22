package com.stormpath.sdk.account;

import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

/**
 * An AccountLinkingPolicy resource is used to configure different aspects of the AccountLinking associated
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
    String getStatus();

    /**
     * Sets the status of the accountLinkingPolicy for the {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return this instance for method chaining.
     */
    AccountLinkingPolicy setStatus(String status);

    /**
     * Returns the automaticProvisioning status of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return the automaticProvisioning status of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     */
    String getAutomaticProvisioning();

    /**
     * Sets the automaticProvisioning status of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : ENABLED, DISABLED.
     *
     * @return this instance for method chaining.
     */
    AccountLinkingPolicy setAutomaticProvisioning(String automaticProvisioningStatus);

    /**
     * Returns the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : EMAIL, USERNAME. null by default.
     *
     * @return the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : EMAIL, USERNAME.
     */
    String getMatchingProperty();

    /**
     * Sets the the matchingProperty of the accountLinkingPolicy for the
     * {@link com.stormpath.sdk.application.AccountStoreHolder accountStoreHolder}.
     * Possible values are : EMAIL, USERNAME. null by default.
     *
     * @return this instance for method chaining.
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
}
