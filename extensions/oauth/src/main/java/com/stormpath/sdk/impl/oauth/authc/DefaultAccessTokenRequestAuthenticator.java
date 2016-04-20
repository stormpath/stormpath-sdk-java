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
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.oauth.http.OauthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.ScopeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultAccessTokenRequestAuthenticator implements AccessTokenRequestAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccessTokenRequestAuthenticator.class);

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG = "HttpRequest class [%s] is not supported. Supported classes: [%s, %s].";

    private HttpServletRequest httpServletRequest;

    private final Application application;

    private ScopeFactory scopeFactory;

    private long ttl = AccessTokenAuthenticationRequest.DEFAULT_TTL;

    DefaultAccessTokenRequestAuthenticator(Application application) {
        Assert.notNull(application, "application cannot be null or empty.");
        this.application = application;
    }

    @Override
    public AccessTokenRequestAuthenticator using(ScopeFactory scopeFactory) {
        this.scopeFactory = scopeFactory;
        return this;
    }

    @Override
    public AccessTokenRequestAuthenticator withTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    public AccessTokenRequestAuthenticator setHttpServletRequest (HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        return this;
    }

    @Override
    @Deprecated //This method will be removed for 1.0. Use the authenticate method instead
    public AccessTokenResult execute() {

        AuthenticationRequest request;
        try {
            request = new AccessTokenAuthenticationRequest(httpServletRequest, scopeFactory, ttl);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory
                .newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }

        AuthenticationResult authenticationResult = application.authenticateAccount(request);

        Assert.isInstanceOf(AccessTokenResult.class, authenticationResult);

        return (AccessTokenResult) authenticationResult;
    }

    @Override
    public AccessTokenResult authenticate(HttpRequest httpRequest) {

        Assert.notNull(httpRequest, "httpRequest cannot be null or empty.");

        Class httpRequestClass = httpRequest.getClass();

        if (HttpServletRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = (HttpServletRequest) httpRequest;
        } else if (HttpRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = new OauthHttpServletRequest((HttpRequest) httpRequest);
        } else {
            throw new IllegalArgumentException(String.format(
                HTTP_REQUEST_NOT_SUPPORTED_MSG,
                httpRequest.getClass(), HttpRequest.class.getName(), HttpServletRequest.class.getName()
            ));
        }

        AuthenticationRequest request;
        try {
            request = new AccessTokenAuthenticationRequest(httpServletRequest, scopeFactory, ttl);
        } catch (Exception e) {
            log.warn("Caught Exception: {}. Rethrowing as OauthAuthenticationException", e.getMessage(), e);

            throw ApiAuthenticationExceptionFactory
                .newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }

        AuthenticationResult authenticationResult = application.authenticateAccount(request);

        Assert.isInstanceOf(AccessTokenResult.class, authenticationResult);

        return (AccessTokenResult) authenticationResult;
    }
}
