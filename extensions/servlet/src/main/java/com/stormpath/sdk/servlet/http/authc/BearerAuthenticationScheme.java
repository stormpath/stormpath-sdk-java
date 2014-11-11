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

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BearerAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final String NAME = "Bearer";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt) {

        Assert.notNull(attempt, "attempt cannot be null.");

        HttpServletRequest request = attempt.getRequest();
        Assert.notNull(request, "attempt request property cannot be null.");

        HttpServletResponse response = attempt.getResponse();
        Assert.notNull(response, "attempt response property cannot be null.");

        HttpCredentials credentials = attempt.getCredentials();
        Assert.notNull(credentials, "credentials cannot be null.");
        Assert.isTrue(NAME.equalsIgnoreCase(credentials.getSchemeName()), "Unsupported scheme.");

        final String token = attempt.getCredentials().getSchemeValue();
        Assert.hasText(token, "Cannot authenticate empty Bearer value.");

        Application application = getApplication(request);

        ApiAuthenticationResult result = application.authenticateApiRequest(request);

        return new DefaultHttpAuthenticationResult(request, response, result);
    }
}
