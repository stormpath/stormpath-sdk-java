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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHttpAuthenticationResult implements HttpAuthenticationResult {

    private final HttpServletRequest   request;
    private final HttpServletResponse  response;
    private final AuthenticationResult result;

    public DefaultHttpAuthenticationResult(HttpServletRequest request, HttpServletResponse response,
                                           AuthenticationResult result) {
        Assert.notNull(request, "request cannot be null.");
        Assert.notNull(response, "response cannot be null.");
        Assert.notNull(result, "AuthenticationResult cannot be null.");
        this.request = request;
        this.response = response;
        this.result = result;
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public AuthenticationResult getAuthenticationResult() {
        return result;
    }

}
