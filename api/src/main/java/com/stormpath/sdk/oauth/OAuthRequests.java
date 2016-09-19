/*
* Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods serving Grant Authentication Request Builders. For example, to
 * construct a builder to create a {@link OAuthPasswordGrantRequestAuthentication}:
 * <pre>
 * OAuthPasswordGrantRequestAuthentication request = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
 *      .setLogin(username)
 *      .setPassword(password)
 *      .build();
 * </pre>
 * or, to construct a builder for a Refresh Grant Authentication request:
 * <pre>
 * OAuthRefreshTokenRequestAuthentication request = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder()
 *      .setRefreshToken(refreshToken)
 *      .build();
 * </pre>
 *
 * @since 1.0.RC7
 */
public final class OAuthRequests {

    private OAuthRequests() {
    }

    /**
     * Returns a new {@link OAuthPasswordGrantRequestAuthenticationFactory} instance, used to construct Create Grant Authentication requests.
     *
     */
    public static final OAuthPasswordGrantRequestAuthenticationFactory OAUTH_PASSWORD_GRANT_REQUEST = (OAuthPasswordGrantRequestAuthenticationFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthPasswordGrantRequestAuthenticationFactory");

    /**
     * Returns a new {@link OAuthRefreshTokenRequestAuthenticationFactory} instance, used to construct Refresh Grant Authentication requests.
     *
     */
    public static final OAuthRefreshTokenRequestAuthenticationFactory OAUTH_REFRESH_TOKEN_REQUEST = (OAuthRefreshTokenRequestAuthenticationFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthRefreshTokenRequestAuthenticationFactory");

    /**
     * Returns a new {@link OAuthBearerRequestAuthenticationFactory} instance, used to authenticate JWT Access Tokens.
     *
     */
    public static final OAuthBearerRequestAuthenticationFactory OAUTH_BEARER_REQUEST = (OAuthBearerRequestAuthenticationFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthBearerRequestAuthenticationFactory");

    /**
     * Returns a new {@link OAuthPasswordGrantRequestAuthenticationFactory} instance, used to construct IdSite Grant Authentication requests.
     *
     * @since 1.0.RC8.2
     */
    public static final IdSiteAuthenticationRequestFactory IDSITE_AUTHENTICATION_REQUEST = (IdSiteAuthenticationRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultIdSiteAuthenticationRequestFactory");

    /**
     * Returns a new {@link OAuthClientCredentialsRequestAuthenticatorFactory} instance, used to construct Client Credentials Authentication requests.
     *
     * @since 1.1.0
     */
    public static final OAuthClientCredentialsGrantRequestAuthenticationFactory OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST = (OAuthClientCredentialsGrantRequestAuthenticationFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthClientCredentialsGrantRequestAuthenticationFactory");

}
