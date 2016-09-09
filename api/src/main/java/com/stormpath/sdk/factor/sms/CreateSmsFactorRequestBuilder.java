package com.stormpath.sdk.factor.sms;

// todo: mehrshad
public interface CreateSmsFactorRequestBuilder {

    CreateSmsFactorRequestBuilder withResponseOptions(SmsFactorOptions options) throws IllegalArgumentException;

    CreateSmsFactorRequestBuilder createChallenge();

    CreateSmsFactorRequest build();
}
