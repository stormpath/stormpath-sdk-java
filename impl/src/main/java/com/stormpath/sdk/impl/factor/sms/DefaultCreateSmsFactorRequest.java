package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.sms.CreateSmsFactorRequest;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.lang.Assert;

/**
 * Created by mehrshadrafiei on 8/30/16.
 */

// todo: mehrshad

public class DefaultCreateSmsFactorRequest implements CreateSmsFactorRequest {
    private final SmsFactor factor;
    private final SmsFactorOptions options;
    private final boolean createChallenge;

    public DefaultCreateSmsFactorRequest(SmsFactor factor, SmsFactorOptions options, boolean createChallenge) {
        Assert.notNull(factor, "factor cannot be null.");
        this.factor = factor;
        this.options = options;
        this.createChallenge = createChallenge;
    }

    @Override
    public SmsFactor getSmsFactor() {
        return this.factor;
    }

    @Override
    public boolean isSmsFactorOptionsSpecified() {
        return this.options != null;
    }

    @Override
    public boolean isCreateChallenge() {
        return createChallenge;
    }

    @Override
    public SmsFactorOptions getSmsFactorOptions() throws IllegalStateException {
        if(this.options == null){
            throw new IllegalStateException("SmsFactorOptions has not been configured. Use the isSmsFactorOptionsSpecified method to check first before invoking this method.");
        }
        return this.options;
    }
}
