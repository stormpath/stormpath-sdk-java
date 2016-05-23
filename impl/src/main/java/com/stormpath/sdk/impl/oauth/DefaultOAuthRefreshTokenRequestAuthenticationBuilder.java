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

import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticationBuilder;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthRefreshTokenRequestAuthenticationBuilder implements OAuthRefreshTokenRequestAuthenticationBuilder {

    private String refreshToken;

    @Override
    public DefaultOAuthRefreshTokenRequestAuthenticationBuilder setRefreshToken(String refreshToken) {
        Assert.hasText(refreshToken, "refreshToken cannot be null or empty.");
        this.refreshToken = refreshToken;
        return this;
    }

    @Override
    public OAuthRefreshTokenRequestAuthentication build() {
        Assert.state(this.refreshToken != null, "refreshToken has not been set. It is a required attribute.");
        DefaultOAuthRefreshTokenRequestAuthentication request = new DefaultOAuthRefreshTokenRequestAuthentication(refreshToken);
        return request;
    }
}
