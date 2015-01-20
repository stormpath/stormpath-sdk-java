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

    @Override
    public void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response, String status)
        throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(request.getServletContext());

        Assert.notNull(status, "status argument cannot be null.");

        //not authenticated, so we'll redirect the user the login url and the 'next' parameter will be equal
        //to the currently requested URL *if* the request is a GET request.  POST requests are rarely safe to
        //automatically execute automatically (not idempotent, etc), so we just return to the default login 'nextUrl'
        //if not a GET

        String redirectUrl = config.getLoginUrl();
        String query = null;

        int i = redirectUrl.indexOf('?');
        if (i != -1) {
            if (i == redirectUrl.length() - 1) {
                query = Strings.EMPTY_STRING;
            } else {
                query = redirectUrl.substring(i + 1);
            }
        }

        if (query == null) {
            redirectUrl += "?status=" + status;
        } else if (!query.contains("status")) {

            if (!query.equals(Strings.EMPTY_STRING)) {
                redirectUrl += "&";
            }

            redirectUrl += "status=" + status;
        }

        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET")) {

            String currentUrlString = request.getRequestURL().toString();
            query = request.getQueryString();
            if (query != null) {
                currentUrlString += "?" + query;
            }

            String encodedCurrentUrlString = URLEncoder.encode(currentUrlString, "UTF-8");

            redirectUrl += "&next=" + encodedCurrentUrlString;
        }

        ServletUtils.issueRedirect(request, response, redirectUrl, null, true, true);
    }
}
