package com.stormpath.sdk.challenge.sms;

import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeOptions;

/**
 * Created by mehrshadrafiei on 9/26/16.
 * todo: mehrshad
 */
public interface SmsChallengeCriteria extends ChallengeCriteria, ChallengeOptions<ChallengeCriteria> {
    /**
     * Ensures that the query results are ordered by group {@link SmsChallenge#getMessage()} message}.
     * <p/>
     * Please see the {@link ChallengeCriteria class-level documentation} for controlling sort order (ascending or
     * descending) and chaining multiple {@code orderBy} clauses.
     *
     * @return this instance for method chaining
     */
    ChallengeCriteria orderByMessage();
}
