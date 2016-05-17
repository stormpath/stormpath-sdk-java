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
import com.stormpath.sdk.impl.account.DefaultAccount;
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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.oauth.DefaultGrantAuthenticationToken.*;
import static com.stormpath.sdk.impl.oauth.DefaultOAuthBearerRequestAuthenticator.ACCESS_TOKEN_PATH;
import static com.stormpath.sdk.impl.oauth.DefaultOAuthBearerRequestAuthenticator.APPLICATION_PATH;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthClientCredentialsGrantRequestAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";
    protected Boolean isLocalValidation = false;

    public DefaultOAuthClientCredentialsGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    public DefaultOAuthClientCredentialsGrantRequestAuthenticator withLocalValidation() {
        this.isLocalValidation = Boolean.TRUE;
        return this;
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthClientCredentialsGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of ClientCredentialsGrantRequest.");
        OAuthClientCredentialsGrantRequestAuthentication credentialsGrantRequest = (OAuthClientCredentialsGrantRequestAuthentication) authenticationRequest;

        if (this.isLocalValidation) {
            // todo: Not sure this is local validation since it does make a request
            HttpRequest request = createOauthAuthenticationRequest("Bearer", credentialsGrantRequest.getJwt());
            // the call below results in in: com.stormpath.sdk.error.jwt.InvalidJwtException: The jwt value format is not correct.
            /*OAuthAuthenticationResult result = Applications.oauthRequestAuthenticator(application).authenticate(request);
            try {
                Assert.isTrue(Strings.hasText(result.getAccount().getEmail()));
            } catch (Exception e) {
                throw new JwtException("JWT failed validation; it cannot be trusted.");
            }*/

            // this is similar to the validation in DefaultOAuthBearerRequestAuthenticator and seems better than the one above
            String apiKeySecret = dataStore.getApiKey().getSecret();
            try {
                // During parsing, the JWT is validated for expiration, signature and tampering
                Claims claims = Jwts.parser()
                        .setSigningKey(apiKeySecret.getBytes("UTF-8"))
                        .parseClaimsJws(credentialsGrantRequest.getJwt()).getBody();
                Assert.isTrue(claims.getIssuer().equals(application.getHref()));

                Map<String, Object> properties = new HashMap<>();

                //Account account = dataStore.getResource(claims.getSubject(), Account.class);

                String accessTokenHref = application.getHref().replace(APPLICATION_PATH, ACCESS_TOKEN_PATH);
                int accessTokenIdStartingPoint = accessTokenHref.lastIndexOf("/") + 1;
                accessTokenHref = accessTokenHref.substring(0, accessTokenIdStartingPoint);
                accessTokenHref = accessTokenHref + claims.getSubject();

                properties.put(DefaultAccount.HREF_PROP_NAME, accessTokenHref);
                //properties.put(DefaultAccessToken.ACCOUNT_PROP_NAME, account);
                properties.put(DefaultAccessToken.APPLICATION_PROP_NAME, application);
                properties.put(DefaultAccessToken.JWT_PROP_NAME, credentialsGrantRequest.getJwt());
                properties.put(DefaultAccessToken.TENANT_PROP_NAME, application.getTenant());
                // using claims.getExpiration().getTime() in the time below causes failure
                properties.put(EXPIRES_IN.getName(), 0);

                GrantAuthenticationToken grantResult = new ClientCredentialsGrantAuthenticationToken(dataStore, properties);

                OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);
                return builder.build();

            } catch (Exception e) {
                throw new JwtException("JWT failed validation; it cannot be trusted.");
            }
        }

        String credentials = credentialsGrantRequest.getApiKeyId() + ":" + credentialsGrantRequest.getApiKeySecret();
        HttpRequest request = createOauthAuthenticationRequest("Basic", credentials);
        AccessTokenResult accessTokenResult = (AccessTokenResult) Applications.oauthRequestAuthenticator(application).authenticate(request);

        Map<String, Object> data = new HashMap<>();
        TokenResponse tokenResponse = accessTokenResult.getTokenResponse();
        data.put(ACCESS_TOKEN.getName(), tokenResponse.getAccessToken());
        data.put(ACCESS_TOKEN_HREF.getName(), tokenResponse.getApplicationHref());
        data.put(TOKEN_TYPE.getName(), tokenResponse.getTokenType());
        data.put(EXPIRES_IN.getName(), tokenResponse.getExpiresIn());
        GrantAuthenticationToken grantResult = new ClientCredentialsGrantAuthenticationToken(dataStore, data);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);
        return builder.build();
    }

    private HttpRequest createOauthAuthenticationRequest(String authHeader, String credentials) {
        try {
            Map<String, String[]> headers = new LinkedHashMap<>();
            headers.put("Accept", new String[]{MediaType.APPLICATION_JSON_VALUE});
            headers.put("Content-Type", new String[]{MediaType.APPLICATION_FORM_URLENCODED_VALUE});
            headers.put("Authorization", new String[]{authHeader + " " + Base64.encodeBase64String(credentials.getBytes("UTF-8"))});
            Map<String, String[]> parameters = new LinkedHashMap<>();
            if (authHeader.equals("Basic")) {
                parameters.put("grant_type", new String[]{"client_credentials"});
            }
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
