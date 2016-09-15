package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.FactorCriteria;
import com.stormpath.sdk.factor.sms.SmsFactorCriteria;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

public class DefaultSmsFactorCriteria extends DefaultCriteria<FactorCriteria, SmsFactorOptions> implements SmsFactorCriteria {

    public DefaultSmsFactorCriteria() {
        super(new DefaultSmsFactorOptions());
    }

    @Override
    public FactorCriteria orderByType() {
        return null;
    }

    @Override
    public SmsFactorCriteria orderByStatus() {
        return (SmsFactorCriteria)orderBy(DefaultSmsFactor.STATUS);
    }

    @Override
    public SmsFactorCriteria orderByVerificationStatus() {
        return (SmsFactorCriteria)orderBy(DefaultSmsFactor.VERIFICATION_STATUS);
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

    @Override
    public FactorCriteria withAccount() {
        return null;
    }
}
