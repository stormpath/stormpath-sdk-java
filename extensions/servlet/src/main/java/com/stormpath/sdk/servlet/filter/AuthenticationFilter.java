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
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

public class AuthenticationFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return request.getRemoteUser() != null; //non null if authenticated
        //TODO: what about remember me? remoteUser might be populated, but rememberMe != authenticated
    }

    @Override
    protected boolean onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //not authenticated, so we'll redirect the user the login url and the 'next' parameter will be equal
        //to the currently requested URL *if* the request is a GET request.  POST requests are rarely safe to
        //automatically execute automatically (not idempotent, etc), so we just return to the default login 'nextUrl'
        //if not a GET

        String redirectUrl = getConfig().getLoginUrl();
        String query = null;

        int i = redirectUrl.indexOf('?');
        if (i != -1) {
            if (i == redirectUrl.length() - 1) {
                query = Strings.EMPTY_STRING;
            } else {
                query = redirectUrl.substring(i+1);
            }
        }

        if (query == null) {
            redirectUrl += "?status=authcReqd";
        } else if (!query.contains("status")) {

            if (!query.equals(Strings.EMPTY_STRING)) {
                redirectUrl += "&";
            }

            redirectUrl += "status=authcReqd";
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

        return false;
    }
}
