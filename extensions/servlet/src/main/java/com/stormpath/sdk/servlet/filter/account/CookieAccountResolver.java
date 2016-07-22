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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.http.CookieResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This account resolver, resolves the account based on the access token cookie, using internally the jwtAccountResolver
 * If the access token cookie is somehow not properly validated, then this Resolver will try to create a new one if the refresh token
 * is available as a cookie as well.
 *
 * See https://github.com/stormpath/stormpath-sdk-java/issues/689
 *
 * @since 1.0.RC3
 */
public class CookieAccountResolver implements Resolver<Account> {

    private static final Logger log = LoggerFactory.getLogger(CookieAccountResolver.class);

    private final JwtAccountResolver jwtAccountResolver;
    private final CookieResolver accessTokenCookieResolver;
    private final CookieResolver refreshTokenCookieResolver;
    private final Saver<AuthenticationResult> authenticationResultSaver;
    private final AccessTokenResultFactory accessTokenResultFactory;

    public CookieAccountResolver(CookieConfig accessTokenCookieConfig,
                                 CookieConfig refreshTokenCookieConfig,
                                 JwtAccountResolver jwtAccountResolver,
                                 Saver<AuthenticationResult> authenticationResultSaver,
                                 AccessTokenResultFactory accessTokenResultFactory) {
        Assert.notNull(accessTokenCookieConfig, "accessTokenCookieConfig cannot be null.");
        Assert.notNull(refreshTokenCookieConfig, "refreshTokenCookieConfig cannot be null.");
        Assert.notNull(jwtAccountResolver, "jwtAccountResolver cannot be null.");
        Assert.notNull(accessTokenResultFactory, "accessTokenResultFactory cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        this.jwtAccountResolver = jwtAccountResolver;
        this.accessTokenCookieResolver = new CookieResolver(accessTokenCookieConfig.getName());
        this.refreshTokenCookieResolver = new CookieResolver(refreshTokenCookieConfig.getName());
        this.accessTokenResultFactory = accessTokenResultFactory;
        this.authenticationResultSaver = authenticationResultSaver;
    }

    @Override
    public Account get(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = accessTokenCookieResolver.get(request, response);

        if (cookie != null) {
            String val = cookie.getValue();
            if (Strings.hasText(val)) {
                try {
                    return getAccount(request, response, val);
                } catch (Exception e) {
                    String msg = "Encountered invalid JWT in access_token cookie. It might have expired, let's try with the refresh token now.";
                    log.debug(msg, e);
                }
            }
        }

        return tryRefreshToken(request, response);
    }

    protected Account getAccount(HttpServletRequest request, HttpServletResponse response, String jwt) {

        Account account = jwtAccountResolver.getAccountByJwt(request, response, jwt);

        if (account != null) {
            request.setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME,
                    HttpServletRequest.FORM_AUTH);
        }

        return account;
    }

    protected void deleteCookie(HttpServletResponse response, Cookie cookie) {
        if (!response.isCommitted()) {
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    //Resolves https://github.com/stormpath/stormpath-sdk-java/issues/689
    protected Account tryRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = refreshTokenCookieResolver.get(request, response);
        if (cookie == null) {
            return null;
        }

        String val = cookie.getValue();
        if (!Strings.hasText(val)) {
            return null;
        }

        try {
            OAuthRefreshTokenRequestAuthentication refreshGrantRequest = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder()
                    .setRefreshToken(val)
                    .build();
            OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder().setRefreshToken(refreshTokenCookieResolver.get(request, response).getValue());

            OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR
                    .forApplication(ApplicationResolver.INSTANCE.getApplication(request))
                    .authenticate(refreshGrantRequest);

                AccessTokenResult accessTokenResult = this.accessTokenResultFactory.createAccessTokenResult(request, response, authenticationResult);

                authenticationResultSaver.set(request, response, accessTokenResult);
                return getAccount(request, response, authenticationResult.getAccessToken().getJwt());

        } catch (Exception e) {
            String msg = "Encountered invalid JWT in refresh_token cookie. We will now delete both the access and refresh cookies for safety.";
            log.error(msg, e);
            deleteCookie(response, cookie);
            deleteCookie(response, accessTokenCookieResolver.get(request, response));
        }

        return null;
    }

}
