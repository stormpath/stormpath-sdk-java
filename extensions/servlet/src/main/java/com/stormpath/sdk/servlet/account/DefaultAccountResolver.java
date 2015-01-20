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
package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Default implementation of the {@link AccountResolver} interface.
 *
 * @since 1.0.RC3
 */
public class DefaultAccountResolver implements AccountResolver {

    public static final String REQUEST_ATTR_NAME = Account.class.getName();

    @Override
    public boolean hasAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Account account = findAccount(request);
        return account != null;
    }

    protected Account findAccount(ServletRequest request) {
        Object value = request.getAttribute(REQUEST_ATTR_NAME);
        if (value == null) {
            Assert.isInstanceOf(HttpServletRequest.class, request, "Only HttpServletRequests are supported.");
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession(false);
            if (session != null) {
                value = session.getAttribute(REQUEST_ATTR_NAME);
            }
        }
        if (value == null) {
            return null;
        }

        Assert.isInstanceOf(Account.class, value,
                            "Account attribute must be a " + Account.class.getName() + " instance.");
        return (Account) value;
    }

    @Override
    public Account getAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        return findAccount(request);
    }

    @Override
    public Account getRequiredAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Account account = findAccount(request);
        Assert.notNull(account, "The current request does not reflect an authenticated user.  " +
                                "Call 'hasAccount' to check if an authenticated user exists before " +
                                "calling this method.");
        return account;
    }
}
