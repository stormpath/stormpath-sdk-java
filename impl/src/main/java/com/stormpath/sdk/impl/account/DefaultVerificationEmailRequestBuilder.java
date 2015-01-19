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
package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

/**
 * @since 1.0.0
 */
public class DefaultVerificationEmailRequestBuilder implements VerificationEmailRequestBuilder {

    private String login;
    private AccountStore accountStore;

    @Override
    public VerificationEmailRequestBuilder setLogin(String usernameOrEmail) {
        Assert.hasText(usernameOrEmail, "usernameOrEmail cannot be null or empty.");
        this.login = usernameOrEmail;
        return this;
    }

    @Override
    public VerificationEmailRequestBuilder setAccountStore(AccountStore accountStore) {
        if (accountStore != null && accountStore.getHref() == null) {
            throw new IllegalArgumentException("accountStore has been specified but its href is null.");
        }
        this.accountStore = accountStore;
        return this;
    }

    @Override
    public VerificationEmailRequest build() {
        Assert.state(Strings.hasText(this.login), "login is a required property. It must be provided before building.");

        DefaultVerificationEmailRequest verificationEmailRequest = new DefaultVerificationEmailRequest(null);
        verificationEmailRequest.setLogin(login);
        verificationEmailRequest.setAccountStore(accountStore);
        return verificationEmailRequest;
    }

}
