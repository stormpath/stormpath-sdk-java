/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.util.EnumSet;
import java.util.TimeZone;

/**
 * OAuthBasicAuthenticator
 *
 * @since 1.0.beta
 */
public class OAuthBasicAuthenticator {

    private static final EnumSet<GrantType> SUPPORTED_GRANT_TYPES;

    static {
        SUPPORTED_GRANT_TYPES = EnumSet.of(GrantType.CLIENT_CREDENTIALS);
    }

    public final static char SCOPE_SEPARATOR = ':';

    public static String TOKEN_DURATION = "3600";

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final InternalDataStore internalDataStore;

    public OAuthBasicAuthenticator(InternalDataStore internalDataStore) {

        this.internalDataStore = internalDataStore;

    }

    public BasicOauthAuthenticationResult authenticate(Application application, DefaultBasicOauthAuthenticationRequest request) {
        Assert.notNull(application, "application cannot be null.");

        try {
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request.getHttpServletRequest());

            ApiKey apiKey = application.getApiKey(oauthRequest.getClientId());

            if (apiKey == null) {

            }


        } catch (OAuthSystemException e) {

        } catch (OAuthProblemException e) {

        }
        return null;
    }

//    public AuthenticationResult authenticate(Application application, Default request) {
//        Assert.notNull(application, "application cannot be null.");
//
//        OAuthAccessResourceRequest d;
//
//
//        HttpServletRequest servletRequest = new OAuthHttpServletRequest(authRequest.getHttpRequest());
//
//        try {
//            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(servletRequest);
//
//            ApiKey apiKey = application.getApiKey(oauthRequest.getClientId());
//
//            if (apiKey == null || !apiKey.getSecret().equals(oauthRequest.getClientSecret())) {
//
//                OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
//                        .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
//                        .setErrorDescription("").buildJSONMessage();
//            }
//
//            //TODO: Probably catch the IllegalArgumentException
//            GrantType grantType = GrantType.valueOf(oauthRequest.getGrantType());
//
//            if (!SUPPORTED_GRANT_TYPES.contains(grantType)) {
//
//                String errorDescription = String.format("GrantType %s is not supported", grantType.toString());
//
//                OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                        .setError(OAuthError.TokenResponse.INVALID_GRANT)
//                        .setErrorDescription(errorDescription).buildJSONMessage();
//            }
//
//
//            OAuthResponse response = OAuthASResponse.status(HttpServletResponse.SC_OK).buildJSONMessage();
//
//
////            AuthenticationResult authenticationResult = new OAuthAuthenticationResult();
//
//            return null;
//        } catch (Exception e) {
//            //return new RequestAuthenticationException("") ;
//            return null;
//        }
//    }

//    public static void main(String[] args) {
//
//        Map<String, String[]> headers = new HashMap<String, String[]>();
//
//        String[] authorization = {"Basic MUhZNERXWUxCQzk3TFZUU1NRV0UxNFdWQzo3U0JzQm5DMmtnYTJSTWp2SUlBRitOT0tGQlI4S3lyNHFXYlNkMHM2WkM4"};
//
//        headers.put("Authorization", authorization);
//
//        String[] redirectUri = {"http://test.stormpath.com/callback"};
//        String[] responseType = {"token"};
//        String[] state = {"requestState"};
//        String[] scope = {"createStuff readStuff deleteStuff"};
//        String[] clientId = {"1HY4DWYLBC97LVTSSQWE14WVC"};
//
//        Map<String, String[]> parameters = new HashMap<String, String[]>();
//
//        parameters.put(OAuth.OAUTH_REDIRECT_URI, redirectUri);
//        parameters.put(OAuth.OAUTH_RESPONSE_TYPE, responseType);
//        parameters.put(OAuth.OAUTH_STATE, state);
//        parameters.put(OAuth.OAUTH_SCOPE, scope);
//        parameters.put(OAuth.OAUTH_CLIENT_ID, clientId);
//
//
//    }
}
