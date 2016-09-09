package com.stormpath.sdk.challenge;

/**
 * todo: mehrshad
 */
public enum ChallengeStatus {
    CREATED,
    WAITING_FOR_PROVIDER,
    WAITING_FOR_VALIDATION,
    SUCCESS,
    FAILED,
    DENIED,
    CANCELLED,
    EXPIRED,
    ERROR,
    UNDELIVERED
}
