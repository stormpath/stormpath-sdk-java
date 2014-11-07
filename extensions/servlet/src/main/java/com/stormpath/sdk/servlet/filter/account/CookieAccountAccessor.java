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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.Accessor;
import com.stormpath.sdk.servlet.http.CookieAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieAccountAccessor extends AccountCookieHandler implements Accessor<Account> {

    private static final Logger log = LoggerFactory.getLogger(CookieAccountAccessor.class);

    @Override
    public Account get(HttpServletRequest request, HttpServletResponse response) {

        String cookieName = getAccountCookieConfig(request).getName();
        Accessor<Cookie> accessor = new CookieAccessor(cookieName);
        Cookie cookie = accessor.get(request, response);

        if (cookie == null) {
            return null;
        }

        String val = cookie.getValue();
        if (!Strings.hasText(val)) {
            return null;
        }

        try {

            Client client = getClient(request);
            JwtToAccountConverter converter = new JwtToAccountConverter(client);

            return converter.convert(val);

        } catch (Exception e) {
            String msg = "Encountered invalid JWT in account cookie.  Ignoring and deleting the cookie for safety.";
            log.debug(msg, e);
            if (!response.isCommitted()) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return null;
    }
}
