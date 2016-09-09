package com.stormpath.sdk.challenge;

/**
 * Created by mehrshadrafiei on 8/31/16.
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
