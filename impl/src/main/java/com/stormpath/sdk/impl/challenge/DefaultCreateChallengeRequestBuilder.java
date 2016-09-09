package com.stormpath.sdk.impl.challenge;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.challenge.CreateChallengeRequestBuilder;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.sms.CreateSmsFactorRequest;
import com.stormpath.sdk.factor.sms.CreateSmsFactorRequestBuilder;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.factor.sms.DefaultCreateSmsFactorRequest;
import com.stormpath.sdk.lang.Assert;

/**
 * Created by mehrshadrafiei on 8/30/16.
 */
// todo: mehrshad
public class DefaultCreateChallengeRequestBuilder implements CreateChallengeRequestBuilder {

    private final Challenge challenge;
    private ChallengeOptions options;

    public DefaultCreateChallengeRequestBuilder(Challenge challenge) {
        Assert.notNull(challenge, "Challenge can't be null.");
        this.challenge = challenge;
    }

    @Override
    public CreateChallengeRequestBuilder withResponseOptions(ChallengeOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateChallengeRequestBuilder createChallenge() {
        return null;
    }

    @Override
    public CreateChallengeRequest build() {
        return new DefaultCreateChallengeRequest(challenge);
    }
}
