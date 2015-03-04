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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutController extends AbstractController {

    private String nextUri;

    public void init() {
        Assert.hasText(nextUri, "nextUri property cannot be null or empty.");
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //clear out any authentication/account state:
        request.logout();

        //it is a security risk to not terminate a session (if one exists) on logout:
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        String next = request.getParameter("next");

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        if (isHtmlPreferred(request)) {
            return new DefaultViewModel(next).setRedirect(true);
        } else {
            //probably an ajax or non-browser client - return 200 ok:
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }
    }

    protected boolean isHtmlPreferred(HttpServletRequest request) {
        UserAgent ua = getUserAgent(request);
        return ua.isHtmlPreferred();
    }

    protected UserAgent getUserAgent(HttpServletRequest request) {
        return new DefaultUserAgent(request);
    }
}
