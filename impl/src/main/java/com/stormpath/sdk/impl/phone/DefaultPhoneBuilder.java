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
package com.stormpath.sdk.impl.phone;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.phone.PhoneBuilder;
import com.stormpath.sdk.phone.PhoneStatus;
import com.stormpath.sdk.phone.PhoneVerificationStatus;

/**
 * TODO: description
 */
// todo: mehrshad

public class DefaultPhoneBuilder implements PhoneBuilder {

    private String number;
    private String name;
    private String description;
    private PhoneStatus phoneStatus;
    private PhoneVerificationStatus verificationStatus;
    private Account account;

    @Override
    public PhoneBuilder setNumber(String number) {
        this.number = number;
        return this;
    }

    @Override
    public PhoneBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public PhoneBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public PhoneBuilder setPhoneStatus(PhoneStatus phoneStatus) {
        this.phoneStatus = phoneStatus;
        return this;
    }

    @Override
    public PhoneBuilder setPhoneVerificationStatus(PhoneVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
        return this;
    }

    @Override
    public PhoneBuilder setAccount(Account account) {
        this.account = account;
        return this;
    }

    public Phone build(){
        Assert.hasText(number, "Phone number cannot be null, empty, or blank");
        Assert.notNull(account, "Account cannot be null");

        if (phoneStatus == null) {
            phoneStatus = PhoneStatus.ENABLED;
        }

        if (verificationStatus == null) {
            verificationStatus = PhoneVerificationStatus.UNVERIFIED;
        }

        //return new DefaultPhone(number, name, description, phoneStatus, phoneVerificationStatus, account);
        return null;
    }
}
