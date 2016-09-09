/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.phone.Phone;

/**
 * TODO: description
 */

// todo: mehrshad

public class DefaultSmsFactorBuilder implements SmsFactorBuilder {

    private Phone phone;
    private Account account;
    private String message;

    @Override
    public SmsFactorBuilder setPhone(Phone phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public SmsFactorBuilder setAccount(Account account) {
        this.account = account;
        return this;
    }

    @Override
    public SmsFactorBuilder setChallengeMessage(String message) {
        this.message = message;
        return this;
    }

    public SmsFactor build(){
        /*Assert.notNull(phone, "Phone cannot be null");
        Assert.notNull(account, "Account cannot be null");

        return new DefaultSmsFactor(phone, account, message);*/
        return null;
    }
}
