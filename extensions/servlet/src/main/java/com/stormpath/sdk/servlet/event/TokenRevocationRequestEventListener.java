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
package com.stormpath.sdk.servlet.event;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.OAuthRevocationRequest;
import com.stormpath.sdk.oauth.OAuthTokenRevocators;
import com.stormpath.sdk.oauth.TokenTypeHint;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.http.CookieResolver;
import org.apache.oltu.oauth2.rs.extractor.BearerHeaderTokenExtractor;
import org.apache.oltu.oauth2.rs.extractor.TokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC8.3
 */
public class TokenRevocationRequestEventListener implements RequestEventListener {

    private static final Logger log = LoggerFactory.getLogger(TokenRevocationRequestEventListener.class);

    private final TokenExtractor tokenExtractor = new BearerHeaderTokenExtractor();
    private final CookieResolver accessTokenCookieResolver = new CookieResolver("access_token");
    private final CookieResolver refreshTokenCookieResolver = new CookieResolver("refresh_token");

    protected ApplicationResolver applicationResolver = ApplicationResolver.INSTANCE;

    @Override
    public void on(SuccessfulAuthenticationRequestEvent event) {
        //No-op
    }

    @Override
    public void on(FailedAuthenticationRequestEvent event) {
        //No-op
    }

    @Override
    public void on(RegisteredAccountRequestEvent event) {
        //No-op
    }

    @Override
    public void on(VerifiedAccountRequestEvent event) {
        //No-op
    }

    @Override
    public void on(LogoutRequestEvent event) {
        revokeAccessToken(event);
        revokeRefreshToken(event);
    }

    private void revokeAccessToken(LogoutRequestEvent event) {
        String accessToken = getJwtFromLogoutRequestEvent(event);
        HttpServletRequest request = event.getRequest();
        revokeToken(accessToken, TokenTypeHint.ACCESS_TOKEN, request);
    }

    private void revokeRefreshToken(LogoutRequestEvent event) {
        HttpServletRequest request = event.getRequest();
        String refreshToken = refreshTokenCookieResolver.get(request, null).getValue();
        revokeToken(refreshToken, TokenTypeHint.REFRESH_TOKEN, request);
    }

    private void revokeToken(String token, TokenTypeHint tokenTypeHint, HttpServletRequest request) {
        Application application = applicationResolver.getApplication(request);
        if (application != null && token != null) {
            try {
                OAuthRevocationRequest revocationRequest = OAuthRequests.OAUTH_TOKEN_REVOCATION_REQUEST.builder()
                    .setToken(token)
                    .setTokenTypeHint(tokenTypeHint)
                    .build();
                OAuthTokenRevocators.OAUTH_TOKEN_REVOCATOR.forApplication(application).revoke(revocationRequest);
            } catch (ResourceException e) {
                com.stormpath.sdk.error.Error error = e.getStormpathError();
                String message = "There was an error trying to revoke a token: {}";

                if (log.isDebugEnabled()) {
                    log.warn(message, error.getMessage(), e);
                } else {
                    log.warn(message, error.getMessage());
                }
            }
        }
    }

    // addresses https://github.com/stormpath/stormpath-sdk-java/issues/788
    // ensures that we look both in the Authorization header and in cookies
    // for an access_token
    protected String getJwtFromLogoutRequestEvent(LogoutRequestEvent event) {
        String jwt = tokenExtractor.getAccessToken(event.getRequest());
        if (jwt == null && accessTokenCookieResolver.get(event.getRequest(), null) != null) {
            jwt = accessTokenCookieResolver.get(event.getRequest(), null).getValue();
        }

        return jwt;
    }

}
