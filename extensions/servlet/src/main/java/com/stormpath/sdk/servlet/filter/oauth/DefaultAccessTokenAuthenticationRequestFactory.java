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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import javax.servlet.http.HttpServletRequest;

public class DefaultAccessTokenAuthenticationRequestFactory implements AccessTokenAuthenticationRequestFactory {

    protected static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    @Override
    public AuthenticationRequest createAccessTokenAuthenticationRequest(HttpServletRequest request)
        throws AccessTokenRequestException {

        String grantType = Strings.clean(request.getParameter(GRANT_TYPE_PARAM_NAME));
        //this is asserted in the AccessTokenFilter so it should never be null/empty here:
        Assert.hasText(grantType, "grant_type must not be null or empty.");

        if ("password".equals(grantType)) {
            return createUsernamePasswordRequest(request);
        }

        throw new AccessTokenRequestException(AccessTokenErrorCode.UNSUPPORTED_GRANT_TYPE);
    }

    protected UsernamePasswordRequest createUsernamePasswordRequest(HttpServletRequest request)
        throws AccessTokenRequestException {

        String username = Strings.clean(request.getParameter("username"));
        if (username == null) {
            throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_REQUEST, "Missing username value.", null);
        }

        String password = Strings.clean(request.getParameter("password"));
        if (password == null) {
            throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_REQUEST, "Missing password value.", null);
        }

        return new UsernamePasswordRequest(username, password, request.getRemoteHost());
    }
}
