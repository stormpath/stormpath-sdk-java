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

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.google.CreateGoogleAuthenticatorFactorRequestBuilder;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactorOptions;
import com.stormpath.sdk.impl.factor.AbstractCreateFactorRequestBuilder;

/**
 * @since 1.1.0
 */
public class DefaultCreateGoogleAuthenticatorFactorRequestBuilder extends AbstractCreateFactorRequestBuilder<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> implements CreateGoogleAuthenticatorFactorRequestBuilder {

    private boolean createChallenge;

    public DefaultCreateGoogleAuthenticatorFactorRequestBuilder(GoogleAuthenticatorFactor factor) {
        super(factor);
    }

    @Override
    public DefaultCreateGoogleAuthenticatorFactorRequestBuilder createChallenge() {
        this.createChallenge = true;
        return this;
    }

    @Override
    public CreateFactorRequest<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> build() {
        return new DefaultGoogleAuthenticatorCreateFactorRequest(factor, options, createChallenge);
    }
}
