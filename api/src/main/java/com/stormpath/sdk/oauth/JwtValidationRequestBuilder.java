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
 * Utility class to build {@link JwtValidationRequest JwtValidationRequest} instances.
 *
 * @since 1.0.RC6
 */
public interface JwtValidationRequestBuilder {

    /**
     * Specifies the JWT that will be validated.
     *
     * @param jwt the String representation of the JWT to validate.
     * @return this instance for method chaining.
     */
    JwtValidationRequestBuilder setJwt(String jwt);

    /**
     * Builds a {@link JwtAuthenticationRequest JwtValidationRequest} instance based on the current state of the builder.
     *
     * @return a {@link JwtAuthenticationRequest} instance.
     */
    JwtValidationRequest build();
}
