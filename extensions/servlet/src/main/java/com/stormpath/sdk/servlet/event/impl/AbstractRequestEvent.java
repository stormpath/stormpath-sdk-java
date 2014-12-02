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
package com.stormpath.sdk.servlet.event.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.event.RequestEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractRequestEvent implements RequestEvent {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public AbstractRequestEvent(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "Request cannot be null.");
        Assert.notNull(response, "Response cannot be null.");
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }
}
