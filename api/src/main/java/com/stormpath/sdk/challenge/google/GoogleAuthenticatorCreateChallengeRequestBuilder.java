package com.stormpath.sdk.challenge.google;

import com.stormpath.sdk.challenge.CreateChallengeRequestBuilder;

public interface GoogleAuthenticatorCreateChallengeRequestBuilder  extends CreateChallengeRequestBuilder<GoogleAuthenticatorChallenge> {

    GoogleAuthenticatorCreateChallengeRequestBuilder withCode(String code);
}
