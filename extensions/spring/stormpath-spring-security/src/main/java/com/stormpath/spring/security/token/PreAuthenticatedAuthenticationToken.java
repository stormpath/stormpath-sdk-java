/*
 * Copyright 2015 Stormpath, Inc.
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
 * @since 1.0.RC7.2
 */
public class PreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;
    private final Account account;

    public PreAuthenticatedAuthenticationToken(Object aPrincipal, Object aCredentials, Account account) {
        super(null);
        Assert.notNull(aPrincipal, "aPrincipal cannot be null");
        Assert.notNull(account, "account cannot be null");
        this.principal = aPrincipal;
        this.credentials = aCredentials;
        this.account = account;
    }

    /**
     * Get the credentials
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
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


