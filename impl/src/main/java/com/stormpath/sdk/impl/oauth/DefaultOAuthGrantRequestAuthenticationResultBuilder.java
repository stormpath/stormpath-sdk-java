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
 * @since 1.0.RC7
 */
public class DefaultOAuthGrantRequestAuthenticationResultBuilder implements OAuthGrantRequestAuthenticationResultBuilder {

    protected AccessToken accessToken;

    protected String accessTokenString;

    protected RefreshToken refreshToken;

    protected String refreshTokenString;

    protected String accessTokenHref;

    protected String tokenType;

    protected long expiresIn;

    protected Boolean isRefreshGrantAuthRequest = false;

    protected GrantAuthenticationToken grantAuthenticationToken;

    public DefaultOAuthGrantRequestAuthenticationResultBuilder(GrantAuthenticationToken grantAuthenticationToken) {
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

    public long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public OAuthGrantRequestAuthenticationResultBuilder setIsRefreshAuthGrantRequest(Boolean isRefreshAuthGrantRequest) {
        this.isRefreshGrantAuthRequest = isRefreshAuthGrantRequest;
        return this;
    }

    @Override
    public DefaultOAuthGrantRequestAuthenticationResult build() {
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
        return new DefaultOAuthGrantRequestAuthenticationResult(this);
    }
}
