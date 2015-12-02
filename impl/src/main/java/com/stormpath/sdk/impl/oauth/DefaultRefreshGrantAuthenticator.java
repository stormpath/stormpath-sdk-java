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
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC7
 */
public class DefaultRefreshGrantAuthenticator extends AbstractOauth2Authenticator implements RefreshGrantAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultRefreshGrantAuthenticator(Application application, DataStore dataStore){
        super(application, dataStore);
    }

    @Override
    public OauthGrantAuthenticationResult authenticate(Oauth2AuthenticationRequest authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(RefreshGrantRequest.class, authenticationRequest, "authenticationRequest must be an instance of RefreshGrantRequest.");
        RefreshGrantRequest refreshGrantRequest = (RefreshGrantRequest) authenticationRequest;

        RefreshAuthenticationAttempt attempt = new DefaultRefreshAuthenticationAttempt(dataStore);
        attempt.setRefreshToken(refreshGrantRequest.getRefreshToken());
        attempt.setGrantType(refreshGrantRequest.getGrantType());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, attempt, GrantAuthenticationToken.class, httpHeaders);

        OauthGrantAuthenticationResultBuilder builder = new DefaultOauthGrantAuthenticationResultBuilder(grantResult);
        return builder.setIsRefreshAuthGrantRequest(true).build();
    }
}
