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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.OauthGrantAuthenticationResult;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC5
 */
public class DefaultOauthGrantAuthenticationResult implements OauthGrantAuthenticationResult {

    private final String accessToken;

    private final String expiresIn;

    private final String refreshToken;

    private final String tokenType;

    private final String stormpathAccessTokenHref;

    private DefaultOauthGrantAuthenticationResult(Builder builder) {
        accessToken = builder.accessToken;
        expiresIn = builder.expiresIn;
        refreshToken = builder.refreshToken;
        tokenType = builder.tokenType;
        stormpathAccessTokenHref = builder.stormpathAccessTokenHref;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getAccessTokenHref() {
        return stormpathAccessTokenHref;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toJson() {
//        return oAuthResponse.getBody();
        return null;
    }

    public static class Builder {

        private String accessToken;
        private String expiresIn;
        private String refreshToken;
        private String tokenType;
        private String stormpathAccessTokenHref;


        public Builder setAccessToken(String accessToken){
            this.accessToken = accessToken;
            return this;
        }

        public Builder setExpiresIn(String expiresIn){
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder setRefreshToken(String refreshToken){
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder setTokenType(String tokenType){
            this.tokenType = tokenType;
            return this;
        }

        public Builder setAccessTokenHref(String stormpathAccessTokenHref){
            this.stormpathAccessTokenHref = stormpathAccessTokenHref;
            return this;
        }

        public OauthGrantAuthenticationResult build() {
            return new DefaultOauthGrantAuthenticationResult(this);
        }
    }
}
