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
 * This class is used to authenticate a Json Web Token both locally or against Stormpath. For example:
 * <pre>
 * Application app = obtainApplication();
 * JwtAuthenticationRequest jwtAuthenticationRequest = Authenticators.JWT_AUTHENTICATOR.builder()
 *      .setJwt(result.getAccessTokenString())
 *      .forLocalValidation()
 *      .build();
 * JwtAuthenticationResult result = app.authenticate(jwtAuthenticationRequest);
 * </pre>
 * Note that to validate the token locally, the builder provides the {@code JwtAuthenticationRequest#forLocalValidation} method. When not specified, the JWT will be validated against Stormpath.
 *
 * @see RefreshGrantAuthenticator
 * @see PasswordGrantAuthenticator
 *
 * @since 1.0.RC6
 */
public interface JwtAuthenticator {

    /**
     * This method can be used to authenticate a JWT.
     * @param jwtRequest the {@link JwtAuthenticationRequest JwtAuthenticationRequest} instance containing the information required for the JWT authentication.
     * @return a {@link JwtAuthenticationResult JwtAuthenticationResult} instance containing the resultant {@link AccessToken AccessToken}.
     */
    JwtAuthenticationResult authenticate(JwtAuthenticationRequest jwtRequest);
}
