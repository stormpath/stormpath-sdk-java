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
