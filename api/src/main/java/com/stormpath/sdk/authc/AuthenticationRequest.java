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
 * An authentication request represents all necessary information to authenticate a specific account.
 *
 * <h4>Usage</h4>
 *
 * <p>While there can be multiple implementations of this interface (typically constructed by type-specific builders)
 * to reflect different type of authentication attempts, the most common scenario is when a user logs in to your
 * application with username-password authentication.  For example:</p>
 *
 * <pre>
 * String username = getUsername(httpServletRequest); //implement me
 * String password = getPassword(httpServletRequest); //implement me
 *
 * AuthenticationRequest authcRequest = {@link UsernamePasswordRequests#builder() UsernamePasswordRequests.builder()}.setUsername(username).setPassword(password).build();
 *
 * myApplication.{@link com.stormpath.sdk.application.Application#authenticateAccount(AuthenticationRequest)
 * authenticateAccount}(authcRequest);
 *
 * </pre>
 *
 * @see com.stormpath.sdk.authc.UsernamePasswordRequests
 * @see com.stormpath.sdk.application.Application#authenticateAccount(AuthenticationRequest) Application.authenticateAccount(authcRequest)
 * @since 0.1
 */
public interface AuthenticationRequest<P, C> {

    /**
     * Returns the principal(s) (identifying information) that reflects the specific Account to be authenticated.  For
     * example, a username or email address.
     *
     * @return the principal(s) (identifying information) that reflects the specific Account to be authenticated.
     */
    P getPrincipals();

    /**
     * Returns the credentials (information that proves authenticity) of the the specific Account to be authenticated.
     * For example, a password.
     *
     * @return the credentials (information that proves authenticity) of the the specific Account to be authenticated.
     */
    C getCredentials();

    /**
     * Returns the host address (name or ip address) from where the authentication attempt is initiated.
     *
     * @return the host address (name or ip address) from where the authentication attempt is initiated.
     */
    String getHost();

    /**
     * Clears out (nulls) any identifying state, such as password bytes ({@code 0x00}), keys, etc, to eliminate the
     * possibility of memory access at a later time.
     */
    void clear();

    /**
     * Returns a specific {@code AccountStore} that should process this authentication request, or {@code null} if the
     * application's default <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account
     * store authentication flow</a> should execute.  If non-null, the account store must be assigned to the application
     * sending the request.
     *
     * <p>This is an optional property, so the default is {@code null}, reflecting an application's default
     * authentication flow.</p>
     *
     * <h4>Usage</h4>
     *
     * <p>Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of assigned
     * account stores, common in multi-tenant applications: in this case specifying an account store will result in a
     * direct (targeted) authentication which would be faster because Stormpath does not need to iteratively try each
     * assigned account store <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as
     * documented</a>.</p>
     *
     * @return a specific {@code AccountStore} assigned to the Application that should process this authentication
     * request (thereby bypassing the application's default <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account
     * store authentication flow</a>), or {@code null} if the application's default account store authentication flow
     * should execute.
     * @since 1.0.alpha
     */
    AccountStore getAccountStore();

    /**
     * Returns the {@link AuthenticationOptions} to be used in this AuthenticationRequest used to customize the response.
     * <p>For example,
     * it can be used to have the {@link com.stormpath.sdk.account.Account Account} resource automatically expanded in the returned result.
     * </p>
     *
     * @return the {@code AuthenticationOptions} that will be used to customize the response.
     * @since 1.0.RC5
     */
    AuthenticationOptions getResponseOptions();

    /**
     * Returns the {@link com.stormpath.sdk.organization.Organization} name key to be used as the account store.
     *
     * @since 1.2.0
     * @return the organization name key
     */
    String getOrganizationNameKey();

}
