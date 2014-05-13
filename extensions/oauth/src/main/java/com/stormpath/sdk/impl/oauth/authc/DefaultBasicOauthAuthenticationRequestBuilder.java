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
import com.stormpath.sdk.error.authc.InvalidAuthenticationRequestOauthException;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import com.stormpath.sdk.oauth.permission.ScopeFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBasicOauthAuthenticationRequestBuilder implements BasicOauthAuthenticationRequestBuilder {

    private final HttpServletRequest httpServletRequest;

    private final Application application;

    private ScopeFactory scopeFactory;

    private long ttl = DefaultBasicOauthAuthenticationRequest.DEFAULT_TTL;

    DefaultBasicOauthAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest, ScopeFactory scopeFactory) {
        Assert.notNull(application, "application cannot be null or empty.");

        this.scopeFactory = scopeFactory;
        this.application = application;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory) {
        this.scopeFactory = scopeFactory;
        return this;
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder withTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public BasicOauthAuthenticationResult execute() {

        AuthenticationRequest request;
        try {
            request = new DefaultBasicOauthAuthenticationRequest(httpServletRequest, scopeFactory, ttl);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(InvalidAuthenticationRequestOauthException.class, e.getMessage());
        }

        AuthenticationResult authenticationResult = application.authenticateAccount(request);

        Assert.isInstanceOf(BasicOauthAuthenticationResult.class, authenticationResult);

        return (BasicOauthAuthenticationResult) authenticationResult;
    }
}
