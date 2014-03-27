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
import com.stormpath.sdk.lang.Assert;

/**
 * A {@code UsernamePasswordRequest} is an {@code AuthenticationRequest} that represents a username (or email) +
 * password pair.  It optionally supports
 * {@link #setAccountStore(com.stormpath.sdk.directory.AccountStore) targeting an specific account store} as well for
 * customized authentication behavior.
 *
 * @since 0.2
 */
public class UsernamePasswordRequest implements AuthenticationRequest<String, char[]> {

    private String username;
    private char[] password;
    private String host;
    private AccountStore accountStore;

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @see #UsernamePasswordRequest(String, String, AccountStore)
     */
    public UsernamePasswordRequest(String usernameOrEmail, String password) {
        this(usernameOrEmail, password, null, null);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @see #UsernamePasswordRequest(String, String, AccountStore)
     */
    public UsernamePasswordRequest(String usernameOrEmail, char[] password) {
        this(usernameOrEmail, password, null, null);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}
     * for an end-user accessing the application from the specified {@code host}.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param host            the host from where the end-user is accessing your application
     * @see #UsernamePasswordRequest(String, String, String, AccountStore)
     */
    public UsernamePasswordRequest(String usernameOrEmail, String password, String host) {
        this(usernameOrEmail, password, host, null);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}
     * for an end-user accessing the application from the specified {@code host}.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param host            the host from where the end-user is accessing your application
     * @see #UsernamePasswordRequest(String, char[], String, AccountStore)
     */
    public UsernamePasswordRequest(String usernameOrEmail, char[] password, String host) {
        this(usernameOrEmail, password, host, null);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified username (or email) and password, ensuring the
     * authentication request will be directly targeted at the application's specified {@link AccountStore} (thereby
     * bypassing the application's default
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication
     * flow</a>).  The {@code AccountStore} must be assigned to the application sending the request.
     * <h4>Usage</h4>
     * Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of
     * assigned account stores: in this case specifying an account store will result in a direct (targeted)
     * authentication which would be faster because
     * Stormpath does not need to iteratively try each assigned account store
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as documented</a>.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param accountStore    an application's specific AccountStore that should process the authentication request,
     *                        thereby bypassing the application's default
     *                        <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication flow</a>.
     */
    public UsernamePasswordRequest(String usernameOrEmail, String password, AccountStore accountStore) {
        this(usernameOrEmail, password, null, accountStore);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified username (or email) and password, ensuring the
     * authentication request will be directly targeted at the application's specified {@link AccountStore} (thereby
     * bypassing the application's default
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication
     * flow</a>).  The {@code AccountStore} must be assigned to the application sending the request.
     * <h4>Usage</h4>
     * Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of
     * assigned account stores: in this case specifying an account store will result in a direct (targeted)
     * authentication which would be faster because
     * Stormpath does not need to iteratively try each assigned account store
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as documented</a>.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param accountStore    an application's specific AccountStore that should process the authentication request,
     *                        thereby bypassing the application's default
     *                        <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication flow</a>.
     */
    public UsernamePasswordRequest(String usernameOrEmail, char[] password, AccountStore accountStore) {
        this(usernameOrEmail, password, null, accountStore);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}
     * for an end-user accessing the application from the specified {@code host}, also ensuring the
     * authentication request will be directly targeted at the application's specified {@link AccountStore} (thereby
     * bypassing the application's default
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication
     * flow</a>).  The {@code AccountStore} must be assigned to the application sending the request.
     * <h4>Usage</h4>
     * Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of
     * assigned account stores: in this case specifying an account store will result in a direct (targeted)
     * authentication which would be faster because
     * Stormpath does not need to iteratively try each assigned account store
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as documented</a>.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param host            the host from where the end-user is accessing your application
     * @param accountStore    an application's specific AccountStore that should process the authentication request,
     *                        thereby bypassing the application's default
     *                        <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication flow</a>.
     */
    public UsernamePasswordRequest(String usernameOrEmail, String password, String host, AccountStore accountStore) {
        this(usernameOrEmail, password != null ? password.toCharArray() : "".toCharArray(), host, accountStore);
    }

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}
     * for an end-user accessing the application from the specified {@code host}, also ensuring the
     * authentication request will be directly targeted at the application's specified {@link AccountStore} (thereby
     * bypassing the application's default
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication
     * flow</a>).  The {@code AccountStore} must be assigned to the application sending the request.
     * <h4>Usage</h4>
     * Most applications will not need to specify an {@code AccountStore} during an authentication attempt, but
     * specifying one can be useful in some cases, such as when an Application has many (dozens or hundreds) of
     * assigned account stores: in this case specifying an account store will result in a direct (targeted)
     * authentication which would be faster because
     * Stormpath does not need to iteratively try each assigned account store
     * <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">as documented</a>.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     * @param host            the host from where the end-user is accessing your application
     * @param accountStore    an application's specific AccountStore that should process the authentication request,
     *                        thereby bypassing the application's default
     *                        <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store authentication flow</a>.
     */
    public UsernamePasswordRequest(String usernameOrEmail, char[] password, String host, AccountStore accountStore) {
        this.username = usernameOrEmail;
        this.password = password;
        this.host = host;
        this.accountStore = accountStore;
    }

    @Override
    public String getPrincipals() {
        return username;
    }

    @Override
    public char[] getCredentials() {
        return password;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * Returns a specific {@code AccountStore} that should process this authentication request, or {@code null} if the
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
     *
     * @return a specific {@code AccountStore} that should process this authentication request, or {@code null} if the
     *         application's default <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">
     *         account store authentication flow</a> should execute.  If non-null, the account store must be assigned
     *         to the application sending the request.
     * @since 1.0.alpha
     */
    @Override
    public AccountStore getAccountStore() {
        return this.accountStore;
    }

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
     *
     * @param accountStore a specific {@code AccountStore} that should process this authentication request, or
     *                     {@code null} if the application's default
     *                     <a href="http://docs.stormpath.com/java/product-guide/#account-store-mappings">account store
     *                     authentication flow</a> should execute.  If non-null, the account store must be assigned
     *                     to the application sending the request.
     * @since 1.0.alpha
     */
    public void setAccountStore(AccountStore accountStore) {
        Assert.notNull(accountStore, "accountStore cannot be null.");
        this.accountStore = accountStore;
    }

    /**
     * Clears out (nulls) the username, password, and host.  The password bytes are explicitly set to
     * <tt>0x00</tt> to eliminate the possibility of memory access at a later time.
     */
    @Override
    public void clear() {
        this.username = null;
        this.host = null;
        this.accountStore = null;

        char[] password = this.password;
        this.password = null;

        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = 0x00;
            }
        }

    }

}
