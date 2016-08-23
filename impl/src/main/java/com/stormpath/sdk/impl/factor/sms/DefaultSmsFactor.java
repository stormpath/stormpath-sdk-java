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
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.phone.Phone;

/**
 * TODO: description
 */
public class DefaultSmsFactor extends Factor implements SmsFactor {
    private Phone phone;
    private String message;

    public DefaultSmsFactor(Phone phone, Account account, String message) {
        super(FactorType.SMS, account);

        this.phone = phone;
        this.message = message;
    }
}
