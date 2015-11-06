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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.oauth.JwtAuthenticationResult;

/**
 * This builder is used to obtain an {@link com.stormpath.sdk.oauth.JwtAuthenticationResult JwtAuthenticationResult} object from the result obtained after a JWT Authentication is performed.
 *
 * @since 1.0.RC6
 */
public interface JwtAuthenticationResultBuilder  {

    /**
     * Creates a new {@code JwtAuthenticationResult} instance based on the current builder state.
     *
     * @return a new {@code JwtAuthenticationResult} instance based on the current builder state.
     */
    JwtAuthenticationResult build();
}
