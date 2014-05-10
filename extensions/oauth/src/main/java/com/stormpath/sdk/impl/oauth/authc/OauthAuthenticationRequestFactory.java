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
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory;
import com.stormpath.sdk.oauth.authc.BearerLocation;

import javax.servlet.http.HttpServletRequest;

/**
 * OauthAuthenticationRequestFactory
 *
 * @since 1.0.RC
 */
public class OauthAuthenticationRequestFactory extends ApiAuthenticationRequestFactory {

    public AuthenticationRequest createFrom(HttpServletRequest httpServletRequest) {
        String authzHeaderValue = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return new DefaultBearerOauthAuthenticationRequest(httpServletRequest, new BearerLocation[]{BearerLocation.HEADER});
        }
        return new DefaultBasicOauthAuthenticationRequest(httpServletRequest, null);
    }

    @Override
    public AuthenticationRequest createFrom(HttpRequest httpRequest) {
        String authzHeaderValue = httpRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return new DefaultBearerOauthAuthenticationRequest(httpRequest, new BearerLocation[]{BearerLocation.HEADER});
        }
        return new DefaultBasicOauthAuthenticationRequest(httpRequest, null);
    }
}
