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
package com.stormpath.spring.security.token;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * @version 1.0.RC7.2
 */
public class ProviderAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Account account;

    public ProviderAuthenticationToken(Account account) {
        super(null);
        Assert.notNull(account.getEmail(), "email cannot be null");
        Assert.notNull(account, "account cannot be null");
        this.principal = account.getEmail();
        this.account = account;
    }

    /**
     * Get the credentials
     */
    @Override
    public Object getCredentials() {
        return null; // already authenticated via IdSite
    }

    /**
     * Get the principal
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * Get the account
     */
    public Account getAccount() {
        return this.account;
    }
}
