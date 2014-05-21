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
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BearerLocation;
import com.stormpath.sdk.oauth.authc.BearerOauthAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBearerOauthAuthenticationRequestBuilder implements BearerOauthAuthenticationRequestBuilder {

    private final Application application;

    private BearerLocation[] locations;


    private final HttpServletRequest httpServletRequest;

    DefaultBearerOauthAuthenticationRequestBuilder(HttpServletRequest httpServletRequest, Application application) {
        Assert.notNull(httpServletRequest);
        Assert.notNull(application, "application cannot be null.");

        this.httpServletRequest = httpServletRequest;
        this.application = application;
    }

    @Override
    public BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations) {
        this.locations = locations;
        return this;
    }

    @Override
    public OauthAuthenticationResult execute() {

        locations = locations == null ? new BearerLocation[]{BearerLocation.HEADER} : locations;

        AuthenticationRequest request;
        try {
            request = new DefaultBearerOauthAuthenticationRequest(httpServletRequest, locations);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, e.getMessage());
        }

        AuthenticationResult result = application.authenticateAccount(request);

        Assert.isInstanceOf(OauthAuthenticationResult.class, result);

        return (OauthAuthenticationResult) result;
    }

}
