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
import com.stormpath.sdk.oauth.*;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthPasswordGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthPasswordGrantRequestAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultOAuthPasswordGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthPasswordGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of PasswordGrantRequest.");
        OAuthPasswordGrantRequestAuthentication oauthPasswordGrantRequestAuthentication = (OAuthPasswordGrantRequestAuthentication) authenticationRequest;

        OAuthPasswordGrantAuthenticationAttempt oauthPasswordGrantAuthenticationAttempt = new DefaultOAuthPasswordGrantAuthenticationAttempt(dataStore);
        oauthPasswordGrantAuthenticationAttempt.setLogin(oauthPasswordGrantRequestAuthentication.getLogin());
        oauthPasswordGrantAuthenticationAttempt.setPassword(oauthPasswordGrantRequestAuthentication.getPassword());
        oauthPasswordGrantAuthenticationAttempt.setGrantType(oauthPasswordGrantRequestAuthentication.getGrantType());
        if (oauthPasswordGrantRequestAuthentication.getAccountStore() != null){
            oauthPasswordGrantAuthenticationAttempt.setAccountStore(oauthPasswordGrantRequestAuthentication.getAccountStore());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, oauthPasswordGrantAuthenticationAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
