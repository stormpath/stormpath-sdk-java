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

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.ConfigValueReader;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutFilter extends PathMatchingFilter {

    public static final String LOGOUT_URL_PROP_NAME = "stormpath.web.logout.url";
    public static final String DEFAULT_LOGOUT_URL = "/logout";
    public static final String LOGOUT_NEXT_URL_PROP_NAME = "stormpath.web.logout.nextUrl";
    public static final String DEFAULT_LOGOUT_NEXT_URL = "/";

    @Override
    protected void onInit() throws ServletException {
        String logoutUrlPattern = getLogoutUrl();
        int i = logoutUrlPattern.indexOf('?');
        if (i != -1) {
            logoutUrlPattern = logoutUrlPattern.substring(0, i);
        }
        i = logoutUrlPattern.indexOf(';');
        if (i != -1) {
            logoutUrlPattern = logoutUrlPattern.substring(0, i);
        }
        this.pathPatterns.add(logoutUrlPattern);
    }

    protected String getLogoutUrl() {
        return ConfigValueReader.DEFAULT.readValue(getServletContext(), LOGOUT_URL_PROP_NAME, DEFAULT_LOGOUT_URL);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        request.logout();

        //it is a security risk to not terminate a session on logout:
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        String next = request.getParameter("next");

        if (!Strings.hasText(next)) {
            next = ConfigValueReader.DEFAULT.readValue(getServletContext(), LOGOUT_NEXT_URL_PROP_NAME, DEFAULT_LOGOUT_NEXT_URL);
        }

        ServletUtils.issueRedirect(request, response, next, null, true, true);

        //don't continue the chain - we want to short circuit and redirect
    }
}
