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
package com.stormpath.sdk.servlet.http.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.servlet.application.ApplicationResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticationScheme implements HttpAuthenticationScheme {

    public AbstractAuthenticationScheme() {
    }

    protected HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt, String usernameOrEmail,
                                                    String password) {

        HttpServletRequest request;
        HttpServletResponse response;
        AuthenticationResult result;
        try {
            request = attempt.getRequest();
            response = attempt.getResponse();

            String remoteHost = attempt.getRequest().getRemoteHost();

            UsernamePasswordRequest upRequest = new UsernamePasswordRequest(usernameOrEmail, password, remoteHost);

            Application app = getApplication(attempt.getRequest());

            result = app.authenticateAccount(upRequest);
        } catch (Exception e) {
            String msg = "Unable to authenticate usernameOrEmail and password-based request for usernameOrEmail [" +
                         usernameOrEmail + "]: " + e.getMessage();
            throw new HttpAuthenticationException(msg, e);
        }

        return new DefaultHttpAuthenticationResult(request, response, result);
    }

    protected HttpAuthenticationResult authenticateApiKey(HttpAuthenticationAttempt attempt) {

        HttpServletRequest request;
        HttpServletResponse response;
        AuthenticationResult result;
        try {
            request = attempt.getRequest();
            response = attempt.getResponse();
            Application app = getApplication(request);
            result = app.authenticateApiRequest(request);
        } catch (Exception e) {
            String msg = "Unable to authenticate request: " + e.getMessage();
            throw new HttpAuthenticationException(msg, e);
        }

        return new DefaultHttpAuthenticationResult(request, response, result);
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
    }
}
