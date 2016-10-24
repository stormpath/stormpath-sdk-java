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

import com.stormpath.sdk.directory.AccountStore;

/**
 * A Builder to construct username/password-based {@link AuthenticationRequest}s.
 *
 * It optionally supports {@link #inAccountStore(AccountStore) specifying an specific account store} as well for
 * targeted authentication behavior.
 *
 * @see UsernamePasswordRequests#builder() UsernamePasswordRequests.builder()
 * @since 1.0.RC5
 */
public interface UsernamePasswordRequestBuilder extends AuthenticationRequestBuilder<UsernamePasswordRequestBuilder> {

    /**
     * Specifies the username or email that will be used to authenticate an account.
     *
     * @param usernameOrEmail the username or email that will be used to authenticate an account.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setUsernameOrEmail(String usernameOrEmail);

    /**
     * Specifies the password of the account that will be authenticated.
     *
     * @param password the account's raw password.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setPassword(String password);

    /**
     * Specifies the password of the account that will be authenticated.
     *
     * @param password the account's raw password.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setPassword(char[] password);

    /**
     * Specifies the host from where the end-user is accessing your application. This property is optional.
     *
     * @param host the host from where the end-user is accessing your application.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setHost(String host);

    /**
     * Sets a specific {@code AccountStore} that should process this authentication request, or {@code null} if the
     * application's default <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store
     * authentication flow</a> should execute.  If non-null, the account store must be assigned to the application
     * sending the request.
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
     * <p>Note that this property will be invalidated if a property is set in {@link #setOrganizationNameKey(String)}. Setting the latter
     * will cause this property to be unused.</p>
     *
     * @param accountStore a specific {@code AccountStore} that should process this authentication request, or
     *                     {@code null} if the application's default
     *                     <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store
     *                     authentication flow</a> should execute.  If non-null, the account store must be assigned
     *                     to the application sending the request.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder inAccountStore(AccountStore accountStore);

    /**
     * Ensures that when the response is obtained, it will be retrieved with the specified options. This enhances performance
     * by leveraging a single request to retrieve multiple related resources you know you will use.
     * <p>For example,
     * it can be used to have the {@link com.stormpath.sdk.account.Account Account} resource automatically expanded in the returned result.
     * </p>
     *
     * @param options the specific {@code BasicAuthenticationOptions} that will be used to customize the authentication response.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder withResponseOptions(BasicAuthenticationOptions options);

    /**
     * Sets the {@link com.stormpath.sdk.organization.Organization#setNameKey(String) Organization Name Key} to be used as the account store.
     * <p>Note that this property cannot be used in conjunction to the {@link #inAccountStore(AccountStore)} method. When the former is used it
     * will invalidate any value set via the latter.</p>
     *
     * @since 1.2.0
     * @param orgNameKey An organization name key
     * @return the instance of the method chaining
     */
    UsernamePasswordRequestBuilder setOrganizationNameKey(String orgNameKey);

}
