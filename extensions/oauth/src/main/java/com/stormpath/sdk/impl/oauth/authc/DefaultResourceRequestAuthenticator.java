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
import com.stormpath.sdk.error.authc.OAuthAuthenticationException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.oauth.http.OAuthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthAuthenticationResult;
import com.stormpath.sdk.oauth.RequestLocation;
import com.stormpath.sdk.oauth.ResourceRequestAuthenticator;

import javax.servlet.http.HttpServletRequest;

/** @since 1.0.RC */
public class DefaultResourceRequestAuthenticator implements ResourceRequestAuthenticator {

    private final Application application;

    private RequestLocation[] locations;

    private HttpServletRequest httpServletRequest;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG = "HttpRequest class [%s] is not supported. Supported classes: [%s, %s].";

    DefaultResourceRequestAuthenticator(HttpServletRequest httpServletRequest, Application application) {
        Assert.notNull(httpServletRequest);
        Assert.notNull(application, "application cannot be null.");
        this.httpServletRequest = httpServletRequest;
        this.application = application;
    }

    DefaultResourceRequestAuthenticator(Application application) {
        Assert.notNull(application, "application cannot be null or empty.");
        this.application = application;
    }

    @Override
    public ResourceRequestAuthenticator inLocation(RequestLocation... locations) {
        this.locations = locations;
        return this;
    }

    @Override
    public OAuthAuthenticationResult execute() {

        RequestLocation[] locations = this.locations != null ? this.locations :
                                      new RequestLocation[]{RequestLocation.HEADER, RequestLocation.BODY};

        AuthenticationRequest request;

        try {
            request = new ResourceAuthenticationRequest(httpServletRequest, locations);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOAuthException(OAuthAuthenticationException.class,
                    OAuthAuthenticationException.INVALID_REQUEST);
        }

        AuthenticationResult result = application.authenticateAccount(request);

        Assert.isInstanceOf(OAuthAuthenticationResult.class, result);

        return (OAuthAuthenticationResult) result;
    }

    @Override
    public OAuthAuthenticationResult authenticate(HttpRequest httpRequest) {
        RequestLocation[] locations = this.locations != null ? this.locations :
                new RequestLocation[]{RequestLocation.HEADER, RequestLocation.BODY};

        Class httpRequestClass = httpRequest.getClass();

        if (HttpServletRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = (HttpServletRequest) httpRequest;
        } else if (HttpRequest.class.isAssignableFrom(httpRequestClass)) {
            this.httpServletRequest = new OAuthHttpServletRequest(httpRequest);
        } else {
            throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequest.getClass(), HttpRequest.class.getName(), HttpServletRequest.class.getName()));
        }
        AuthenticationRequest request;

        try {
            request = new ResourceAuthenticationRequest(httpServletRequest, locations);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOAuthException(OAuthAuthenticationException.class,
                    OAuthAuthenticationException.INVALID_REQUEST);
        }

        AuthenticationResult result = application.authenticateAccount(request);
        Assert.isInstanceOf(OAuthAuthenticationResult.class, result);
        return (OAuthAuthenticationResult) result;
    }
}
