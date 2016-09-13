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
package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.json.JsonFunction;
import com.stormpath.sdk.servlet.json.ResourceJsonFunction;
import com.stormpath.sdk.servlet.mvc.ResourceMapFunction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@code Resolver} that locates the account associated with the current request using an
 * {@link AccountResolver}, converts the located account to a String via a
 * {@link Function Function&lt;Account,String&gt;}, and returns that String to the caller.
 * <p>
 * If an account cannot be resolved (for example, the request is not authenticated or a user not remembered
 * from a previous authentication), {@code null} is returned to indicate no account could be located.
 * </p>
 * <p>
 * If an account is located, the default configuration returns a JSON representation of the account.  A
 * different account String representation can be returned by configuring a different
 * {@link #setAccountStringFunction(Function) accountStringFunction}.
 * </p>
 *
 * @see #setAccountResolver(AccountResolver)
 * @see #setAccountStringFunction(Function)
 * @since 1.1.0
 */
public class AccountStringResolver implements Resolver<String> {

    private Function<Account, String> accountStringFunction;

    private AccountResolver accountResolver;

    /**
     * Default constructor that uses a {@link ResourceJsonFunction ResourceJsonFunction} to convert any discovered
     * account to a string.
     */
    public AccountStringResolver() {
        this.accountResolver = new DefaultAccountResolver();
        ResourceMapFunction<Account> mapFunction = new ResourceMapFunction<>();
        mapFunction.setIncludedFields(Collections.toSet("groups")); //represent this one collection by default
        this.accountStringFunction = new ResourceJsonFunction<>(mapFunction, new JsonFunction<>());
    }

    /**
     * Sets the function used to convert a discovered {@link Account} to a String representation.
     * <p>Unless overridden, the default instance is a {@link ResourceJsonFunction}, which returns a JSON
     * representation of the account.</p>
     *
     * @param accountStringFunction the function used to convert a discovered {@link Account} to a String representation.
     */
    public void setAccountStringFunction(Function<Account, String> accountStringFunction) {
        Assert.notNull(accountStringFunction, "accountStringFunction cannot be null.");
        this.accountStringFunction = accountStringFunction;
    }

    /**
     * Sets the account resolver to use to look a request's associated account.  Unless overridden, the default
     * instance is a {@link DefaultAccountResolver}.  Once located, the account will be converted to a string
     * using the {@link #accountStringFunction}.
     *
     * @param accountResolver the account resolver to use to look a request's associated account.
     */
    public void setAccountResolver(AccountResolver accountResolver) {
        Assert.notNull(accountResolver, "accountResolver cannot be null.");
        this.accountResolver = accountResolver;
    }

    /**
     * Returns a string representation of the request's associated account or {@code null} if an account is not
     * available.
     * <p>
     * This method locates the account associated with the current request using the
     * {@link #accountResolver}, converts the located account to a String with the
     * {@link #accountStringFunction}, and returns the resulting String.
     * </p>
     * <p>
     * If an account cannot be resolved (for example, the request is not authenticated or a user not remembered
     * from a previous authentication), {@code null} is returned to indicate an account is not available.
     * </p>
     *
     * @param request  the inbound request
     * @param response the outbound response
     * @return a string representation of the request's associated account or {@code null} if an account is not
     * available.
     */
    @Override
    public String get(HttpServletRequest request, HttpServletResponse response) {
        Account account = accountResolver.getAccount(request);
        if (account != null) {
            return accountStringFunction.apply(account);
        }
        return null;
    }
}
