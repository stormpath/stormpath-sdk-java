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
import com.stormpath.sdk.http.HttpRequest;
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

    private final HttpServletRequest httpServletRequest;

    private final HttpRequest httpRequest;

    private final Application application;

    public DefaultOauthAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest) {
        this(application, httpServletRequest, null);
        Assert.notNull(httpServletRequest);
    }

    public DefaultOauthAuthenticationRequestBuilder(Application application, HttpRequest httpRequest) {
        this(application, null, httpRequest);
        Assert.notNull(httpRequest);
    }

    private DefaultOauthAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest, HttpRequest httpRequest) {
        Assert.notNull(application, "application cannot  be null.");
        this.application = application;
        this.httpServletRequest = httpServletRequest;
        this.httpRequest = httpRequest;
    }

    @Override
    public BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory) {
        DefaultBasicOauthAuthenticationRequestBuilder builder = httpServletRequest != null
                ? new DefaultBasicOauthAuthenticationRequestBuilder(application, httpServletRequest, scopeFactory)
                : new DefaultBasicOauthAuthenticationRequestBuilder(application, httpRequest, scopeFactory);

        return builder;
    }

    @Override
    public BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations) {
        Assert.noNullElements(locations);
        DefaultBearerOauthAuthenticationRequestBuilder builder = httpServletRequest != null
                ? new DefaultBearerOauthAuthenticationRequestBuilder(httpServletRequest, application, locations)
                : new DefaultBearerOauthAuthenticationRequestBuilder(httpRequest, application, locations);
        return builder;
    }

    @Override
    public OauthAuthenticationResult execute() {
        throw new UnsupportedOperationException("execute() method hasn't been implemented.");
    }
}
