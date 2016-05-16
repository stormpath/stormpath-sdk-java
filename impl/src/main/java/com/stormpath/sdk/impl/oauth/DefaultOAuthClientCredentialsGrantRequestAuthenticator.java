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
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.http.HttpRequests;
import com.stormpath.sdk.impl.ds.DefaultResourceFactory;
import com.stormpath.sdk.impl.ds.ResourceFactory;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.TokenResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthClientCredentialsGrantRequestAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";
    final private ResourceFactory resourceFactory;

    public DefaultOAuthClientCredentialsGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
        resourceFactory = new DefaultResourceFactory(super.dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthClientCredentialsGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of ClientCredentialsGrantRequest.");
        OAuthClientCredentialsGrantRequestAuthentication oauthClientCredentialsGrantRequestAuthentication = (OAuthClientCredentialsGrantRequestAuthentication) authenticationRequest;

        String credentials = oauthClientCredentialsGrantRequestAuthentication.getApiKeyId() + ":" +
                oauthClientCredentialsGrantRequestAuthentication.getApiKeySecret();

        HttpRequest request = createOauthAuthenticationRequest(credentials);
        AccessTokenResult accessTokenResult = (AccessTokenResult) Applications.oauthRequestAuthenticator(application).authenticate(request);

        Map<String, Object> data = new HashMap<>();
        TokenResponse tokenResponse = accessTokenResult.getTokenResponse();
        data.put("scope", tokenResponse.getScope());
        data.put("access_token", tokenResponse.getAccessToken());
        data.put("refresh_token", tokenResponse.getRefreshToken());
        data.put("stormpath_access_token_href", tokenResponse.getApplicationHref());
        data.put("token_type", tokenResponse.getTokenType());
        data.put("expires_in", tokenResponse.getExpiresIn());
        GrantAuthenticationToken grantResult = new DefaultGrantAuthenticationToken(dataStore, data);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }

    private HttpRequest createOauthAuthenticationRequest(String credentials) {
        try {
            Map<String, String[]> headers = new LinkedHashMap<>();
            headers.put("Accept", new String[]{MediaType.APPLICATION_JSON_VALUE});
            headers.put("Content-Type", new String[]{MediaType.APPLICATION_FORM_URLENCODED_VALUE});
            headers.put("Authorization", new String[]{"Basic " + Base64.encodeBase64String(credentials.getBytes("UTF-8"))});
            Map<String, String[]> parameters = new LinkedHashMap<>();
            parameters.put("grant_type", new String[]{"client_credentials"});
            return HttpRequests.method(HttpMethod.POST)
                    .headers(headers)
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
