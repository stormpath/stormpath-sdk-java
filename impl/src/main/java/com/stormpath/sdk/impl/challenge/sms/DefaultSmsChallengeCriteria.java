package com.stormpath.sdk.impl.challenge.sms;

import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.challenge.sms.SmsChallengeCriteria;
import com.stormpath.sdk.impl.challenge.DefaultChallengeCriteria;
import com.stormpath.sdk.impl.challenge.DefaultChallengeOptions;

/**
 * @since 1.1.0
 */
public class DefaultSmsChallengeCriteria extends DefaultChallengeCriteria<SmsChallengeCriteria, DefaultChallengeOptions>  implements SmsChallengeCriteria{

    public DefaultSmsChallengeCriteria(ChallengeOptions options) {
        super((DefaultChallengeOptions) options);
    }

    @Override
    public SmsChallengeCriteria orderByMessage() {
        return (SmsChallengeCriteria) orderBy(DefaultSmsChallenge.MESSAGE);
    }
}
