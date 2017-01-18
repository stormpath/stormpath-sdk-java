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
package com.stormpath.sdk.impl.challenge.google;

import com.stormpath.sdk.challenge.google.GoogleAuthenticatorCreateChallengeRequest;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorCreateChallengeRequestBuilder;
import com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequestBuilder;

public class DefaultGoogleAuthenticatorCreateChallengeRequestBuilder extends DefaultCreateChallengeRequestBuilder<GoogleAuthenticatorChallenge> implements GoogleAuthenticatorCreateChallengeRequestBuilder {

    public DefaultGoogleAuthenticatorCreateChallengeRequestBuilder(GoogleAuthenticatorChallenge challenge) {
        super(challenge);
    }

    @Override
    public GoogleAuthenticatorCreateChallengeRequestBuilder withCode(String code) {
        challenge.setCode(code);
        return this;
    }

    @Override
    public GoogleAuthenticatorCreateChallengeRequest build() {
        return new DefaultGoogleAuthenticatorCreateChallengeRequest(challenge, options);
    }
}
