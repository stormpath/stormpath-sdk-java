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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Resource;

/**
 * An {@code AuthenticationResult} represents the return value of an
 * {@link com.stormpath.sdk.application.Application#authenticateAccount(AuthenticationRequest) Application
 * authentiation attempt}.  The successfully authenticated account may be obtained by calling {@link #getAccount()}.
 *
 * <h3>Different Results</h3>
 * <p>A simple account username/password request will result in an {@code AuthenticationResult}, but so will
 * authentication attempts by ApiKey ({@link com.stormpath.sdk.api.ApiAuthenticationResult}), OAuth Bearer Token,
 * ({@link com.stormpath.sdk.oauth.OAuthAuthenticationResult OAuthAuthenticationResult}, or OAuth
 * via ApiKey as a <a href="http://tools.ietf.org/html/rfc6749#section-2.3.1">Client Credentials Grant Type</a> request
 * ({@link com.stormpath.sdk.oauth.AccessTokenResult AccessTokenResult}).</p>
 *
 * <p>
 * While all of these results allow you to access the calling account via {@link #getAccount()}, sometimes you may wish
 * to perform specific logic based on the type of result returned.  For this, you can use the type-safe
 * {@link AuthenticationResultVisitor} interface.  For example:
 * <pre>
 * AuthenticationResult result = application.authenticateApiRequest(request);
 *
 * result.accept(new AuthenticationResultVisitor() {
 *
 *     &#64;Override
 *     public void accept(AuthenticationResult result) {
 *         //do something as a result of a 'normal' successful username/password authentication
 *     }
 *
 *     &#64;Override
 *     public void accept(ApiAuthenticationResult result) {
 *         //do something as a result of successful ApiKey-based authentication
 *     }
 *
 *     ... etc ...
 *
 * });
 * </pre>
 * </p>
 *
 * <p>The visitor patten replaces many if-then-else statements with a type-safe interface to guarantee no conditions
 * will be missed at compile time.</p>
 *
 * @see com.stormpath.sdk.api.ApiAuthenticationResult
 * @see com.stormpath.sdk.oauth.OAuthAuthenticationResult OAuthAuthenticationResult
 * @see com.stormpath.sdk.oauth.AccessTokenResult AccessTokenResult
 * @since 0.1
 */
public interface AuthenticationResult extends Resource {

    /**
     * Returns the successfully authenticated {@link Account}.
     *
     * @return the successfully authenticated {@link Account}.
     */
    Account getAccount();

    /**
     * Allows an {@link AuthenticationResultVisitor authentication result visitor} to visit the concrete authentication
     * result indistinctively of the authentication request type used. For example, {@link com.stormpath.sdk.api.ApiAuthenticationResult},
     * {@link com.stormpath.sdk.oauth.OAuthAuthenticationResult ) or
     * {@link com.stormpath.sdk.oauth.AccessTokenResult }
     *
     * @param visitor the visitor in charge of visiting the concrete authentication result
     * @see AuthenticationResultVisitor
     * @since 1.0.RC
     */
    void accept(AuthenticationResultVisitor visitor);
}
