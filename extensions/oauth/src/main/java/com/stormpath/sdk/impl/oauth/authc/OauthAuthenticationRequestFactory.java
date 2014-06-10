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
import com.stormpath.sdk.error.authc.InvalidAuthenticationException;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.oauth.authc.RequestLocation;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class OauthAuthenticationRequestFactory extends ApiAuthenticationRequestFactory {

    public AuthenticationRequest createFrom(HttpServletRequest httpServletRequest) {
        String authzHeaderValue = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        try {
            if (schemeAndValue == null) {

                HttpMethod method = HttpMethod.fromName(httpServletRequest.getMethod());

                if (method != HttpMethod.GET && hasContentType(httpServletRequest.getHeader(CONTENT_TYPE_HEADER), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    return new DefaultBearerOauthAuthenticationRequest(httpServletRequest, new RequestLocation[]{RequestLocation.BODY});
                }

            } else {
                if (schemeAndValue[0].equalsIgnoreCase(BASIC_AUTHENTICATION_SCHEME)) {
                    return new DefaultBasicOauthAuthenticationRequest(httpServletRequest, null, DefaultBasicOauthAuthenticationRequest.DEFAULT_TTL);
                } else if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {

                    boolean hasApplicationFormUrlEncoded = hasContentType(httpServletRequest.getHeader(CONTENT_TYPE_HEADER), MediaType.APPLICATION_FORM_URLENCODED_VALUE);

                    return new DefaultBearerOauthAuthenticationRequest(httpServletRequest, getRequestLocations(true, hasApplicationFormUrlEncoded));
                }
            }
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(InvalidAuthenticationException.class);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }
    }
}
