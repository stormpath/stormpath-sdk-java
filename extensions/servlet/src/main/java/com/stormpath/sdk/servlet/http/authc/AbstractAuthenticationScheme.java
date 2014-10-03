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
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractAuthenticationScheme implements HttpAuthenticationScheme {

    private static final ApplicationResolver APPLICATION_RESOLVER = new DefaultApplicationResolver();

    public AbstractAuthenticationScheme() {
    }

    protected HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt,
                                                    String usernameOrEmail, String password) {

        String remoteHost = attempt.getRequest().getRemoteHost();

        UsernamePasswordRequest request = new UsernamePasswordRequest(usernameOrEmail, password, remoteHost);

        Application app = getApplication(attempt.getRequest());

        AuthenticationResult result = app.authenticateAccount(request);

        return new DefaultHttpAuthenticationResult(attempt.getRequest(), attempt.getResponse(), result, true);
    }

    protected final Application getApplication(HttpServletRequest request) {
        return APPLICATION_RESOLVER.getApplication(request.getServletContext());
    }
}
