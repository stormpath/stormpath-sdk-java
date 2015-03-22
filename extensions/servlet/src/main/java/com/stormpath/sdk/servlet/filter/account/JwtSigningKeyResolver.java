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

import com.stormpath.sdk.authc.AuthenticationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;

/**
 * A JWT Signing Key Resolver can inspect a request and return an appropriate cryptographic signing key used to create
 * or verify JWT signatures.  Both method implementations <em>MUST</em> return the exact same signing key.
 *
 * @since 1.0.RC3
 */
public interface JwtSigningKeyResolver {

    /**
     * Inspects the request and authentication result and returns a cryptographic signature key that will be used to
     * digitally sign a JWT using the specified {@code alg}orithm.  The JWT will represent the authenticated account.
     *
     * <p>This method implementation <em>MUST</em> return the exact same signing key as what is returned by {@link
     * #getSigningKey(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * io.jsonwebtoken.JwsHeader, io.jsonwebtoken.Claims)}.</p>
     *
     * @param request  the inbound request
     * @param response the outbound response
     * @param result   the result for which an account JWT will be created
     * @param alg      the JWT signature algorithm that will be used to generate the JWT
     * @return a cryptographic signature key that will be used to digitally sign a JWT using the specified {@code
     * alg}orithm.
     */
    Key getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result,
                      SignatureAlgorithm alg);

    /**
     * Inspects the method arguments and returns the cryptographic signing key that will be used to verify the digitally
     * signed JWT that reflects the header and claims.
     *
     * <p>This method implementation <em>MUST</em> return the exact same signing key as what is returned by {@link
     * #getSigningKey(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * com.stormpath.sdk.authc.AuthenticationResult, io.jsonwebtoken.SignatureAlgorithm)}.</p>
     *
     * @param request   the inbound request
     * @param response  the outbound response
     * @param jwsHeader the header of the JWT that will be cryptographically verified.  Can be inspected (for example)
     *                  to find a JWT-standard {@code kid} field that indicates which signing key was used.
     * @param claims    the claims of the JWT that will be cryptographically verified.  Can be inspected for information
     *                  that might reveal which signing key was used and which should be returned.
     * @return
     */
    Key getSigningKey(HttpServletRequest request, HttpServletResponse response, JwsHeader jwsHeader, Claims claims);
}
