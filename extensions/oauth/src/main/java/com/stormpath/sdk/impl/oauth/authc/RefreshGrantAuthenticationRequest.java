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

import com.stormpath.sdk.authc.AuthenticationOptions;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.http.ServletHttpRequest;
import com.stormpath.sdk.impl.oauth.http.OauthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.ScopeFactory;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC8
 */
public class RefreshGrantAuthenticationRequest extends OAuthTokenRequest implements AuthenticationRequest<String, String> {

    public static final long DEFAULT_REFRESH_TOKEN_TTL = 3600 * 24 * 365; // Default TTL for refresh tokens is 1 year

    private final long ttl;

    @SuppressWarnings("UnusedDeclaration") //used via reflection in com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory
    public RefreshGrantAuthenticationRequest(HttpRequest httpRequest) throws Exception {
        this(getHttpServletRequest(httpRequest), DEFAULT_REFRESH_TOKEN_TTL);
    }

    public RefreshGrantAuthenticationRequest(HttpServletRequest httpServletRequest, long ttl) throws Exception {
        super(httpServletRequest);
        Assert.isTrue(ttl > 0, "ttl cannot be less or equal to 0 (zero).");

        this.ttl = ttl;

    }

    private static HttpServletRequest getHttpServletRequest(HttpRequest httpRequest) {
        if (httpRequest instanceof ServletHttpRequest) {
            return ((ServletHttpRequest)httpRequest).getHttpServletRequest();
        } else {
            return new OauthHttpServletRequest(httpRequest);
        }
    }

    public long getTtl() {
        return ttl;
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

    @Override
    public AuthenticationOptions getResponseOptions() {
        throw new UnsupportedOperationException(getClass().getName() + " .getResponseOptions() is not supported.");
    }

    @Override
    public String getPrincipals() {
        throw new UnsupportedOperationException(getClass().getName() + " .getPrincipals() is not supported.");
    }

    @Override
    public String getCredentials() {
        throw new UnsupportedOperationException(getClass().getName() + " .getCredentials() is not supported.");
    }
}
