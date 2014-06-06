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
package com.stormpath.sdk.oauth.authc;

import com.stormpath.sdk.oauth.authz.TokenResponse;

/**
 * An {@code AuthenticationResult} that indicates a client authenticated with your server-side API specifically
 * for the purpose of obtaining a new OAuth Access Token (eg via a
 * <a href="http://tools.ietf.org/html/rfc6749#section-4.4">OAuth 2 Client Credentials Grant Type</a> request).
 *
 * <p>The only time this result is returned is when an authenticated API client access your OAuth token URI, for
 * example {@code /oauth/token} and you've relayed the request for processing to the
 * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object) application.authenticateOauthRequest})
 * method.</p>
 *
 * <p>The created access token is available via the {@link #getTokenResponse() tokenResponse} property, and should be
 * returned to the client so the client may use the token for subsequent requests to your API.</p>
 *
 * <p>The {@link TokenResponse} JavaDoc shows example code of how this token might be returned to the client.</p>
 *
 * @since 1.0.RC
 */
public interface TokenOauthAuthenticationResult extends OauthAuthenticationResult {

    /**
     * The {@link com.stormpath.sdk.oauth.authz.TokenResponse} obtained as a result of a successful authentication
     * request execution.  See the the class {@link TokenOauthAuthenticationResult JavaDoc} and the
     * {@link TokenResponse} JavaDoc for an example of how the response might be returned to an API client.
     *
     * @return the {@link com.stormpath.sdk.oauth.authz.TokenResponse} obtained as a result of a successful
     *         authentication request execution, and which should be returned to the API client.
     */
    TokenResponse getTokenResponse();
}
