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
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.CookieConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class CookieAccountUnmarshaller extends AccountCookieAccessor implements Unmarshaller<Account> {

    private static final Logger log = LoggerFactory.getLogger(CookieAccountUnmarshaller.class);

    @Override
    public Account unmarshall(HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = getCookie(request);
        if (cookie == null) {
            return null;
        }

        String val = cookie.getValue();
        if (!Strings.hasText(val)) {
            return null;
        }

        String accountHref = null;

        try {
            String signingKey = getSigningKey(request);

            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(val).getBody();

            accountHref = claims.getSubject();

            Date exp = claims.getExpiration();
            if (exp != null) {
                Date now = new Date();
                if (now.after(exp)) {
                    //throw an exception so the cookie removal code below will execute:
                    throw new IllegalStateException("Account cookie JWT has expired and cannot be used.");
                }
            }
        } catch (Exception e) {
            String msg = "Encountered invalid JWT in account cookie.  Ignoring and deleting the cookie for safety.";
            log.debug(msg, e);
            if (!response.isCommitted()) {
                cookie.setValue("");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        if (accountHref != null) {
            Client client = getClient(request);
            return client.getResource(accountHref, Account.class);
        }

        return null;
    }

    protected Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        CookieConfig cfg = getAccountCookieConfig(request);

        for (Cookie cookie : cookies) {
            if (cfg.getName().equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }
}
