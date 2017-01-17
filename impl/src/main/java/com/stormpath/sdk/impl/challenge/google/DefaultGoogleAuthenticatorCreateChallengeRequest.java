package com.stormpath.sdk.impl.challenge.google;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorCreateChallengeRequest;
import com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequest;

public class DefaultGoogleAuthenticatorCreateChallengeRequest extends DefaultCreateChallengeRequest implements GoogleAuthenticatorCreateChallengeRequest {

    public DefaultGoogleAuthenticatorCreateChallengeRequest(Challenge challenge, ChallengeOptions options) {
        super(challenge, options);
    }
}
