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
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.http.ServletHttpRequest;
import com.stormpath.sdk.impl.oauth.http.OAuthHttpServletRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.RequestLocation;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class ResourceAuthenticationRequest extends OAuthAccessResourceRequest implements AuthenticationRequest {

    private final HttpServletRequest httpServletRequest;

    @SuppressWarnings("UnusedDeclaration")
    //used via reflection by com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory
    public ResourceAuthenticationRequest(HttpRequest request, RequestLocation[] requestLocations)
        throws OAuthSystemException, OAuthProblemException {
        this(toHttpServletRequest(request), requestLocations);
    }

    public ResourceAuthenticationRequest(HttpServletRequest httpServletRequest, RequestLocation[] requestLocations)
        throws OAuthProblemException, OAuthSystemException {
        super(httpServletRequest, new TokenType[]{ TokenType.BEARER }, convert(requestLocations));
        Assert.notNull(httpServletRequest, "httpServletRequest cannot be null");
        this.httpServletRequest = httpServletRequest;
    }

    private static HttpServletRequest toHttpServletRequest(HttpRequest httpRequest) {
        if (httpRequest instanceof ServletHttpRequest) {
            return ((ServletHttpRequest) httpRequest).getHttpServletRequest();
        } else {
            return new OAuthHttpServletRequest(httpRequest);
        }
    }

    private static ParameterStyle[] convert(RequestLocation[] requestLocations) {
        Assert.notNull(requestLocations);

        ParameterStyle[] parameterStyles = new ParameterStyle[requestLocations.length];

        int index = 0;

        for (RequestLocation requestLocation : requestLocations) {
            switch (requestLocation) {
                case HEADER:
                    parameterStyles[index] = ParameterStyle.HEADER;
                    break;

                case BODY:
                    parameterStyles[index] = ParameterStyle.BODY;
                    break;

                case QUERY_PARAM:
                    parameterStyles[index] = ParameterStyle.QUERY;
                    break;
                default:
                    throw new IllegalArgumentException("requestLocations has an illegal argument.");
            }
            index++;
        }
        return parameterStyles;
    }

    @Override
    public Object getPrincipals() {
        throw new UnsupportedOperationException(getClass().getName() + " .getPrincipals() is not supported.");
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException(getClass().getName() + " .getCredentials() is not supported.");
    }

    @Override
    public String getHost() {
        return httpServletRequest.getRemoteHost();
    }

    @Override
    public void clear() {
        //no-op
    }

    @Override
    public AccountStore getAccountStore() {
        throw new UnsupportedOperationException(getClass().getName() + " .getAccountStore() is not supported.");
    }

    /* @since 1.2.0 */
    @Override
    public String getOrganizationNameKey() {
        throw new UnsupportedOperationException(getClass().getName() + " .getOrganizationNameKey() is not supported.");
    }

    /* @since 1.0.RC5 */
    @Override
    public AuthenticationOptions getResponseOptions() {
        throw new UnsupportedOperationException(getClass().getName() + " .getResponseOptions() is not supported.");
    }

}
