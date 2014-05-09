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
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.authc.BasicApiAuthenticationRequest;
import com.stormpath.sdk.oauth.permission.ScopeFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBasicOauthAuthenticationRequest extends BasicApiAuthenticationRequest {

    private final ScopeFactory scopeFactory;

    public DefaultBasicOauthAuthenticationRequest(HttpServletRequest httpServletRequest, ScopeFactory scopeFactory) {
        super(httpServletRequest);
        this.scopeFactory = scopeFactory;
    }

    public DefaultBasicOauthAuthenticationRequest(HttpRequest httpRequest, ScopeFactory scopeFactory) {
        super(httpRequest);
        this.scopeFactory = scopeFactory;
    }

    public ScopeFactory getScopeFactory() {
        return scopeFactory;
    }
}
