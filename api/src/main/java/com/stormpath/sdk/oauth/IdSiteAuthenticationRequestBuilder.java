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
 * Builder that can construct {@link IdSiteAuthenticationRequest} instances.
 *
 * @since 1.0.RC8.2
 */
public interface IdSiteAuthenticationRequestBuilder extends OAuthRequestAuthenticationBuilder<IdSiteAuthenticationRequest> {

    /**
     * Specifies the Id Site token that will be used to obtain an Access Token.
     *
     * @param token the token that will be used to obtain an Access Token.
     * @return this instance for method chaining.
     */
    IdSiteAuthenticationRequestBuilder setToken(String token);

    /**
     * Builds a new {@link OAuthRefreshTokenRequestAuthentication RefreshGrantRequest} instance based on the current builder state.
     *
     * @return the {@link OAuthRefreshTokenRequestAuthentication} object used to create a new authentication token
     */
    IdSiteAuthenticationRequest build();
}
