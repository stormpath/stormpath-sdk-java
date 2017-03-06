package com.stormpath.sdk.okta;

/**
 * An {@code UserStatus} represents the various states a user may be in.
 */
public enum UserStatus {

    STAGED,

    PROVISIONED,

    ACTIVE,

    RECOVERY,

    PASSWORD_EXPIRED,

    LOCKED_OUT,

    SUSPENDED,

    DEPROVISIONED
}