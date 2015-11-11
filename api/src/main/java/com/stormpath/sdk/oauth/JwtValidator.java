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
 * This class is used to validate a Json Web Token, either locally or against Stormpath server.
 * The local validation verifies the JWT is correctly signed, for example:
 * <pre>
 * Application app = obtainApplication();
 * JwtValidationRequest validationRequest = Oauth2Requests.JWT_VALIDATION_REQUEST.builder()
 *      .setJwt(result.getAccessTokenString())
 *      .withLocalValidation()
 *      .build()
 * boolean result = Validators.jwtValidator.forApplication(app).validate(validationRequest)
 * </pre>
 *
 * Validation against Stormpath server:
 * <pre>
 * Application app = obtainApplication();
 * JwtValidationRequest validationRequest = Oauth2Requests.JWT_VALIDATION_REQUEST.builder()
 *      .setJwt(result.getAccessTokenString())
 *      .build()
 * boolean result = Validators.jwtValidator.forApplication(app).validate(validationRequest)
 * </pre>
 *
 * @see PasswordGrantAuthenticator
 * @see RefreshGrantAuthenticator
 *
 * @since 1.0.RC6
 */

public interface JwtValidator {

    /**
     * Validates a Json Web Token, either locally or against Stormpath server.
     *
     *
     * @param jwtValidationRequest the {@link JwtValidationRequest JwtValidationRequest} instance containing the information required for the JWT validation.
     * @return true if the JWT could be successfully validated.
     */
    boolean validate(JwtValidationRequest jwtValidationRequest);
}
