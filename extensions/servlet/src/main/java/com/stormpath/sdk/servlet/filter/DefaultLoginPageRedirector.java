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
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @since 1.0.RC3
 */
public class DefaultLoginPageRedirector implements LoginPageRedirector {

    private String loginUri;

    public DefaultLoginPageRedirector() {}

    public DefaultLoginPageRedirector(String loginUri) {
        Assert.hasText(loginUri, "loginUri cannot be null or empty.");
        this.loginUri = loginUri;
    }

    @Override
    public void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        if (loginUri == null) {
            Config config = ConfigResolver.INSTANCE.getConfig(request.getServletContext());

            //not authenticated, so we'll redirect the user the login url and the 'next' parameter will be equal
            //to the currently requested URL *if* the request is a GET request.  POST requests are rarely safe to
            //automatically execute automatically (not idempotent, etc), so we just return to the default login 'nextUrl'
            //if not a GET

            loginUri = config.getLoginConfig().getUri();
        }

        String method = request.getMethod();
        String nextUri = loginUri;

        if (method.equalsIgnoreCase("GET")) {

            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/1061
            String requestURI = request.getServletPath() + (Strings.hasText(request.getQueryString()) ? "?" + request.getQueryString() : "");

            String encodedCurrentUrlString = URLEncoder.encode(requestURI, "UTF-8");

            nextUri += "?next=" + encodedCurrentUrlString;
        }

        ServletUtils.issueRedirect(request, response, nextUri, null, true, true);
    }
}
