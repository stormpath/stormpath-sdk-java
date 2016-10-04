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
package com.stormpath.sdk.impl.factor.google;

import com.stormpath.sdk.factor.google.CreateGoogleAuthenticatorFactorRequest;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactorOptions;
import com.stormpath.sdk.impl.factor.AbstractCreateFactorRequest;

/**
 * @since 1.1.0
 */
public class DefaultGoogleAuthenticatorCreateFactorRequest extends AbstractCreateFactorRequest<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> implements CreateGoogleAuthenticatorFactorRequest {

    private final boolean createChallenge;

    public DefaultGoogleAuthenticatorCreateFactorRequest(GoogleAuthenticatorFactor factor, GoogleAuthenticatorFactorOptions options, boolean createChallenge) {
        super(factor, options);
        this.createChallenge = createChallenge;
    }
}
