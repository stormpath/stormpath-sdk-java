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
 * This class is used to perform a local validation of a Json Web Token. For example:
 * <pre>
 * Application app = obtainApplication();
 * JwtValidationRequest validationRequest = Oauth2Requests.JWT_VALIDATION_REQUEST
 *      .builder()
 *      .setJwt(jwt)
 *      .build()
 * boolean result = Validators.jwtValidator.forApplication(app).validate(validationRequest)
 * </pre>
 *
 * This is a local validation that verifies the JWT by making sure its sign is correct and it can be trusted,
 * if you need to authenticate the token against Stormpath server, use {@link JwtAuthenticator#authenticate(Oauth2AuthenticationRequest)} method instead.
 *
 * @since 1.0.RC6
 */

public interface JwtValidator {

    /**
     * Performs a local validation of the JWT.
     *
     * @param jwtValidationRequest the {@link JwtValidationRequest JwtValidationRequest} instance containing the information required for the JWT validation.
     * @return true if the JWT could be validated.
     */
    boolean validate(JwtValidationRequest jwtValidationRequest);
}
