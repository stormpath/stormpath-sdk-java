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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.lang.*;
import com.stormpath.sdk.servlet.config.*;
import com.stormpath.sdk.servlet.http.*;
import com.stormpath.sdk.servlet.http.impl.*;
import org.slf4j.*;

import javax.servlet.http.*;

/**
 * @since 1.0.RC3
 */
public class CookieAccountResolver extends AccountCookieHandler implements Resolver<Account> {

    private static final Logger log = LoggerFactory.getLogger(CookieAccountResolver.class);

    private JwtAccountResolver jwtAccountResolver;

    public CookieAccountResolver(CookieConfig accountCookieConfig, JwtAccountResolver jwtAccountResolver) {
        super(accountCookieConfig);
        Assert.notNull(jwtAccountResolver, "JwtAccountResolver cannot be null.");
        this.jwtAccountResolver = jwtAccountResolver;
    }

    protected JwtAccountResolver getJwtAccountResolver() {
        return jwtAccountResolver;
    }

    @Override
    public Account get(HttpServletRequest request, HttpServletResponse response) {

        Resolver<Cookie> resolver = getCookieResolver(request);
        Cookie cookie = resolver.get(request, response);

        if (cookie == null) {
            return null;
        }

        String val = cookie.getValue();
        if (!Strings.hasText(val)) {
            return null;
        }

        try {
            return getAccount(request, response, val);
        } catch (Exception e) {
            String msg = "Encountered invalid JWT in account cookie.  Ignoring and deleting the cookie for safety.";
            log.debug(msg, e);
            deleteCookie(request, response, cookie);
        }

        return null;
    }

    protected Resolver<Cookie> getCookieResolver(HttpServletRequest request) {
        String cookieName = getAccountCookieConfig(request).getName();
        return new CookieResolver(cookieName);
    }

    protected Account getAccount(HttpServletRequest request, HttpServletResponse response, String jwt) {

        Account account = getJwtAccountResolver().getAccountByJwt(request, response, jwt);

        if (account != null) {
            request.setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME,
                                 HttpServletRequest.FORM_AUTH);
        }

        return account;
    }

    protected void deleteCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {
        if (!response.isCommitted()) {
            cookie.setValue("");
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/207
            //cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
