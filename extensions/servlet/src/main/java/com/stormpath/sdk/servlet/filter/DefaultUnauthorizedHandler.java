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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 1.0.RC3
 */
public class DefaultUnauthorizedHandler implements UnauthorizedHandler {

    private final String unauthorizedUrl;

    public DefaultUnauthorizedHandler(String unauthorizedUrl) {
        Assert.hasText(unauthorizedUrl, "unauthorizedUrl cannot be null or empty.");
        this.unauthorizedUrl = unauthorizedUrl;
    }

    protected String getUnauthorizedUrl() {
        return this.unauthorizedUrl;
    }

    @Override
    public boolean onUnauthorized(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {

        if (isHtmlPreferred(request)) {
            String url = getUnauthorizedUrl();
            try {
                ServletUtils.issueRedirect(request, response, url, null, true, true);
            } catch (IOException e) {
                String msg = "Unable to issue unauthorized redirect: " + e.getMessage();
                throw new ServletException(msg, e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
        }

        return false;
    }

    protected boolean isHtmlPreferred(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        return userAgent.isHtmlPreferred();
    }

    protected UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgents.get(request);
    }
}
