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

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;

import javax.servlet.http.HttpServletRequest;

public class OriginAccessTokenRequestAuthorizer implements AccessTokenRequestAuthorizer {

    @Override
    public void assertAuthorizedAccessTokenRequest(HttpServletRequest request) throws OauthException {
        String origin = request.getHeader("Origin");
        if (!Strings.hasText(origin)) {
            throw new OauthException(OauthErrorCode.INVALID_CLIENT, "Missing Origin header.", null);
        }

        String uri = ServerUriResolver.INSTANCE.getServerUri(request);
        if (!origin.startsWith(uri)) {
            throw new OauthException(OauthErrorCode.INVALID_CLIENT, "Unauthorized Origin.", null);
        }
    }
}
