/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.authc;

/**
 * A Builder to construct {@link com.stormpath.sdk.authc.RefreshTokenRequest RefreshTokenRequest}s.
 *
 * @see com.stormpath.sdk.authc.RefreshTokenRequest#builder()
 * @since 1.0.RC8
 */
public interface RefreshTokenRequestBuilder extends AuthenticationRequestBuilder<RefreshTokenRequestBuilder> {

    /**
     * Specifies the String representation of the Refresh Token that will be used to create a new Access Token.
     *
     * @param refreshToken the username or email that will be used to authenticate an account.
     * @return this instance for method chaining.
     */
    RefreshTokenRequestBuilder setRefreshToken(String refreshToken);
}
