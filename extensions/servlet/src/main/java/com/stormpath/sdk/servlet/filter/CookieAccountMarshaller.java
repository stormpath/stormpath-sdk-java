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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.CookieConfig;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieAccountMarshaller extends AccountCookieAccessor implements Marshaller<AuthenticationResult> {

    @Override
    public void marshall(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {

        String jwt;

        if (result instanceof AccessTokenResult) {
            jwt = ((AccessTokenResult)result).getTokenResponse().getAccessToken();
        } else {
            jwt = createJwt(request, result.getAccount());
        }

        CookieConfig cfg = getAccountCookieConfig(request);
        SessionCookieConfig sessionCookieConfig = request.getServletContext().getSessionCookieConfig();

        Cookie cookie = new Cookie(cfg.getName(), jwt);

        String val = cfg.getComment();
        if (Strings.hasText(val)) {
            cookie.setComment(val);
        }

        val = cfg.getDomain();
        if (!Strings.hasText(val)) { //fall back to session config if any:
            val = sessionCookieConfig.getDomain();
        }
        if (Strings.hasText(val)) {
            cookie.setDomain(val);
        }

        val = cfg.getPath();
        if (!Strings.hasText(val)) { //fall back to session config if any:
            val = sessionCookieConfig.getPath();
        }
        if (Strings.hasText(val)) {
            cookie.setPath(val);
        }

        cookie.setSecure(cfg.isSecure());
        cookie.setHttpOnly(cfg.isHttpOnly());

        int maxAge = cfg.getMaxAge();
        if (maxAge == Integer.MIN_VALUE) { //fall back to session config if any:
            maxAge = sessionCookieConfig.getMaxAge();
        }
        if (maxAge >= -1) {
            cookie.setMaxAge(maxAge);
        }

        response.addCookie(cookie);
    }
}
