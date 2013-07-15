package com.stormpath.sdk.directory;

/**
 * @since 0.8
 */
public enum DirectoryStatus {

    /**
     * Accounts in enabled Directories may login to applications.
     */
    ENABLED,

    /**
     * Accounts in disabled Directories may not login to applications.
     */
    DISABLED,
}
