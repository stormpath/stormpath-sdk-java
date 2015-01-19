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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class CookieAuthenticationResultSaver extends AccountCookieHandler implements Saver<AuthenticationResult> {

    private AuthenticationJwtFactory authenticationJwtFactory;
    private Resolver<Boolean> secureCookieRequired;

    public CookieAuthenticationResultSaver(CookieConfig accountCookieConfig,
                                           Resolver<Boolean> secureCookieRequired,
                                           AuthenticationJwtFactory authenticationJwtFactory) {
        super(accountCookieConfig);
        Assert.notNull(secureCookieRequired, "secureCookieRequired RequestRCondition cannot be null.");
        Assert.notNull(authenticationJwtFactory, "AuthenticationJwtFactory cannot be null.");
        this.secureCookieRequired = secureCookieRequired;
        this.authenticationJwtFactory = authenticationJwtFactory;
    }

    public Resolver<Boolean> getSecureCookieRequired() {
        return secureCookieRequired;
    }

    public AuthenticationJwtFactory getAuthenticationJwtFactory() {
        return authenticationJwtFactory;
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
            jwt = getAuthenticationJwtFactory().createAccountJwt(request, response, value);
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

    protected boolean isSecureCookieRequired(HttpServletRequest request) {
        return getSecureCookieRequired().get(request, null);
    }

    @Override
    protected CookieConfig getAccountCookieConfig(HttpServletRequest request) {
        final CookieConfig config = super.getAccountCookieConfig(request);

        //should always be true in prod, but allow for localhost development testing:
        final boolean secure = config.isSecure() && isSecureCookieRequired(request);

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
