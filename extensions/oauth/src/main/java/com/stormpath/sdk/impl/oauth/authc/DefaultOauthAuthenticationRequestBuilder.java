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
import com.stormpath.sdk.impl.oauth.http.OAuthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.BearerLocation;
import com.stormpath.sdk.oauth.authc.BearerOauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;
import com.stormpath.sdk.oauth.permission.ScopeFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultOauthAuthenticationRequestBuilder implements OauthAuthenticationRequestBuilder {

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG = "HttpRequest class [%s] is not supported. Supported classes: [%s, %s].";

    private final HttpServletRequest httpServletRequest;

    private final Application application;

    public DefaultOauthAuthenticationRequestBuilder(Application application, Object httpRequest) {
        Assert.notNull(application, "application cannot  be null.");
        Assert.notNull(httpRequest, "httpRequest cannot be null.");

        Class httpRequestClass = httpRequest.getClass();

        if (HttpServletRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = (HttpServletRequest) httpRequest;
        } else if (HttpRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = new OAuthHttpServletRequest((HttpRequest) httpRequest);
        } else {
            throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequest.getClass(), HttpRequest.class.getName(), HttpServletRequest.class.getName()));
        }
        this.application = application;
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory) {
        return new DefaultBasicOauthAuthenticationRequestBuilder(application, httpServletRequest, scopeFactory);
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder withTtl(long ttl) {
        return new DefaultBasicOauthAuthenticationRequestBuilder(application, httpServletRequest, null).withTtl(ttl);
    }

    @Override
    public BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations) {
        return new DefaultBearerOauthAuthenticationRequestBuilder(httpServletRequest, application).inLocation(locations);
    }

    @Override
    public OauthAuthenticationResult execute() {

        OauthAuthenticationRequestFactory factory = new OauthAuthenticationRequestFactory();

        AuthenticationRequest request = factory.createFrom(httpServletRequest);

        AuthenticationResult result = application.authenticateAccount(request);

        Assert.isInstanceOf(OauthAuthenticationResult.class, result);

        return (OauthAuthenticationResult) result;
    }
}
