package com.stormpath.sdk.challenge;

import com.stormpath.sdk.factor.sms.CreateSmsFactorRequest;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;

public interface CreateChallengeRequestBuilder {

    CreateChallengeRequestBuilder withResponseOptions(ChallengeOptions options) throws IllegalArgumentException;

    CreateChallengeRequestBuilder createChallenge();

    CreateChallengeRequest build();
}
