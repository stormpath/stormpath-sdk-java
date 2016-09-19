package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.sms.CreateSmsFactorRequestBuilder;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.factor.DefaultCreateFactorRequest;
import com.stormpath.sdk.lang.Assert;

/**
 * Created by mehrshadrafiei on 8/30/16.
 */

// todo: mehrshad

public class DefaultCreateSmsFactorRequestBuilder implements CreateSmsFactorRequestBuilder {

    private final com.stormpath.sdk.factor.sms.SmsFactor smsFactor;
    private SmsFactorOptions options;
    private boolean createChallenge;

    public DefaultCreateSmsFactorRequestBuilder(SmsFactor smsFactor) {
        Assert.notNull(smsFactor, "Factor can't be null.");
        this.smsFactor = smsFactor;
    }

    @Override
    public DefaultCreateSmsFactorRequestBuilder createChallenge() {
        this.createChallenge = true;
        return this;
    }


    @Override
    public CreateSmsFactorRequestBuilder withResponseOptions(SmsFactorOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateFactorRequest<SmsFactor, SmsFactorOptions> build() {
        return new DefaultCreateFactorRequest(smsFactor, options, createChallenge);
    }
}
