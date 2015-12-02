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
 * A JWT-specific {@link Oauth2AuthenticationRequestBuilder}.
 *
 * @since 1.0.RC7
 */
public interface JwtAuthenticationRequestBuilder extends Oauth2AuthenticationRequestBuilder<JwtAuthenticationRequest> {

    /**
     * Setter for the jwt to be used in the {@link JwtAuthenticationRequest} that this builder will construct.
     *
     * @param jwt the jwt to be used in the {@link JwtAuthenticationRequest} that this builder will construct.
     * @return this instance for method chaining.
     */
    JwtAuthenticationRequestBuilder setJwt(String jwt);
}
