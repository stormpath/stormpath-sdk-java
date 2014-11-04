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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.CookieConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieMutator implements Mutator<String> {

    private final CookieConfig config;

    public CookieMutator(CookieConfig config) {
        Assert.notNull(config, "config cannot be null.");
        this.config = config;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, String value) {

        boolean delete = !Strings.hasText(value); //null means 'delete the cookie', which is done by setting an empty value

        if (delete) {
            value = "";
        }

        CookieConfig cfg = this.config;

        Cookie cookie = new Cookie(cfg.getName(), value);

        String val = cfg.getComment();
        if (val != null) {
            cookie.setComment(val);
        }

        val = cfg.getDomain();
        if (val != null) {
            cookie.setDomain(val);
        }

        val = cfg.getPath();
        if (val == null || delete) {
            val = "/";
        }
        cookie.setPath(val);
        cookie.setSecure(cfg.isSecure());
        cookie.setHttpOnly(cfg.isHttpOnly());

        int maxAge = delete ? 0 : Math.max(-1, cfg.getMaxAge());
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }
}
