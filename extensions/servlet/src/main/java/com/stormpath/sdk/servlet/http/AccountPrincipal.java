/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import java.security.Principal;

public class AccountPrincipal implements Principal {  //NOT serializable on purpose

    private final Account account;

    public AccountPrincipal(Account account) {
        Assert.notNull(account, "Account cannot be null.");
        this.account = account;
    }

    @Override
    public String getName() {
        String value = account.getGivenName();
        if (!Strings.hasText(value)) {
            value = account.getEmail();
        }
        return value;
    }

    public Account getAccount() {
        return this.account;
    }

}
