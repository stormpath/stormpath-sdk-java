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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.RequestLocation;

import java.lang.reflect.Constructor;

public class OauthAuthenticationRequestFactory {

    public static final OauthAuthenticationRequestFactory INSTANCE = new OauthAuthenticationRequestFactory();

    private static final String BASIC_OAUTH_REQUEST_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest";

    private static final String BEARER_OAUTH_REQUEST_FQCN =
        "com.stormpath.sdk.impl.oauth.authc.ResourceAuthenticationRequest";

    private static final String OAUTH_REQUEST_NOT_AVAILABLE_MSG = "Unable to find the OauthRequest implementation on " +
                                                                  "the classpath. Please ensure you have added the " +
                                                                  "stormpath-sdk-oauth-{version}.jar file to your " +
                                                                  "runtime classpath.";

    @SuppressWarnings("unchecked")
    public AuthenticationRequest createRequest(HttpRequest request, RequestLocation[] requestLocations) {

        Assert.isTrue(Classes.isAvailable(BEARER_OAUTH_REQUEST_FQCN), OAUTH_REQUEST_NOT_AVAILABLE_MSG);

        Class<AuthenticationRequest> clazz = Classes.forName(BEARER_OAUTH_REQUEST_FQCN);

        Constructor<AuthenticationRequest> constructor =
            Classes.getConstructor(clazz, HttpRequest.class, RequestLocation[].class);

        return instantiate(constructor, request, requestLocations);
    }

    @SuppressWarnings("unchecked")
    public AuthenticationRequest createTokenRequest(HttpRequest request) {

        Assert.isTrue(Classes.isAvailable(BASIC_OAUTH_REQUEST_FQCN), OAUTH_REQUEST_NOT_AVAILABLE_MSG);

        Class<AuthenticationRequest> clazz = Classes.forName(BASIC_OAUTH_REQUEST_FQCN);

        Constructor<AuthenticationRequest> constructor = Classes.getConstructor(clazz, HttpRequest.class);

        return instantiate(constructor, request);
    }

    protected AuthenticationRequest instantiate(Constructor<AuthenticationRequest> ctor, Object... args) {
        try {
            return Classes.instantiate(ctor, args);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory
                .newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }
    }
}
