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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.*;

/**
 * @since 1.0.RC5.1
 */
public class DefaultOauthGrantAuthenticationResult implements OauthGrantAuthenticationResult {

    private final AccessToken accessToken;

    private final String accessTokenString;

    private final RefreshToken refreshToken;

    private final String refreshTokenString;

    private final String accessTokenHref;

    private final String tokenType;

    private final int expiresIn;

    public DefaultOauthGrantAuthenticationResult(DefaultOauthGrantAuthenticationResultBuilder builder) {
        this.accessToken = builder.getAccessToken();
        this.accessTokenString = builder.getAccessTokenString();
        this.refreshToken = builder.getRefreshToken();
        this.refreshTokenString = builder.getRefreshTokenString();
        this.accessTokenHref = builder.getAccessTokenHref();
        this.tokenType = builder.getTokenType();
        this.expiresIn = builder.getExpiresIn();
    }

    public AccessToken getAccessToken() {
        return accessToken;
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

    public int getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getAccessTokenString() {
        return accessTokenString;
    }

    public static OauthGrantAuthenticationResultBuilder builder() {
        return Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOauthGrantAuthenticationResultBuilder");
    }
}
