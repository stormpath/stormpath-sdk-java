/*
 * Copyright 2015 Stormpath, Inc.
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

import com.stormpath.sdk.account.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A JWT Account Resolver can inspect a String token (a JWT), assert the JWT's validity by verifying a cryptographic
 * signature, and if valid, return the {@link Account} instance reflected by the token.
 *
 * <p>This behavior is useful for Token Authentication or Signle Sign-On use cases.</p>
 *
 * @since 1.0.RC3
 */
public interface JwtAccountResolver {

    /**
     * Authenticates and validates the specified JWT token string and then returns the {@link Account} corresponding to
     * the valid JWT.  If the JWT is invalid, it will throw a runtime exception.
     *
     * @param request  inbound request
     * @param response outbound response
     * @param jwt      the JWT token string to authenticate and inspect for an associated Account identity.
     * @return the {@link Account} corresponding to the valid JWT
     */
    Account getAccountByJwt(HttpServletRequest request, HttpServletResponse response, String jwt);

}
