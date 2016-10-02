package com.stormpath.sdk.impl.factor;

import com.stormpath.sdk.factor.FactorCriteria;
import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * Created by mehrshadrafiei on 10/1/16.
 */
public class DefaultFactorCriteria<T extends FactorCriteria, R extends FactorOptions>  extends DefaultCriteria<FactorCriteria, R> implements FactorCriteria {

    public DefaultFactorCriteria(R options) {
        super(options);
    }

    @Override
    public FactorCriteria orderByStatus() {
        return orderBy(AbstractFactor.STATUS);
    }

    @Override
    public FactorCriteria orderByVerificationStatus() {
        return orderBy(AbstractFactor.VERIFICATION_STATUS);
    }

    @Override
    public FactorCriteria orderByCreatedAt() {
        return orderBy(AbstractFactor.CREATED_AT);
    }

    @Override
    public FactorCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }

    @Override
    public FactorCriteria withChallenges() {
        getOptions().withChallenges();
        return this;
    }

    @Override
    public FactorCriteria withMostRecentChallenge() {
        getOptions().withMostRecentChallenge();
        return this;
    }
}
