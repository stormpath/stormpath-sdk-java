package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.factor.Factor;
// todo: mehrshad
public interface CreateSmsFactorRequest {

    Factor getSmsFactor();

    boolean isSmsFactorOptionsSpecified();

    boolean isCreateChallenge();

    SmsFactorOptions getSmsFactorOptions() throws IllegalStateException;

}
