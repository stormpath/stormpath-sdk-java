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
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.Oauth2AuthenticationRequest;
import com.stormpath.sdk.oauth.OauthGrantAuthenticationResult;
import com.stormpath.sdk.oauth.IdSiteAuthenticationRequest;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;

/**
 * @since 1.0.RC7
 */
public class DefaultIdSiteAuthenticator extends AbstractOauth2Authenticator implements IdSiteAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultIdSiteAuthenticator(Application application, DataStore dataStore){
        super(application, dataStore);
    }

    @Override
    public OauthGrantAuthenticationResult authenticate(Oauth2AuthenticationRequest authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(IdSiteAuthenticationRequest.class, authenticationRequest, "authenticationRequest must be an instance of IdSiteAuthenticationRequest.");

        IdSiteAuthenticationRequest request = (IdSiteAuthenticationRequest) authenticationRequest;

        IdSiteAuthenticationAttempt attempt = new DefaultIdSiteAuthenticationAttempt(dataStore);
        attempt.setGrantType(request.getGrantType());
        attempt.setToken(request.getToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, attempt, GrantAuthenticationToken.class, httpHeaders);

        OauthGrantAuthenticationResultBuilder builder = new DefaultOauthGrantAuthenticationResultBuilder(grantResult);
        return builder.build();
    }
}
