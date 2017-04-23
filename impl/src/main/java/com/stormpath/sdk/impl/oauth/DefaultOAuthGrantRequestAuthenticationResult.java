/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.RefreshToken;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthGrantRequestAuthenticationResult implements OAuthGrantRequestAuthenticationResult {

    private final AccessToken accessToken;

    private final String accessTokenString;

    private final String idTokenString;

    private final RefreshToken refreshToken;

    private final String refreshTokenString;

    private final String accessTokenHref;

    private final String tokenType;

    private final long expiresIn;

    public DefaultOAuthGrantRequestAuthenticationResult(DefaultOAuthGrantRequestAuthenticationResultBuilder builder) {
        this.accessToken = builder.getAccessToken();
        this.accessTokenString = builder.getAccessTokenString();
        this.idTokenString = builder.getIdTokenString();
        this.refreshToken = builder.getRefreshToken();
        this.refreshTokenString = builder.getRefreshTokenString();
        this.accessTokenHref = builder.getAccessTokenHref();
        this.tokenType = builder.getTokenType();
        this.expiresIn = builder.getExpiresIn();
    }

    public DefaultOAuthGrantRequestAuthenticationResult(AccessToken accessToken,
                                                        String accessTokenString,
                                                        String idTokenString,
                                                        RefreshToken refreshToken,
                                                        String refreshTokenString,
                                                        String accessTokenHref,
                                                        String tokenType,
                                                        long expiresIn) {
        this.accessToken = accessToken;
        this.accessTokenString = accessTokenString;
        this.idTokenString = idTokenString;
        this.refreshToken = refreshToken;
        this.refreshTokenString = refreshTokenString;
        this.accessTokenHref = accessTokenHref;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    @Override
    public String getIdTokenString() {
        return idTokenString;
    }

    public String getRefreshTokenString() {
        return refreshTokenString;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public String getAccessTokenHref() {
        return accessTokenHref;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getAccessTokenString() {
        return accessTokenString;
    }

    public static OAuthGrantRequestAuthenticationResultBuilder builder() {
        return Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthGrantRequestAuthenticationResultBuilder");
    }
}
