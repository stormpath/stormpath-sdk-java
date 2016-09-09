package com.stormpath.sdk.impl.challenge;

import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */
public class DefaultChallengeCriteria extends DefaultCriteria<ChallengeCriteria, ChallengeOptions> implements ChallengeCriteria {

    public DefaultChallengeCriteria() {
        super(new DefaultChallengeOptions());
    }

}
