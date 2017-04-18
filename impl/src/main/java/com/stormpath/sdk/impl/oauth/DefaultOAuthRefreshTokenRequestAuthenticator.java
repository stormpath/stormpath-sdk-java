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
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.oauth.*;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthRefreshTokenRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthRefreshTokenRequestAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    private final String oauthTokenUrl;

    public DefaultOAuthRefreshTokenRequestAuthenticator(Application application, DataStore dataStore){
        this(application, dataStore, application.getHref() + OAUTH_TOKEN_PATH);
    }

    public DefaultOAuthRefreshTokenRequestAuthenticator(DataStore dataStore, String oauthTokenUrl) {
        this(null, dataStore, oauthTokenUrl);
    }

    public DefaultOAuthRefreshTokenRequestAuthenticator(Application application, DataStore dataStore, String oauthTokenUrl) {
        super(application, dataStore);
        this.oauthTokenUrl = oauthTokenUrl;
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.oauthTokenUrl, "baseUrl cannot be null or empty");
        Assert.isInstanceOf(OAuthRefreshTokenRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of RefreshGrantRequest.");
        OAuthRefreshTokenRequestAuthentication refreshGrantRequest = (OAuthRefreshTokenRequestAuthentication) authenticationRequest;

        RefreshAuthenticationAttempt attempt = new DefaultRefreshAuthenticationAttempt(dataStore);
        attempt.setRefreshToken(refreshGrantRequest.getRefreshToken());
        attempt.setGrantType(refreshGrantRequest.getGrantType());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        GrantAuthenticationToken grantResult = dataStore.create(oauthTokenUrl, attempt, GrantAuthenticationToken.class, httpHeaders);

        return buildGrant(grantResult);
    }

    protected OAuthGrantRequestAuthenticationResult buildGrant(GrantAuthenticationToken grantResult) {
        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);
        return builder.build();
    }
}
