package com.stormpath.sdk.factor.sms;


public interface CreateSmsFactorRequestBuilder {

    CreateSmsFactorRequestBuilder withResponseOptions(SmsFactorOptions options) throws IllegalArgumentException;

    CreateSmsFactorRequest build();
}
