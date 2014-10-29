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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.DefaultRequestAccountResolver;
import com.stormpath.sdk.servlet.config.Config;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//not a configurable filter - always executes immediately after the StormpathFilter but before other user-configured filters.
public class AccountAccessorFilter extends HttpFilter {

    private static final String ACCOUNT_ACCESSOR_CONFIG_PROP_NAME = "stormpath.web.account.discovery";

    private static final Accessor<Account> COOKIE_ACCOUNT_ACCESSOR = new CookieAccountAccessor();
    private static final Accessor<Account> HEADER_ACCOUNT_ACCESSOR = new AuthorizationHeaderAccountAccessor();
    private static final Accessor<Account> SESSION_ACCOUNT_ACCESSOR = new SessionAccountAccessor();

    //not strictly thread-safe, but it is only really manipulated by a single thread startup:
    private List<Accessor<Account>> accessors;

    @Override
    protected void onInit() throws ServletException {

        Config config = getConfig();

        String value = config.get(ACCOUNT_ACCESSOR_CONFIG_PROP_NAME);

        String[] accessorNames = Strings.commaDelimitedListToStringArray(value);

        List<Accessor<Account>> accessors = new ArrayList<Accessor<Account>>(accessorNames.length);

        for(String name : accessorNames) {

            Accessor<Account> accessor;

            if ("header".equalsIgnoreCase(name)) {
                accessor = HEADER_ACCOUNT_ACCESSOR;
            } else if ("cookie".equalsIgnoreCase(name)) {
                accessor = COOKIE_ACCOUNT_ACCESSOR;
            } else if ("session".equalsIgnoreCase(name)) {
                accessor = SESSION_ACCOUNT_ACCESSOR;
            } else {
                String msg = "Unrecognized " + ACCOUNT_ACCESSOR_CONFIG_PROP_NAME + " config value: " + name;
                throw new IllegalArgumentException(msg);
            }

            if (!accessors.contains(accessor)) {
                accessors.add(accessor);
            }
        }

        this.accessors = Collections.unmodifiableList(accessors);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        for(Accessor<Account> accessor : accessors) {
            Account account = accessor.get(request, response);
            if (account != null) {
                request.setAttribute(DefaultRequestAccountResolver.REQUEST_ATTR_NAME, account);
                break;
            }
        }

        chain.doFilter(request, response);
    }
}
