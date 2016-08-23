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
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.phone.PhoneStatus;
import com.stormpath.sdk.phone.PhoneVerificationStatus;

/**
 * TODO: description
 */
public class DefaultPhone implements Phone {
    private String number;
    private String name;
    private String description;
    private PhoneStatus phoneStatus;
    private PhoneVerificationStatus phoneVerificationStatus;
    private Account account;

    public DefaultPhone(String number, String name, String description, PhoneStatus phoneStatus, PhoneVerificationStatus phoneVerificationStatus, Account account) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.phoneStatus = phoneStatus;
        this.phoneVerificationStatus = phoneVerificationStatus;
        this.account = account;
    }
}
