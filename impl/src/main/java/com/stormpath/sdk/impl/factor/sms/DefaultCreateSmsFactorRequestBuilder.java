/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.sms.CreateSmsFactorRequestBuilder;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.impl.factor.DefaultCreateFactorRequest;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.1.0
 */
public class DefaultCreateSmsFactorRequestBuilder implements CreateSmsFactorRequestBuilder {

    private final com.stormpath.sdk.factor.sms.SmsFactor smsFactor;
    private SmsFactorOptions options;
    private boolean createChallenge;

    public DefaultCreateSmsFactorRequestBuilder(SmsFactor smsFactor) {
        Assert.notNull(smsFactor, "Factor can't be null.");
        this.smsFactor = smsFactor;
    }

    @Override
    public DefaultCreateSmsFactorRequestBuilder createChallenge() {
        this.createChallenge = true;
        return this;
    }


    @Override
    public CreateSmsFactorRequestBuilder withResponseOptions(SmsFactorOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateFactorRequest<SmsFactor, SmsFactorOptions> build() {
        return new DefaultCreateFactorRequest(smsFactor, options, createChallenge);
    }
}
