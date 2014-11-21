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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.servlet.filter.account.AccountJwtFactory;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultAccessTokenResultFactory implements AccessTokenResultFactory {

    private final AccountJwtFactory accountJwtFactory;
    private final Application application;
    private final int accountJwtTtl;

    public DefaultAccessTokenResultFactory(Application application, AccountJwtFactory accountJwtFactory,
                                           int accountJwtTtl) {
        Assert.notNull(application, "Application argument cannot be null.");
        Assert.notNull(accountJwtFactory, "AccountJwtFactory cannot be null.");
        this.application = application;
        this.accountJwtFactory = accountJwtFactory;
        this.accountJwtTtl = accountJwtTtl;
    }

    protected Application getApplication() {
        return this.application;
    }

    protected AccountJwtFactory getAccountJwtFactory() {
        return accountJwtFactory;
    }

    protected int getAccountJwtTtl() {
        return this.accountJwtTtl;
    }

    @Override
    public AccessTokenResult createAccessTokenResult(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationResult result) {

        final Account account = result.getAccount();

        AccountJwtFactory factory = getAccountJwtFactory();

        String jwt = factory.createAccountJwt(request, response, result);

        Application application = getApplication();

        int ttl = getAccountJwtTtl();

        final TokenResponse tokenResponse =
            DefaultTokenResponse.tokenType(TokenType.BEARER).accessToken(jwt).applicationHref(application.getHref())
                                .expiresIn(String.valueOf(ttl)).build();

        return new PasswordGrantAccessTokenResult(account, tokenResponse);
    }
}
