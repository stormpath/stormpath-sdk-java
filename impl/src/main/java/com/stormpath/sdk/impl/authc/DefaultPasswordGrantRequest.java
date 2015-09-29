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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.PasswordGrantRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.authc.PasswordGrantRequestBuilder;

public class DefaultPasswordGrantRequest implements PasswordGrantRequest {

    private final String login;
    private final char[] password;
    private AccountStore accountStore;
    private final String grant_type = "password";

    public DefaultPasswordGrantRequest(String login, char[] password) {
        Assert.notNull(login, "login argument cannot be null.");
        Assert.notNull(password, "password argument cannot be null.");

        this.login = login;
        this.password = password;
    }

    @Override
    public PasswordGrantRequest setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
        return this;
    }

    @Override
    public String getPassword() {
        return password.toString();
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public AccountStore getAccountStore() {
        return accountStore;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
