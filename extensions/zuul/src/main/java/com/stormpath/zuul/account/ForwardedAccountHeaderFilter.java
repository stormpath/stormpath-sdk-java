/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.zuul.account;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.AccountStringResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.zuul.filter.AppliedRequestHeaderFilter;

import javax.servlet.ServletRequest;

/**
 * An Zuul {@code pre} filter that intercepts requests and sets an outbound request header with a string representation
 * of the account currently associated with the request.  This allows destination/origin http servers to read the
 * request account's information by simply looking at a request header, without needing any Stormpath SDK if desired.
 * <p>
 * This filter will only execute if an account is available as determined by invoking its
 * {@link #setAccountResolver(AccountResolver) accountResolver}'s
 * {@link AccountResolver#hasAccount(ServletRequest) hasAccount} method. If there is an account, the account's string
 * representation will be obtained by calling the {@link #valueResolver}, a
 * {@link AccountStringResolver} instance by default.  If there is not an account associated with the
 * request, the header is not set at all.
 * </p>
 * <h5>Header Name</h5>
 * <p>If an account string is available, this filter sets the {@code X-Forwarded-Account} header with the account
 * string value.  The header name may be changed if desired by setting the superclass
 * {@link #setHeaderName(String) headerName} property.  If no account/string is available, no header will be set.
 * </p>
 *
 * @since 1.1.0
 */
public class ForwardedAccountHeaderFilter extends AppliedRequestHeaderFilter {

    public static final String DEFAULT_HEADER_NAME = "X-Forwarded-Account";

    private AccountResolver accountResolver;

    /**
     * Default constructor with the following default values configured:
     *
     * <table>
     *     <tr>
     *         <th>Property Name</th>
     *         <th>Property Value</th>
     *     </tr>
     *     <tr>
     *         <td>{@code headerName}</td>
     *         <td>{@code X-Forwarded-Account}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link #setAccountResolver(AccountResolver) accountResolver}</td>
     *         <td>a new {@link DefaultAccountResolver} instance</td>
     *     </tr>
     *     <tr>
     *         <td>{@link #setValueResolver(Resolver) valueResolver}</td>
     *         <td>a new {@link AccountStringResolver} instance</td>
     *     </tr>
     * </table>
     */
    public ForwardedAccountHeaderFilter() {
        setHeaderName(DEFAULT_HEADER_NAME);
        AccountResolver accountResolver = new DefaultAccountResolver();
        setAccountResolver(accountResolver);
        AccountStringResolver resolver = new AccountStringResolver();
        resolver.setAccountResolver(accountResolver);
        setValueResolver(resolver);
    }

    /**
     * Sets the {@code AccountResolver} used to determine if this filter should execute during a request or not.  The
     * default instance is a {@link DefaultAccountResolver}.
     *
     * @param accountResolver the {@code AccountResolver} used to determine if this filter should execute during a request or not.
     */
    public void setAccountResolver(AccountResolver accountResolver) {
        Assert.notNull(accountResolver, "AccountResolver cannot be null.");
        this.accountResolver = accountResolver;
    }

    /**
     * Returns {@link #setAccountResolver(AccountResolver) accountResolver}
     * {@link AccountResolver#hasAccount(ServletRequest) hasAccount}.  If there isn't an account available, the
     * filter will not execute, implying the {@link #setHeaderName(String) header} will not be set at all.
     *
     * @return {@link #setAccountResolver(AccountResolver) accountResolver}
     * {@link AccountResolver#hasAccount(ServletRequest) hasAccount}.
     */
    @Override
    public boolean shouldFilter() {
        return accountResolver.hasAccount(getRequestContext().getRequest());
    }
}
