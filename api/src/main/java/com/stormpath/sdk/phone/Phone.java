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
package com.stormpath.sdk.phone;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * TODO: description
 */

// todo: mehrshad


public interface Phone extends Resource, Saveable, Deletable, Auditable {
    String getNumber();
    Phone setNumber(String number);
    String getName();
    Phone setName(String name);
    String getDescription();
    Phone setDescription(String description);
    PhoneStatus getStatus();
    Phone setStatus(PhoneStatus status);
    PhoneVerificationStatus getVerificationStatus();
    Phone setVerificationStatus(PhoneVerificationStatus verificationStatus);
    Account getAccount();
    Phone setAccount(Account account);
}
