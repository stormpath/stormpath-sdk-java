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
 * This class is used to authenticate a Json Web Token against Stormpath. For example:
 * <pre>
 * Application app = obtainApplication();
 * JwtAuthenticationRequest authRequest = Oauth2Requests.JWT_AUTHENTICATION_REQUEST
 *      .builder()
 *      .setJwt(jwt)
 *      .build()
 * JwtAuthenticationResult result = Authenticators.JWT_AUTHENTICATOR.forApplication(app).authenticate(authRequest)
 * </pre>
 *
 * This validation is always performed against Stormpath server, if you need to validate the token locally, use {@link JwtValidator#validate(JwtValidationRequest)} method instead.
 *
 * @see RefreshGrantAuthenticator
 * @see PasswordGrantAuthenticator
 *
 * @since 1.0.RC6
 */
public interface JwtAuthenticator extends Authenticator<JwtAuthenticationResult> {

    /**
     * Authenticates a JWT against Stormpath server.

     * @param jwtRequest the {@link JwtAuthenticationRequest JwtAuthenticationRequest} instance containing the information required for the JWT authentication.
     * @return a {@link JwtAuthenticationResult JwtAuthenticationResult} instance containing the resultant {@link AccessToken AccessToken}.
     */
    JwtAuthenticationResult authenticate(Oauth2AuthenticationRequest jwtRequest);
}
