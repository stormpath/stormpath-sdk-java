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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;

import java.util.Collections;
import java.util.Set;

/**
 * An OAuth {@code AccessTokenResult} that is returned as a result from a successful password {@code grant_type}
 * authentication attempt.
 */
public class PasswordGrantAccessTokenResult implements AccessTokenResult {

    private static final Set<String> emptySet = Collections.emptySet();

    private final TokenResponse tokenResponse;
    private final Set<String> scope;
    private final Account account;

    public PasswordGrantAccessTokenResult(Account account, TokenResponse tokenResponse) {
        this(account, tokenResponse, emptySet);
    }

    public PasswordGrantAccessTokenResult(Account account, TokenResponse tokenResponse, Set<String> scope) {
        Assert.notNull(account, "Account argument cannot be null.");
        Assert.notNull(tokenResponse, "TokenResponse argument cannot be null.");
        Assert.notNull(scope, "scope argument cannot be null.");
        this.tokenResponse = tokenResponse;
        this.scope = Collections.unmodifiableSet(scope);
        this.account = account;
    }

    @Override
    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public ApiKey getApiKey() {
        //return null because an ApiKey was not used to authenticate (password grant_type = username+password):
        return null;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getHref() {
        //this result is not itself a resource and therefore does not have its own href:
        return null;
    }
}
