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

import java.lang.reflect.Constructor;

/**
 * @since 1.0.RC
 */
public class DefaultApiRequestAuthenticator implements ApiRequestAuthenticator {

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final String HTTP_SERVLET_REQUEST_WRAPPER_FQCN = "com.stormpath.sdk.impl.authc.DefaultHttpServletRequestWrapper";

    private static final Class<? extends HttpServletRequestWrapper> HTTP_SERVLET_REQUEST_WRAPPER_CLASS;

    static {
        if (Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN)) {
            HTTP_SERVLET_REQUEST_WRAPPER_CLASS = Classes.forName(HTTP_SERVLET_REQUEST_WRAPPER_FQCN);
        } else {
            HTTP_SERVLET_REQUEST_WRAPPER_CLASS = null;
        }
    }

    private final HttpServletRequestWrapper httpServletRequestWrapper;

    private final HttpRequest httpRequest;

    private final Application application;

    public DefaultApiRequestAuthenticator(Application application, Object httpRequest) {
        Assert.notNull(httpRequest);
        Assert.notNull(application, "application cannot be null.");

        this.application = application;
        if (HttpRequest.class.isAssignableFrom(httpRequest.getClass())) {
            this.httpRequest = (HttpRequest) httpRequest;
            this.httpServletRequestWrapper = null;
        } else {
            //This must never happen, if the object request is of HttpServlet request type the HTTP_SERVLET_REQUEST_WRAPPER_CLASS
            //must be already loaded and therefor cannot be null.
            if (HTTP_SERVLET_REQUEST_WRAPPER_CLASS == null) {
                throw new RuntimeException("");
            }
            this.httpRequest = null;
            Constructor<? extends HttpServletRequestWrapper> ctor = Classes.getConstructor(HTTP_SERVLET_REQUEST_WRAPPER_CLASS, Object.class);
            httpServletRequestWrapper = Classes.instantiate(ctor, httpRequest);
        }
    }

    @Override
    public ApiAuthenticationResult execute() {
        AuthenticationRequest request;

        if (httpServletRequestWrapper != null) {
            request = new ApiAuthenticationRequestFactory().createFrom(httpServletRequestWrapper);
        } else {
            request = new ApiAuthenticationRequestFactory().createFrom(httpRequest);
        }

        AuthenticationResult result = application.authenticateAccount(request);
        Assert.isInstanceOf(ApiAuthenticationResult.class, result);
        return (ApiAuthenticationResult) result;
    }
}
