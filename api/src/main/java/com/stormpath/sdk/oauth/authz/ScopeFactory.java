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
package com.stormpath.sdk.oauth.authz;

import com.stormpath.sdk.authc.AuthenticationResult;

import java.util.Set;

/**
 * A {@code ScopeFactory} allows you to define application-specific <code>scopes</code> (aka 'permissions') granted to
 * a successfully authenticated {@link com.stormpath.sdk.account.Account Account} making an OAuth request.  For example:
 * <pre>
 * // Your implementation knows how to inspect an {@link com.stormpath.sdk.account.Account Account} resource and return
 * // a collection of OAuth scope strings (permissions) that should be considered as assigned to that account for the
 * // duration of the request:
 * ScopeFactory <b>myScopeFactory</b> = new MyScopeFactory();
 *
 * OauthAuthenticationResult result = application.authenticateOauth(httpRequest)
 *     .<b>{@link com.stormpath.sdk.oauth.authc.OauthRequestAuthenticator#using(ScopeFactory) using}(myScopeFactory)</b>
 *     ...
 *     .execute();
 * </pre>
 * <p>
 * Specifying a {@code ScopeFactory} is optional. It is necessary only if you want to perform OAuth authorization
 * (access control) checks after the OAuth caller is authenticated.
 * </p>
 *
 * @since 1.0.RC
 */
public interface ScopeFactory {

    /**
     * Returns the set of Application-specific granted scopes (permissions) for a successfully authenticated account
     * making an OAuth request.
     * <p>
     * Implementations will likely inspect the authenticated account (via
     * {@link com.stormpath.sdk.authc.AuthenticationResult#getAccount() result.getAccount()}) and the
     * {@code requestedScopes} and return a set of the scopes that are actually granted. That is, an implementation is
     * free to return whatever scopes it wishes to consider 'granted' to the specified {@code account}, regardless of
     * what may be requested.
     * </p>
     *
     * @param result the authentication result reflecting the already-authenticated account that made the OAuth request.
     * @param requestedScopes the set of Application-specific permissions requested in the authentication request.
     * @return the actual set of Application-specific scopes/permissions granted to the account.
     */
    Set<String> createScope(AuthenticationResult result, Set<String> requestedScopes);
}
