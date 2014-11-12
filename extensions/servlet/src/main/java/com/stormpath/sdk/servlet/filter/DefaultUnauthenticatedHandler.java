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

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultUnauthenticatedHandler implements UnauthenticatedHandler, ServletContextInitializable {

    protected static final String HTTP_AUTHENTICATOR = "stormpath.servlet.http.authc";
    private HttpAuthenticator httpAuthenticator;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        this.httpAuthenticator = config.getInstance(HTTP_AUTHENTICATOR);
    }

    @Override
    public boolean onAuthenticationRequired(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (isHtmlPreferred(request)) {
            LoginPageRedirector.INSTANCE.redirectToLoginPage(request, response, "authcReqd");
        } else {
            this.httpAuthenticator.sendChallenge(request, response);
        }

        return false;
    }

    protected boolean isHtmlPreferred(HttpServletRequest request) {
        UserAgent ua = new DefaultUserAgent(request);
        return ua.isHtmlPreferred();
    }
}
