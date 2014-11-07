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
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.filter.account.AccountJwtFactory;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultAccessTokenResultFactory implements AccessTokenResultFactory {

    protected static final String ACCOUNT_JWT_FACTORY = "stormpath.web.account.jwt.factory";

    protected Config getConfig(HttpServletRequest request) {
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext());
    }

    protected Client getClient(HttpServletRequest request) {
        return ClientResolver.INSTANCE.getClient(request.getServletContext());
    }

    protected AccountJwtFactory getAccountJwtFactory(HttpServletRequest request) {
        String className = getConfig(request).get(ACCOUNT_JWT_FACTORY);
        Assert.hasText(className, ACCOUNT_JWT_FACTORY + " class name value is required.");
        return Classes.newInstance(className);
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
    }

    @Override
    public AccessTokenResult createAccessTokenResult(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     AuthenticationResult result) {

        final Account account = result.getAccount();

        AccountJwtFactory factory = getAccountJwtFactory(request);

        String jwt = factory.createAccountJwt(request, response, account);

        int ttl = getConfig(request).getAccountJwtTtl();

        Application application = getApplication(request);

        final TokenResponse tokenResponse = DefaultTokenResponse
            .tokenType(TokenType.BEARER)
            .accessToken(jwt)
            .applicationHref(application.getHref())
            .expiresIn(String.valueOf(ttl))
            .build();

        return new PasswordGrantAccessTokenResult(account, tokenResponse);
    }
}
