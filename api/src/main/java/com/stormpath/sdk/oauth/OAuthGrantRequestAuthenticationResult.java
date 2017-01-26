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
package com.stormpath.sdk.oauth;

/**
 * Contains the resultant {@link AccessToken AccessToken} and all its related properties after a successful Grant Authentication request.
 *
 * @see OAuthPasswordGrantRequestAuthenticator#authenticate(OAuthRequestAuthentication)
 * @see OAuthRefreshTokenRequestAuthenticator#authenticate(OAuthRequestAuthentication)
 *
 * @since 1.0.RC7
 */
public interface OAuthGrantRequestAuthenticationResult extends OAuthRequestAuthenticationResult {

    /**
     * Returns the String that corresponds to the OAuth Access Token created during the Create Grant Authentication operation.
     * @return the String representation of the OAuth Access Token
     */
    String getAccessTokenString();

    /**
     * Returns the {@code AccessToken AccessToken} generated during the Create Grant Authentication operation.
     * @return the {@code AccessToken AccessToken} object
     */
    AccessToken getAccessToken();


    /**
     * Returns the String that corresponds to the OpenID Connect id_token (if present) created during the Create Grant
     * Authentication operation.
     * @return the String representation of the OpenID Connect id_token
     * @since 1.4.0
     */
    String getIdTokenString();

    /**
     * Returns the String that corresponds to the token created during the Refresh Grant Authentication operation.
     * @return the String representation of the Oauth refresh token
     */
    String getRefreshTokenString();

    /**
     * Returns the {@code RefreshToken RefreshToken} generated during the Refresh Grant Authentication operation.
     * @return the {@code RefreshToken RefreshToken} object
     */
    RefreshToken getRefreshToken();

    /**
     * Returns the href of the token created during the Create Grant Authentication operation.
     * @return the href OAuth Access Token
     */
    String getAccessTokenHref();

    /**
     * Returns the type of the token created during the create grant authentication or refresh grant authentication operations.
     * @return the String corresponding to the type of the token created during the grant authentication operation
     */
    String getTokenType();

    /**
     * The lifetime in seconds of the access token. For example, the value "3600" denotes that the access token will expire one hour after the response was generated.
     * @return lifetime in seconds of the access token. For example, the value "3600" denotes that the access token will expire one hour after the response was generated.
     */
    long getExpiresIn();
}
