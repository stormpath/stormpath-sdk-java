package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.factor.CreateFactorRequest;

// todo: mehrshad
public interface CreateSmsFactorRequestBuilder {

    CreateSmsFactorRequestBuilder withResponseOptions(SmsFactorOptions options) throws IllegalArgumentException;

    CreateSmsFactorRequestBuilder createChallenge();

    CreateFactorRequest<SmsFactor, SmsFactorOptions> build();
}
