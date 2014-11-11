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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultUnauthenticatedHandler implements UnauthenticatedHandler {

    @Override
    public boolean onAuthenticationRequired(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (isHtmlPreferred(request)) {
            LoginPageRedirector.INSTANCE.redirectToLoginPage(request, response, "authcReqd");
        } else {
            //return 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            String realmName = getRealmName(request);
            String realmSuffix = "realm=\"" + realmName + "\"";

            // https://tools.ietf.org/html/rfc7235#section-2.1 recommends that Basic be listed before
            // other less common schemes:
            //
            //    Note: Many clients fail to parse a challenge that contains an
            //    unknown scheme.  A workaround for this problem is to list well-
            //    supported schemes (such as "basic") first.
            //
            //
            response.setHeader("WWW-Authenticate", "Basic " + realmSuffix);
            response.setHeader("WWW-Authenticate", "Bearer " + realmSuffix);

            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
        }

        return false;
    }

    protected boolean isHtmlPreferred(HttpServletRequest request) {
        UserAgent ua = new DefaultUserAgent(request);
        return ua.isHtmlPreferred();
    }

    protected String getRealmName(HttpServletRequest request) {
        Application application = ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
        return application.getName();
    }
}
