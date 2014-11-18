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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieAuthenticationResultSaver extends AccountCookieHandler
    implements Saver<AuthenticationResult>, ServletContextInitializable {

    protected static final String ACCOUNT_COOKIE_SECURE_EVALUATOR = "stormpath.web.account.cookie.secure.evaluator";
    protected static final String ACCOUNT_JWT_FACTORY = "stormpath.web.account.jwt.factory";

    private AccountCookieSecureEvaluator accountCookieSecureEvaluator;
    private AccountJwtFactory accountJwtFactory;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        this.accountCookieSecureEvaluator = config.getInstance(ACCOUNT_COOKIE_SECURE_EVALUATOR);
        this.accountJwtFactory = config.getInstance(ACCOUNT_JWT_FACTORY);
    }

    public AccountCookieSecureEvaluator getAccountCookieSecureEvaluator() {
        return accountCookieSecureEvaluator;
    }

    public AccountJwtFactory getAccountJwtFactory() {
        return accountJwtFactory;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {

        if (value == null) {
            remove(request, response);
            return;
        }

        String jwt;

        if (value instanceof AccessTokenResult) {
            jwt = ((AccessTokenResult) value).getTokenResponse().getAccessToken();
        } else {
            jwt = getAccountJwtFactory().createAccountJwt(request, response, value.getAccount());
        }

        Saver<String> saver = getCookieSaver(request);

        saver.set(request, response, jwt);
    }

    protected void remove(HttpServletRequest request, HttpServletResponse response) {
        Saver<String> saver = getCookieSaver(request);
        saver.set(request, response, null);
    }

    protected Saver<String> getCookieSaver(HttpServletRequest request) {
        CookieConfig cfg = getAccountCookieConfig(request);
        return new CookieSaver(cfg);
    }

    @Override
    protected CookieConfig getAccountCookieConfig(HttpServletRequest request) {
        final CookieConfig config = super.getAccountCookieConfig(request);

        //should always be true in prod, but allow for localhost development testing:
        final boolean secure = config.isSecure() &&
                               getAccountCookieSecureEvaluator().isAccountCookieSecure(request, config);

        //wrap it to allow for access during development:
        return new CookieConfig() {
            @Override
            public String getName() {
                return config.getName();
            }

            @Override
            public String getComment() {
                return config.getComment();
            }

            @Override
            public String getDomain() {
                return config.getDomain();
            }

            @Override
            public int getMaxAge() {
                return config.getMaxAge();
            }

            @Override
            public String getPath() {
                return config.getPath();
            }

            @Override
            public boolean isSecure() {
                return secure;
            }

            @Override
            public boolean isHttpOnly() {
                return config.isHttpOnly();
            }
        };
    }
}
