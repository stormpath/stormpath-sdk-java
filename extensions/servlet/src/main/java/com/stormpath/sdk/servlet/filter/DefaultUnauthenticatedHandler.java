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
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class DefaultUnauthenticatedHandler implements UnauthenticatedHandler {

    private HeaderAuthenticator httpAuthenticator;

    public DefaultUnauthenticatedHandler(HeaderAuthenticator httpAuthenticator) {
        Assert.notNull(httpAuthenticator, "HttpAuthenticator cannot be null.");
        this.httpAuthenticator = httpAuthenticator;
    }

    @Override
    public boolean onAuthenticationRequired(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        if (isHtmlPreferred(request)) {
            LoginPageRedirector.INSTANCE.redirectToLoginPage(request, response, "authcReqd");
        } else {
            this.httpAuthenticator.sendChallenge(request, response);
        }

        return false;
    }

    protected boolean isHtmlPreferred(HttpServletRequest request) {
        UserAgent ua = getUserAgent(request);
        return ua.isHtmlPreferred();
    }

    protected UserAgent getUserAgent(HttpServletRequest request) {
        return new DefaultUserAgent(request);
    }
}
