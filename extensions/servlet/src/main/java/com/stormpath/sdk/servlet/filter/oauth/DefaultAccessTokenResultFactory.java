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
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.servlet.Servlets;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.filter.account.AccountJwtFactory;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultAccessTokenResultFactory implements AccessTokenResultFactory, ServletContextInitializable {

    protected static final String ACCOUNT_JWT_FACTORY = "stormpath.web.account.jwt.factory";

    private Config config;
    private Client client;
    private Application application;
    private AccountJwtFactory accountJwtFactory;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        this.config = ConfigResolver.INSTANCE.getConfig(servletContext);
        this.client = Servlets.getClient(servletContext);
        this.application = Servlets.getApplication(servletContext);
        this.accountJwtFactory = config.getInstance(ACCOUNT_JWT_FACTORY);
    }

    protected Config getConfig() {
        return this.config;
    }

    public Client getClient() {
        return client;
    }

    protected Application getApplication() {
        return this.application;
    }

    public AccountJwtFactory getAccountJwtFactory() {
        return accountJwtFactory;
    }

    @Override
    public AccessTokenResult createAccessTokenResult(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     AuthenticationResult result) {

        final Account account = result.getAccount();

        AccountJwtFactory factory = getAccountJwtFactory();

        String jwt = factory.createAccountJwt(request, response, result);

        Application application = getApplication();

        int ttl = getConfig().getAccountJwtTtl();

        final TokenResponse tokenResponse = DefaultTokenResponse
            .tokenType(TokenType.BEARER)
            .accessToken(jwt)
            .applicationHref(application.getHref())
            .expiresIn(String.valueOf(ttl))
            .build();

        return new PasswordGrantAccessTokenResult(account, tokenResponse);
    }
}
