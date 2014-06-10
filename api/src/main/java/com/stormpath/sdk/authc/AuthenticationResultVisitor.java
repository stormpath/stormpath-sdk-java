/*
 * Copyright 2014 Stormpath, Inc.
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

import com.stormpath.sdk.oauth.authc.AccessTokenResult;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;

/**
 * A <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a> interface that allows one to
 * react to different types of authentication results, particularly those reflecting successfully authenticated API
 * requests (e.g. using {@link com.stormpath.sdk.api.ApiKey ApiKeys}.
 *
 * @see com.stormpath.sdk.api.ApiKey ApiKey
 * @see ApiAuthenticationResult
 * @see OauthAuthenticationResult
 * @see com.stormpath.sdk.oauth.authc.AccessTokenResult
 * @since 1.0.RC
 */
public interface AuthenticationResultVisitor {

    /**
     * Allows handling of a successful (usually username/password)-based authentication attempt.
     *
     * @param result the {@link AuthenticationResult} instance reflecting a successful username/password authentication
     *               attempt.
     */
    void visit(AuthenticationResult result);

    /**
     * Invoked when an API request has been successfully authenticated with an
     * {@link com.stormpath.sdk.api.ApiKey ApiKey}, usually as a result of HTTP Basic Authentication.
     *
     * @param result the {@link ApiAuthenticationResult} representing the successful API authentication attempt.
     */
    void visit(ApiAuthenticationResult result);

    /**
     * Invoked when an API request has been successfully authenticated using the OAuth 2 protocol.
     *
     * @param result the {@link OauthAuthenticationResult} representing the successful OAuth-based API authentication
     *               attempt.
     */
    void visit(OauthAuthenticationResult result);

    /**
     * Invoked when an API request has been successfully authenticated using the OAuth 2 protocol and the HTTP response
     * must return a new OAuth 2 Access Token to the caller.  This almost always occurs as a result of handling a
     * request to the application's OAuth 2 Access Token endpoint, for example, {@code /oauth2/token}.
     *
     * @param result the {@link com.stormpath.sdk.oauth.authc.AccessTokenResult} representing the successful OAuth-based API
     *               authentication attempt that must respond with an OAuth 2 Access Token response.
     */
    void visit(AccessTokenResult result);
}
