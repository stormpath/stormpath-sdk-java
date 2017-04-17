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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;

/**
 * @since 2.0.0
 */
public class OktaOAuthPasswordGrantRequestAuthenticator extends DefaultOAuthPasswordGrantRequestAuthenticator{

    private final OktaAuthNAuthenticator authenticator;
    private final OAuthTokenRevocator tokenRevocator;
    private final OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator;

    public OktaOAuthPasswordGrantRequestAuthenticator(Application application, DataStore dataStore, String oauthTokenPath, OktaAuthNAuthenticator authenticator, OAuthTokenRevocator tokenRevocator, OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator) {
        super(application, dataStore, oauthTokenPath);
        this.authenticator = authenticator;
        this.tokenRevocator = tokenRevocator;
        this.refreshTokenAuthenticator = refreshTokenAuthenticator;
    }

    @Override
    protected OAuthGrantRequestAuthenticationResult buildGrant(GrantAuthenticationToken grantResult) {
        OAuthGrantRequestAuthenticationResultBuilder builder = new OktaOAuthGrantRequestAuthenticationResultBuilder(grantResult, authenticator, tokenRevocator, refreshTokenAuthenticator, application);
        return builder.build();
    }
}
