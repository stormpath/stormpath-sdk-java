package com.stormpath.sdk.group;

/**
 * @since 0.8
 */
public enum GroupStatus {

    /**
     * Accounts in enabled Groups mapped to applications may login to those applications.
     */
    ENABLED,

    /**
     * Accounts in disabled Groups mapped to applications may not login to those applications.
     */
    DISABLED,

}
