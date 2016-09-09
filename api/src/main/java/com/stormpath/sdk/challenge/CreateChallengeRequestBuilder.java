package com.stormpath.sdk.challenge;

// todo: mehrshad

public interface CreateChallengeRequestBuilder {

    CreateChallengeRequestBuilder withResponseOptions(ChallengeOptions options) throws IllegalArgumentException;

    CreateChallengeRequestBuilder createChallenge();

    CreateChallengeRequest build();
}
