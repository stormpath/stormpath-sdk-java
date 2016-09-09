package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.query.Criteria;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

public interface SmsFactorCriteria extends Criteria<SmsFactorCriteria>, SmsFactorOptions<SmsFactorCriteria> {


    SmsFactorCriteria orderByPhone();

    SmsFactorCriteria orderByChallenge();

    SmsFactorCriteria orderByStatus();

    SmsFactorCriteria orderByVerificationStatus();

}