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

import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHttpAuthenticationAttempt implements HttpAuthenticationAttempt {

    private final HttpServletRequest  request;
    private final HttpServletResponse response;
    private final HttpCredentials     credentials;

    public DefaultHttpAuthenticationAttempt(HttpServletRequest request, HttpServletResponse response,
                                            HttpCredentials creds) {
        Assert.notNull(request);
        Assert.notNull(response);
        Assert.notNull(creds);
        this.request = request;
        this.response = response;
        this.credentials = creds;
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @Override
    public HttpCredentials getCredentials() {
        return this.credentials;
    }
}
