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
package com.stormpath.sdk.challenge.google;

/**
 * An {@code GoogleAuthenticatorChallengeStatus} represents the various states a GoogleAuthenticatorChallenge may be in.
 *
 * @since 1.1.0
 */
public enum GoogleAuthenticatorChallengeStatus {
    /**
     * The challenge was created.
     */
    CREATED,

    /**
     * The challenge was successfully verified.
     */
    SUCCESS,

    /**
     * An attempt was made to verify the challenge, but it failed, for example by specifying an incorrect code.
     */
    FAILED,
}
