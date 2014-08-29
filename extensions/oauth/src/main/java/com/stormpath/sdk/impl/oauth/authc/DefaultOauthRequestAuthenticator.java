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
import com.stormpath.sdk.impl.oauth.http.OauthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import com.stormpath.sdk.oauth.OauthRequestAuthenticator;
import com.stormpath.sdk.oauth.RequestLocation;
import com.stormpath.sdk.oauth.ResourceRequestAuthenticator;
import com.stormpath.sdk.oauth.ScopeFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
@SuppressWarnings("UnusedDeclaration") //used via reflection from API module
public class DefaultOauthRequestAuthenticator implements OauthRequestAuthenticator {

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG = "HttpRequest class [%s] is not supported. Supported classes: [%s, %s].";

    private final HttpServletRequest httpServletRequest;

    private final Application application;

    public DefaultOauthRequestAuthenticator(Application application, Object httpRequest) {
        Assert.notNull(application, "application cannot  be null.");
        Assert.notNull(httpRequest, "httpRequest cannot be null.");

        Class httpRequestClass = httpRequest.getClass();

        if (HttpServletRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = (HttpServletRequest) httpRequest;
        } else if (HttpRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = new OauthHttpServletRequest((HttpRequest) httpRequest);
        } else {
            throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequest.getClass(), HttpRequest.class.getName(), HttpServletRequest.class.getName()));
        }
        this.application = application;
    }

    @Override
    public AccessTokenRequestAuthenticator using(ScopeFactory scopeFactory) {
        return new DefaultAccessTokenRequestAuthenticator(application, httpServletRequest, scopeFactory);
    }

    @Override
    public AccessTokenRequestAuthenticator withTtl(long ttl) {
        return new DefaultAccessTokenRequestAuthenticator(application, httpServletRequest, null).withTtl(ttl);
    }

    @Override
    public ResourceRequestAuthenticator inLocation(RequestLocation... locations) {
        return new DefaultResourceRequestAuthenticator(httpServletRequest, application).inLocation(locations);
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
