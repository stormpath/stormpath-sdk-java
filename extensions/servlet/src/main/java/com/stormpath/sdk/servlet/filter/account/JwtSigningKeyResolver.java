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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.authc.AuthenticationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtSigningKeyResolver {

    String getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result);

    //TODO: invoke this method when JJWT releases SigningKeyResolver functionality:
    String getSigningKey(HttpServletRequest request, HttpServletResponse response,
                         JwsHeader jwsHeader, Claims claims);

}
