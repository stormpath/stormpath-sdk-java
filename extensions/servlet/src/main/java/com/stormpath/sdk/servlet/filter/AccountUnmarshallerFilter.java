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
public class AccountUnmarshallerFilter extends HttpFilter {

    private static final String ACCOUNT_UNMARSHALLER_CONFIG_NAME = "stormpath.web.account.discovery";

    private static final Unmarshaller<Account> COOKIE_ACCOUNT_UNMARSHALLER = new CookieAccountUnmarshaller();
    private static final Unmarshaller<Account> HEADER_ACCOUNT_UNMARSHALLER = new AuthorizationHeaderAccountUnmarshaller();
    private static final Unmarshaller<Account> SESSION_ACCOUNT_UNMARSHALLER = new SessionAccountUnmarshaller();

    //not strictly thread-safe, but it is only really manipulated by a single thread startup:
    private List<Unmarshaller<Account>> unmarshallers;

    @Override
    protected void onInit() throws ServletException {

        Config config = getConfig();

        String value = config.get(ACCOUNT_UNMARSHALLER_CONFIG_NAME);

        String[] unmarshallerNames = Strings.commaDelimitedListToStringArray(value);

        List<Unmarshaller<Account>> unmarshallers = new ArrayList<Unmarshaller<Account>>(unmarshallerNames.length);

        for(String name : unmarshallerNames) {

            Unmarshaller<Account> unmarshaller;

            if ("header".equalsIgnoreCase(name)) {
                unmarshaller = HEADER_ACCOUNT_UNMARSHALLER;
            } else if ("cookie".equalsIgnoreCase(name)) {
                unmarshaller = COOKIE_ACCOUNT_UNMARSHALLER;
            } else if ("session".equalsIgnoreCase(name)) {
                unmarshaller = SESSION_ACCOUNT_UNMARSHALLER;
            } else {
                String msg = "Unrecognized " + ACCOUNT_UNMARSHALLER_CONFIG_NAME + " config value: " + name;
                throw new IllegalArgumentException(msg);
            }

            if (!unmarshallers.contains(unmarshaller)) {
                unmarshallers.add(unmarshaller);
            }
        }

        this.unmarshallers = Collections.unmodifiableList(unmarshallers);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        for(Unmarshaller<Account> unmarshaller : unmarshallers) {
            Account account = unmarshaller.unmarshall(request, response);
            if (account != null) {
                request.setAttribute(DefaultRequestAccountResolver.REQUEST_ATTR_NAME, account);
                break;
            }
        }

        chain.doFilter(request, response);
    }
}
