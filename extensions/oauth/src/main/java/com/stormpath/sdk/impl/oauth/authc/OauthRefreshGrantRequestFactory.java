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
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.impl.authc.ApiAuthenticationRequestFactory;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC8
 */
public class OauthRefreshGrantRequestFactory extends ApiAuthenticationRequestFactory {

    public RefreshGrantAuthenticationRequest createFrom(HttpServletRequest httpServletRequest) {

        final String REFRESH_TOKEN_PARAM_NAME = "refresh_token";

        final String refreshToken = httpServletRequest.getParameter(REFRESH_TOKEN_PARAM_NAME);

        String authzHeaderValue = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        try {
            if (refreshToken != null){
                return new RefreshGrantAuthenticationRequest(httpServletRequest, AccessTokenAuthenticationRequest.DEFAULT_REFRESH_TOKEN_TTL);
            }
            throw ApiAuthenticationExceptionFactory.newOauthRefreshGrantException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOauthRefreshGrantException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }
    }
}

