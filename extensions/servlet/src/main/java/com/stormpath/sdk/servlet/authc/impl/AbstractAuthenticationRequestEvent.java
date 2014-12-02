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
package com.stormpath.sdk.servlet.authc.impl;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.servlet.authc.AuthenticationRequestEvent;
import com.stormpath.sdk.servlet.event.impl.AbstractRequestEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticationRequestEvent extends AbstractRequestEvent
    implements AuthenticationRequestEvent {

    private final AuthenticationRequest authcRequest;

    public AbstractAuthenticationRequestEvent(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationRequest authcRequest) {
        super(request, response);
        this.authcRequest = authcRequest;
    }

    @Override
    public AuthenticationRequest getAuthenticationRequest() {
        return this.authcRequest;
    }
}
