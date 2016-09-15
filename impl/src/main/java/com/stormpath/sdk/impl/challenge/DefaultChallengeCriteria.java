package com.stormpath.sdk.impl.challenge;

import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * todo: mehrshad
 */
public class DefaultChallengeCriteria extends DefaultCriteria<ChallengeCriteria, ChallengeOptions> implements ChallengeCriteria {

    public DefaultChallengeCriteria() {
        super(new DefaultChallengeOptions());
    }

    @Override
    public ChallengeCriteria orderByMessage() {
        return orderBy(DefaultChallenge.MESSAGE);
    }

    @Override
    public ChallengeCriteria orderByMessageId() {
        return orderBy(DefaultChallenge.MESSAGE_ID);
    }

    @Override
    public ChallengeCriteria orderByStatus() {
        return orderBy(DefaultChallenge.STATUS);
    }

    @Override
    public ChallengeCriteria withAccount() {
        return orderBy(DefaultChallenge.ACCOUNT);
    }
}
