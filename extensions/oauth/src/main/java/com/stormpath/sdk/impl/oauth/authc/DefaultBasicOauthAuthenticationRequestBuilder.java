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


import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import com.stormpath.sdk.oauth.permission.ScopeFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBasicOauthAuthenticationRequestBuilder implements BasicOauthAuthenticationRequestBuilder {

    private final HttpRequest httpRequest;

    private final HttpServletRequest httpServletRequest;

    private ScopeFactory scopeFactory;

    private final Application application;

    DefaultBasicOauthAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest, ScopeFactory scopeFactory) {
        this(httpServletRequest, null, application, scopeFactory);
    }

    DefaultBasicOauthAuthenticationRequestBuilder(Application application, HttpRequest httpRequest, ScopeFactory scopeFactory) {
        this(null, httpRequest, application, scopeFactory);
    }

    private DefaultBasicOauthAuthenticationRequestBuilder(HttpServletRequest httpServletRequest, HttpRequest httpRequest, Application application, ScopeFactory scopeFactory) {
        Assert.notNull(application, "application cannot be null or empty.");

        this.httpRequest = httpRequest;
        this.httpServletRequest = httpServletRequest;
        this.application = application;
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory) {
        this.scopeFactory = scopeFactory;
        return this;
    }

    @Override
    public BasicOauthAuthenticationResult execute() {

        AuthenticationRequest request;

        if (httpServletRequest != null) {
            request = new DefaultBasicOauthAuthenticationRequest(httpServletRequest, scopeFactory);
        } else {
            request = new DefaultBasicOauthAuthenticationRequest(httpRequest, scopeFactory);
        }

        AuthenticationResult authenticationResult = application.authenticateAccount(request);

        Assert.isInstanceOf(BasicOauthAuthenticationResult.class, authenticationResult);

        return (BasicOauthAuthenticationResult) authenticationResult;
    }
}
