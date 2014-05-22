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
 * The {@code ScopeFactory} interface provides a mechanism to define application-specific <code>scopes</code>. This
 * factory will be used during Oauth authentication to create the concrete application-specific scopes granted to the user.
 *
 * @since 1.0.RC
 */
public interface ScopeFactory {

    /**
     * Returns the set of Application-specific granted scopes for the account.
     * <p/>
     * The concrete implementation of this logic will be in charge of defining the granted permissions based on the
     * the available {@link AuthenticationResult} and the scope(s) requested during the authentication request (i.e.
     * <code>requestedScope</code>).
     *
     * @param result the result of the authentication obtained from Stormpath.
     * @param requestedScope the set of Application-specific permissions requested during the authentication request operation.
     * @return the set of Application-specific permissions granted to the account.
     */
    Set<String> createScope(AuthenticationResult result, Set<String> requestedScope);

}
