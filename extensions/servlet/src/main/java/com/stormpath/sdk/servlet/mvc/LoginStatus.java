package com.stormpath.sdk.servlet.mvc;

/**
 * A {@code LoginStatus} represents the various status messages that can result from a login.
 *
 * @since 1.0.3
 */
public enum LoginStatus {

    UNVERIFIED,
    VERIFIED,
    CREATED,
    FORGOT,
    RESET
}
