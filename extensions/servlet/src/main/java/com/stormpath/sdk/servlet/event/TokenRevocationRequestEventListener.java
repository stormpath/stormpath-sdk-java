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

import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.AccessTokenList;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.oauth.RefreshTokenList;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.RC8.3
 */
public class TokenRevocationRequestEventListener implements RequestEventListener {

    private static final Logger log = LoggerFactory.getLogger(TokenRevocationRequestEventListener.class);

    @Override
    public void on(SuccessfulAuthenticationRequestEvent e) {
        //No-op
    }

    @Override
    public void on(FailedAuthenticationRequestEvent e) {
        //No-op
    }

    @Override
    public void on(RegisteredAccountRequestEvent e) {
        //No-op
    }

    @Override
    public void on(VerifiedAccountRequestEvent e) {
        //No-op
    }

    @Override
    public void on(LogoutRequestEvent e) {
        AccessTokenList accessTokens = e.getAccount().getAccessTokens();
        for (AccessToken accessToken : accessTokens) {
            accessToken.delete();
        }
        RefreshTokenList refreshTokens = e.getAccount().getRefreshTokens();
        for (RefreshToken refreshToken : refreshTokens) {
            refreshToken.delete();
        }

        if (log.isDebugEnabled()) {
            log.debug("All access and refresh tokens for {} have been revoked.", e.getAccount().getEmail());
        }
    }

}
