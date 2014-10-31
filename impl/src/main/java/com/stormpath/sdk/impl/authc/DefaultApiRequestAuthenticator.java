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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiRequestAuthenticator;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;

/**
 * @since 1.0.RC
 */
public class DefaultApiRequestAuthenticator implements ApiRequestAuthenticator {

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final ApiAuthenticationRequestFactory FACTORY = new ApiAuthenticationRequestFactory();

    private final Application application;

    private final HttpRequest httpRequest;

    public DefaultApiRequestAuthenticator(Application application, Object httpRequest) {

        Assert.notNull(application, "application argument cannot be null.");
        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");
        this.application = application;

        if (HttpRequest.class.isAssignableFrom(httpRequest.getClass())) {
            this.httpRequest = (HttpRequest) httpRequest;
        } else {
            Assert.isTrue(Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN),
                          "The " + HTTP_SERVLET_REQUEST_FQCN + " class must be in the runtime classpath.");

            Assert.isInstanceOf(javax.servlet.http.HttpServletRequest.class, httpRequest,
                                "The specified httpRequest argument must be an instance of " +
            HttpRequest.class.getName() + " or " + HTTP_SERVLET_REQUEST_FQCN);

            javax.servlet.http.HttpServletRequest httpServletRequest =
                new javax.servlet.http.HttpServletRequestWrapper((javax.servlet.http.HttpServletRequest)httpRequest);

            this.httpRequest = new com.stormpath.sdk.impl.http.ServletHttpRequest(httpServletRequest);
        }
    }

    @Override
    public ApiAuthenticationResult execute() {
        AuthenticationRequest request = FACTORY.createFrom(httpRequest);
        AuthenticationResult result = application.authenticateAccount(request);
        Assert.isInstanceOf(ApiAuthenticationResult.class, result);
        return (ApiAuthenticationResult) result;
    }
}
