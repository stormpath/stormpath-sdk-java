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
 * Builder that can construct {@link OAuthRefreshTokenRequestAuthentication} instances.
 *
 * @since 1.0.RC7
 */
public interface OAuthRefreshTokenRequestAuthenticationBuilder extends OAuthRequestAuthenticationBuilder<OAuthRefreshTokenRequestAuthentication> {

    /**
     * Specifies the refresh token that will be used to obtain a new Access Token without requiring credentials.
     *
     * @param refreshToken the refresh token that will be used to create a new authentication token.
     * @return this instance for method chaining.
     */
    OAuthRefreshTokenRequestAuthenticationBuilder setRefreshToken(String refreshToken);

    /**
     * Builds a new {@link OAuthRefreshTokenRequestAuthentication RefreshGrantRequest} instance based on the current builder state.
     *
     * @return the {@link OAuthRefreshTokenRequestAuthentication} object used to create a new authentication token
     */
    OAuthRefreshTokenRequestAuthentication build();
}
