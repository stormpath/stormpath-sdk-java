package com.stormpath.sdk.impl.challenge.sms;

import com.stormpath.sdk.challenge.sms.SmsChallengeCriteria;
import com.stormpath.sdk.impl.challenge.AbstractChallengeCriteria;

/**
 * Created by mehrshadrafiei on 9/26/16.
 */
public class DefaultSmsChallengeCriteria extends AbstractChallengeCriteria implements SmsChallengeCriteria{

    @Override
    public SmsChallengeCriteria orderByMessage() {
        return (SmsChallengeCriteria) orderBy(DefaultSmsChallenge.MESSAGE);
    }
}
