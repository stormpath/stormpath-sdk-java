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
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.CookieResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieAccountResolver extends AccountCookieHandler
    implements Resolver<Account>, ServletContextInitializable {

    private static final Logger log = LoggerFactory.getLogger(CookieAccountResolver.class);

    protected static final String JWT_ACCOUNT_RESOLVER = "stormpath.web.account.jwt.resolver";

    private JwtAccountResolver jwtAccountResolver;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        this.jwtAccountResolver = config.getInstance(JWT_ACCOUNT_RESOLVER);
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
            request.setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, HttpServletRequest.FORM_AUTH);
        }

        return account;
    }

    protected void deleteCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {
        if (!response.isCommitted()) {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
