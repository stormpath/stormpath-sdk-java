package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.query.Options;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

public interface SmsFactorOptions<T> extends Options {
    T withPhone();
    T withChallenges();
    T withMostRecentChallenge();
}
