/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper class serving {@link OAuthRequestAuthenticatorFactory OAuthRequestAuthenticatorFactory}s. For example, to
 * construct a {@link OAuthPasswordGrantRequestAuthentication PasswordGrantRequest}:
 * <pre>
 *      OAuthPasswordGrantRequestAuthentication createRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
 *              .setLogin(email)
 *              .setPassword(password)
 *              .build();
 *      OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(createRequest);
 * </pre>
 * Once your application receives the result, the first thing to do is to validate that the token is valid. There are different ways you can complete this task.
 * The benefit of using Stormpath to validate the token through the REST API is that Stormpath can validate the token against the state of your application
 * and account. To illustrate the difference:
 * <table summary="JWT validation">
 *   <tr>
 *      <td>Validation Criteria</td><td>Locally</td><td>Stormpath</td>
 *   <tr/>
 *   <tr>
 *      <td>Token hasn't been tampered with</td><td>yes</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Token hasn't expired</td><td>yes</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Token hasn't been revoked</td><td>no</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Account hasn't been disabled, and hasn't been deleted</td><td>no</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Issuer is Stormpath</td><td>yes</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Issuing application is still enabled, and hasn't been deleted</td><td>no</td><td>yes</td>
 *   </tr>
 *   <tr>
 *      <td>Account is still in an account store for the issuing application</td><td>no</td><td>yes</td>
 *   </tr>
 * </table>
 * <h2>Using Stormpath to Validate Tokens</h2>
 * <pre>
 * JwtAuthenticationRequest authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(grantResult.getAccessTokenString()).build();
 * JwtAuthenticationResult authResultRemote = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(authRequest);
 * </pre>
 * <h2>Validating the Token Locally</h2>
 * <pre>
 * JwtAuthenticationRequest authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(grantResult.getAccessTokenString()).build();
 * JwtAuthenticationResult authResultRemote = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest);
 * </pre>
 * <h2>Refreshing Access Tokens</h2>
 * <p>
 * Passing access tokens allows access to resources in your application. But what happens when the Access Token expires? You could require the user to authenticate again,
 * or use the Refresh Token to get a new Access Token without requiring credentials.
 * </p>
 * <p>To get a new Access Token to for a Refresh Token, you must first make sure that the application {@link OAuthPolicy#setRefreshTokenTtl(String)
 * has been configured to generate a Refresh Token} in the OAuth 2.0 Access Token Response.</p>
 * <p>A refresh token is obtained this way:</p>
 * <pre>
 * RefreshGrantRequest request = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder().setRefreshToken(result.getRefreshTokenString()).build();
 * OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(request);
 * </pre>
 *
 * @see OAuthPolicy
 * @since 1.0.RC7
 */
public class Authenticators {

    private Authenticators() {
    }

    /**
     * Constructs {@link OAuthPasswordGrantRequestAuthenticator}s.
     */
    public static final OAuthPasswordRequestAuthenticatorFactory OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR =
            (OAuthPasswordRequestAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthPasswordRequestAuthenticatorFactory");

    /**
     * Constructs {@link OAuthRefreshTokenRequestAuthenticator}s.
     */
    public static final OAuthRefreshTokenRequestAuthenticatorFactory OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR =
            (OAuthRefreshTokenRequestAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthRefreshTokenRequestAuthenticatorFactory");

    /**
     * Constructs {@link OAuthBearerRequestAuthenticator}s.
     */
    public static final OAuthBearerRequestAuthenticatorFactory OAUTH_BEARER_REQUEST_AUTHENTICATOR =
            (OAuthBearerRequestAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthBearerRequestAuthenticatorFactory");

    /**
     * Constructs {@link IdSiteAuthenticator}s.
     *
     * @since 1.0.RC8.2
     */
    public static final IdSiteAuthenticatorFactory ID_SITE_AUTHENTICATOR =
            (IdSiteAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultIdSiteAuthenticatorFactory");

    /**
     * Constructs {@link OAuthClientCredentialsGrantRequestAuthenticator}s.
     *
     * @since 1.0.0
     */
    public static final OAuthClientCredentialsRequestAuthenticatorFactory OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR =
            (OAuthClientCredentialsRequestAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthClientCredentialsRequestAuthenticatorFactory");

    /**
     * Constructs {@link OAuthStormpathSocialGrantRequestAuthenticator}s.
     *
     * @since 1.1.0
     */
    public static final OAuthStormpathSocialRequestAuthenticatorFactory OAUTH_STORMPATH_SOCIAL_GRANT_REQUEST_AUTHENTICATOR =
            (OAuthStormpathSocialRequestAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthStormpathSocialRequestAuthenticatorFactory");
}

