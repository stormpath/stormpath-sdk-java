package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.sms.SmsFactorCriteria;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

public class DefaultSmsFactorCriteria extends DefaultCriteria<SmsFactorCriteria, SmsFactorOptions> implements SmsFactorCriteria {

    public DefaultSmsFactorCriteria() {
        super(new DefaultSmsFactorOptions());
    }

    @Override
    public SmsFactorCriteria orderByPhone() {
        return orderBy(DefaultSmsFactor.PHONE);
    }

    @Override
    public SmsFactorCriteria orderByChallenge() {
        return orderBy(DefaultSmsFactor.CHALLENGE);
    }

    @Override
    public SmsFactorCriteria orderByStatus() {
        return orderBy(DefaultSmsFactor.STATUS);
    }

    @Override
    public SmsFactorCriteria orderByVerificationStatus() {
        return orderBy(DefaultSmsFactor.VERIFICATION_STATUS);
    }

    @Override
    public SmsFactorCriteria withPhone() {
        getOptions().withPhone();
        return this;
    }

    @Override
    public SmsFactorCriteria withChallenges(){
        getOptions().withChallenges();
        return this;
    }

    @Override
    public SmsFactorCriteria withMostRecentChallenge() {
        getOptions().withMostRecentChallenge();
        return this;
    }
}
