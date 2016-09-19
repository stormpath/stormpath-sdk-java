package com.stormpath.sdk.impl.factor;

import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.lang.Assert;

/**
 * Created by mehrshadrafiei on 8/30/16.
 */

// todo: mehrshad

public class DefaultCreateFactorRequest<T extends Factor, R extends FactorOptions> implements CreateFactorRequest {
    private final T factor;
    private final R options;
    private final boolean createChallenge;

    public DefaultCreateFactorRequest(T factor, R options, boolean createChallenge) {
        Assert.notNull(factor, "factor cannot be null.");
        this.factor = factor;
        this.options = options;
        this.createChallenge = createChallenge;
    }

    @Override
    public T getFactor() {
        return this.factor;
    }

    @Override
    public boolean hasFactorOptions() {
        return this.options != null;
    }

    @Override
    public boolean isCreateChallenge() {
        return createChallenge;
    }

    @Override
    public R getFactorOptions() throws IllegalStateException {
        if(this.options == null){
            throw new IllegalStateException("SmsFactorOptions has not been configured. Use the hasFactorOptions method to check first before invoking this method.");
        }
        return this.options;
    }
}
