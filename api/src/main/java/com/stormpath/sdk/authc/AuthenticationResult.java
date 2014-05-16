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
 * Main interface to be extended by any authentication result type supported by Stormpath.
 *
 * @see ApiAuthenticationResult
 * @see com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult
 * @see com.stormpath.sdk.oauth.authc.OauthAuthenticationResult
 * @since 0.1
 */
public interface AuthenticationResult extends Resource {

    /**
     * Returns the actual {@link Account} obtained as a result of a successful authentication request.
     *
     * @return the actual {@link Account} obtained as a result of a successful authentication request.
     */
    Account getAccount();

    /**
     * Allows an {@link AuthenticationResultVisitor authentication result visitor} to visit the concrete authentication
     * result indistinctively of the authentication request type used. For example, {@link ApiAuthenticationResult},
     * {@link com.stormpath.sdk.oauth.authc.OauthAuthenticationResult) or {@link com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult}
     *
     * @param visitor the visitor in charge of visiting the concrete authentication result
     * @see com.stormpath.sdk.authc.AuthenticationResultVisitor
     *
     * @since 1.0.RC
     */
    void accept(AuthenticationResultVisitor visitor);
}
