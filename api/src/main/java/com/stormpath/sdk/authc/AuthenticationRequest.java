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

/**
 * @since 0.1
 */
public interface AuthenticationRequest<P, C> {

    P getPrincipals();

    C getCredentials();

    String getHost();

    /**
     * Clears out (nulls) any identifying state, such as password bytes ({@code 0x00}), keys, etc, to eliminate the
     * possibility of memory access at a later time.
     */
    void clear();

    /**
     * Returns a specific {@code AccountStore} that should process this authentication
     * request, or {@code null} if the application's default
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication flow</a>
     * should execute.  If non-null, the account store must be assigned to the application sending the request.
     * <p/>
     * This is an optional property, so the default is {@code null}, reflecting an application's default authentication
     * flow.
     * <h3>Usage</h3>
     * Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of
     * assigned account stores: in this case specifying an account store will result in a direct (targeted)
     * authentication which would be faster because
     * Stormpath does not need to iteratively try each assigned account store
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as documented</a>.
     *
     * @return a specific {@code AccountStore} assigned to the Application that should process this authentication
     *         request (thereby bypassing the application's default
     *         <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication
     *         flow</a>), or {@code null} if the application's default account store authentication flow should execute.
     * @since 1.0.alpha
     */
    AccountStore getAccountStore();

}
