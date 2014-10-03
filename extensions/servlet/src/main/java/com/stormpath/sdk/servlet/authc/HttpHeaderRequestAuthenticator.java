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
package com.stormpath.sdk.servlet.authc;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public abstract class HttpHeaderRequestAuthenticator {

    private final String headerName;

    public HttpHeaderRequestAuthenticator() {
        this("Authorization");
    }

    protected HttpHeaderRequestAuthenticator(String headerName) {
        Assert.hasText(headerName, "headerName argument cannot be null or empty.");
        this.headerName = headerName;
    }

    public AuthenticationResult authenticate(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> values = request.getHeaders(headerName);
        if (values == null) {
            throw new AuthenticationException("HTTP header '" + headerName + "' is not present.  Unable to authenticate the request.");
        }
        if (!values.hasMoreElements()) {
            throw new AuthenticationException("HTTP header '" + headerName + "' did not contain any values.  Unable to authenticate the request.");
        }
        return authenticate(request, response, values);
    }

    protected AuthenticationResult authenticate(HttpServletRequest request, HttpServletResponse response, Enumeration<String> headerValues) {
        return authenticate(request, response, headerValues.nextElement());
    }

    protected abstract AuthenticationResult authenticate(HttpServletRequest request, HttpServletResponse response, String headerValue);
}
