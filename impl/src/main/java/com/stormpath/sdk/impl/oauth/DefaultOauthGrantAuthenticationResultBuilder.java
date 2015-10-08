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
import com.stormpath.sdk.oauth.*;

/**
 * @since 1.0.RC5.1
 */
public class DefaultOauthGrantAuthenticationResultBuilder implements OauthGrantAuthenticationResultBuilder {

    private AccessToken accessToken;

    private String accessTokenString;

    private RefreshToken refreshToken;

    private String refreshTokenString;

    private String accessTokenHref;

    private String tokenType;

    private int expiresIn;

    private Boolean isRefreshGrantAuthRequest = false;

    private GrantAuthenticationToken grantAuthenticationToken;

    public DefaultOauthGrantAuthenticationResultBuilder(GrantAuthenticationToken grantAuthenticationToken) {
        Assert.notNull(grantAuthenticationToken, "grantAuthenticationToken cannot be null.");
        this.grantAuthenticationToken = grantAuthenticationToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public String getRefreshTokenString() {
        return refreshTokenString;
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
    public OauthGrantAuthenticationResultBuilder setIsRefreshAuthGrantRequest(Boolean isRefreshAuthGrantRequest) {
        this.isRefreshGrantAuthRequest = isRefreshAuthGrantRequest;
        return this;
    }

    @Override
    public DefaultOauthGrantAuthenticationResult build() {
        Assert.notNull(this.grantAuthenticationToken, "grantAuthenticationToken has not been set. It is a required attribute.");

        this.accessToken = grantAuthenticationToken.getAsAccessToken();
        this.accessTokenString = grantAuthenticationToken.getAccessToken();
        this.refreshTokenString = grantAuthenticationToken.getRefreshToken();
        this.accessTokenHref = grantAuthenticationToken.getAccessTokenHref();
        this.tokenType = grantAuthenticationToken.getTokenType();
        this.expiresIn = Integer.parseInt(grantAuthenticationToken.getExpiresIn());

        if (isRefreshGrantAuthRequest){
            this.refreshToken = grantAuthenticationToken.getAsRefreshToken();
        }
        return new DefaultOauthGrantAuthenticationResult(this);
    }
}
