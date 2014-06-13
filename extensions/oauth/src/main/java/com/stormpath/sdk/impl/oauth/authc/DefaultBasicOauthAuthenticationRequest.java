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

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.oauth.http.OauthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.ScopeFactory;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBasicOauthAuthenticationRequest extends OAuthTokenRequest implements AuthenticationRequest<String, String> {

    public static final long DEFAULT_TTL = 3600;

    private final ScopeFactory scopeFactory;

    private final long ttl;

    private final String id;

    private final String secret;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG = "HttpRequest class [%s] is not supported. Supported classes: [%s, %s].";

    //This is used via reflection by: com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory
    //Don't delete it.
    public DefaultBasicOauthAuthenticationRequest(Object httpRequest) throws Exception {
        this(getHttpServletRequest(httpRequest), null, DEFAULT_TTL);
    }

    public DefaultBasicOauthAuthenticationRequest(HttpServletRequest httpServletRequest, ScopeFactory scopeFactory, long ttl) throws Exception {
        super(httpServletRequest);
        Assert.isTrue(ttl > 0, "ttl cannot be less or equal to 0 (zero).");

        this.scopeFactory = scopeFactory;

        String[] credentials = OAuthUtils.decodeClientAuthenticationHeader(request.getHeader(OAuth.HeaderType.AUTHORIZATION));
        if (credentials == null) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(MissingApiKeyException.class);
        }
        this.id = credentials[0];
        this.secret = credentials[1];
        this.ttl = ttl;
    }

    private static HttpServletRequest getHttpServletRequest(Object httpRequest) {
        HttpServletRequest httpServletRequest;
        Class httpRequestClass = httpRequest.getClass();
        if (HttpServletRequest.class.isAssignableFrom(httpRequestClass)) {
            httpServletRequest = (HttpServletRequest) httpRequest;
        } else if (HttpRequest.class.isAssignableFrom(httpRequestClass)) {
            httpServletRequest = new OauthHttpServletRequest((HttpRequest) httpRequest);
        } else {
            throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequest.getClass(), HttpRequest.class.getName(), HttpServletRequest.class.getName()));
        }
        return httpServletRequest;
    }

    public long getTtl() {
        return ttl;
    }

    public ScopeFactory getScopeFactory() {
        return scopeFactory;
    }

    public boolean hasScopeFactory() {
        return scopeFactory != null;
    }

    public String getClientId() {
        return id;
    }

    public String getClientSecret() {
        return secret;
    }

    @Override
    public String getPrincipals() {
        return id;
    }

    @Override
    public String getCredentials() {
        return secret;
    }

    @Override
    public String getHost() {
        throw new UnsupportedOperationException("getHost() method hasn't been implemented.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() method hasn't been implemented.");
    }

    @Override
    public AccountStore getAccountStore() {
        throw new UnsupportedOperationException("getAccountStore() method hasn't been implemented.");
    }
}
