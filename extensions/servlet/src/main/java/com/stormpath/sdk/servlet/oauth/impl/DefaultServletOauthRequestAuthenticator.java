/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.servlet.oauth.impl;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.*;
import com.stormpath.sdk.servlet.oauth.ServletOauthRequestAuthenticator;
import com.stormpath.sdk.servlet.util.Servlets;

import javax.servlet.http.HttpServletRequest;

public class DefaultServletOauthRequestAuthenticator implements ServletOauthRequestAuthenticator {

    private final Application application;

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    public DefaultServletOauthRequestAuthenticator(Application application) {
        this.application = application;
    }

    @Override
    public OauthAuthenticationResult authenticate(HttpServletRequest httpRequest) {
        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");
        Assert.isInstanceOf(javax.servlet.http.HttpServletRequest.class, httpRequest,
                "The specified httpRequest argument must be an instance of " + HTTP_SERVLET_REQUEST_FQCN);

        com.stormpath.sdk.impl.http.ServletHttpRequest stmpHttpRequest = new com.stormpath.sdk.impl.http.ServletHttpRequest(httpRequest);
        return (OauthAuthenticationResult) Applications.oauthRequestAuthenticator(application).authenticate(stmpHttpRequest);
    }

    @Override
    public AccessTokenRequestAuthenticator using(ScopeFactory scopeFactory) {
        return Applications.oauthRequestAuthenticator(application).using(scopeFactory);
    }

    @Override
    public AccessTokenRequestAuthenticator withTtl(long ttl) {
        return Applications.oauthRequestAuthenticator(application).withTtl(ttl);
    }

    @Override
    public ResourceRequestAuthenticator inLocation(RequestLocation... locations) {
        return Applications.oauthRequestAuthenticator(application).inLocation(locations);
    }

}
