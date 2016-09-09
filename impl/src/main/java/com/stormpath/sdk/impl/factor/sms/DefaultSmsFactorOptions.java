package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */
public class DefaultSmsFactorOptions extends DefaultOptions<SmsFactorOptions> implements SmsFactorOptions<SmsFactorOptions> {

    @Override
    public SmsFactorOptions withPhone() {
        return expand(DefaultSmsFactor.PHONE);
    }

    @Override
    public SmsFactorOptions withChallenges() {
        return expand(DefaultSmsFactor.CHALLENGES);
    }

    @Override
    public SmsFactorOptions withMostRecentChallenge() {
        return expand(DefaultSmsFactor.MOST_RECENT_CHALLENGE);
    }
}
