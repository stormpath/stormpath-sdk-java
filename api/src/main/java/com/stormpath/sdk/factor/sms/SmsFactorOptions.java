package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.query.Options;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

public interface SmsFactorOptions<T> extends FactorOptions<T> {
    T withPhone();
    T withChallenges();
    T withMostRecentChallenge();
}
