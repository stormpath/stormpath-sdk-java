/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Classes;

/**
 * @since 1.0.RC8
 */
public class RefreshTokenRequest implements AuthenticationRequest<String, char[]> {

    /**
     * Returns a new {@link RefreshTokenRequestBuilder} instance, used to construct {@link RefreshTokenRequest}s.
     *
     * @return a new {@link RefreshTokenRequestBuilder} instance, used to construct {@link RefreshTokenRequest}s.
     * @since 1.0.RC5
     */
    public static RefreshTokenRequestBuilder builder() {
        return (RefreshTokenRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.authc.DefaultRefreshTokenRequestBuilder");
    }

    private String refreshToken;

    /**
     * Constructs a new {@code RefreshTokenRequest} with the specified {@code refreshToken}.
     *
     * @param refreshToken the refresh token to use for authentication and new access token creation
     */
    @Deprecated
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    @Deprecated
    public String getPrincipals() {
        throw new UnsupportedOperationException("getPrincipals() method hasn't been implemented.");
    }

    @Override
    @Deprecated
    public char[] getCredentials() {
        throw new UnsupportedOperationException("getCredentials() method hasn't been implemented.");
    }

    @Override
    @Deprecated
    public String getHost() {
        throw new UnsupportedOperationException("getHost() method hasn't been implemented.");
    }

    @Override
    public AccountStore getAccountStore() {
        throw new UnsupportedOperationException("getAccountStore() method hasn't been implemented.");
    }

    @Override
    public AuthenticationOptions getResponseOptions() {
        throw new UnsupportedOperationException("getResponseOptions() method hasn't been implemented.");
    }

    /**
     * Clears out (nulls) the refresh token.
     */
    @Override
    @Deprecated
    public void clear() {
        this.refreshToken = null;
    }

}
