package com.stormpath.sdk.impl.factor;

import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * Created by mehrshadrafiei on 10/1/16.
 */
public class DefaultFactorOptions<T extends FactorOptions> extends DefaultOptions<T> implements FactorOptions<T>{
    @Override
    public T withAccount() {
        return expand(AbstractFactor.ACCOUNT);
    }

    @Override
    public T withChallenges() {
        return expand(AbstractFactor.CHALLENGES);
    }

    @Override
    public T withMostRecentChallenge() {
        return expand(AbstractFactor.MOST_RECENT_CHALLENGE);
    }
}
