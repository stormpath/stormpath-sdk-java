/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.challenge;

/**
 * An {@code ChallengeStatus} represents the various states a challenge may be in.
 *
 * @since 1.1.0
 */
public enum ChallengeStatus {
    /**
     * The challenge was created.
     */
    CREATED,

    /**
     * A request has been sent to SMS provider to send an SMS to user and challenge the authentication.
     */
    WAITING_FOR_PROVIDER,

    /**
     * The challenge has been issued to the user, and we are awaiting a response.
     */
    WAITING_FOR_VALIDATION,

    /**
     * The challenge was successfully verified.
     */
    SUCCESS,

    /**
     * An attempt was made to verify the challenge, but it failed, for example by specifying an incorrect code.
     */
    FAILED,

    /**
     * The challenge was explicitly denied by the user.
     */
    DENIED,

    /**
     * The user chose not to reply to the challenge.
     */
    CANCELLED,

    /**
     * The challenge was not verified within the allowed time window.
     */
    EXPIRED,

    /**
     * Unexpected internal server error.
     */
    ERROR,

    /**
     * SMS provider sent the message (and charged us) but did not get a successful delivery notification.
     */
    UNDELIVERED
}
