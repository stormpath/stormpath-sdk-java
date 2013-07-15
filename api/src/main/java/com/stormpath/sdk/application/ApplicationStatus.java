package com.stormpath.sdk.application;

/**
 * @since 0.8
 */
public enum ApplicationStatus {

    /**
     * Accounts may login to enabled applications.
     */
    ENABLED,

    /**
     * Accounts may not login to disabled applications.
     */
    DISABLED,
}
