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

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

public class DefaultAuthenticationJwtFactory implements AuthenticationJwtFactory {

    private final JwtSigningKeyResolver jwtSigningKeyResolver;
    private final int jwtTtl;

    public DefaultAuthenticationJwtFactory(JwtSigningKeyResolver jwtSigningKeyResolver, int jwtTtl) {
        Assert.notNull(jwtSigningKeyResolver, "JwtSigningKeyResolver cannot be null.");
        this.jwtSigningKeyResolver = jwtSigningKeyResolver;
        this.jwtTtl = jwtTtl;
    }

    protected JwtSigningKeyResolver getJwtSigningKeyResolver() {
        return jwtSigningKeyResolver;
    }

    protected int getJwtTtl() {
        return jwtTtl;
    }

    @Override
    public String createAccountJwt(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationResult result) {

        String id = createJwtId(request, response, result);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        String sub = getJwtSubject(request, response, result);
        Assert.hasText(sub, "JWT subject value cannot be null or empty.");

        String signingKey = getJwtSigningKey(request, response, result);

        JwtBuilder builder =
            Jwts.builder().setId(id).setIssuedAt(now).setSubject(sub).signWith(SignatureAlgorithm.HS256, signingKey);

        int ttl = getJwtTtlSeconds(request, response, result);
        if (ttl >= 0) {
            long ttlMillis = ttl * 1000; //JWT requires times to be in seconds (not millis) since epoch
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    @SuppressWarnings("UnusedParameters")
    protected String createJwtId(HttpServletRequest request, HttpServletResponse response,
                                 AuthenticationResult result) {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("UnusedParameters")
    protected String getJwtSubject(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationResult result) {

        // If the account was authenticated by HTTP Basic authentication using an API Key, the
        // com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme implementation will indicate this by exposing
        // the API Key used as a request attribute.  So we check for the presence of this attribute to know if the JWT
        // should retain knowledge of how the account was authenticated.

        ApiKey apiKey = (ApiKey) request.getAttribute(ApiKey.class.getName());
        if (apiKey != null) {
            //request was authenticated with an API Key (and not username/password authentication):
            return apiKey.getHref();
        }

        // otherwise the request was authenticated with username/password authentication, so just represent the account
        // directly:
        return result.getAccount().getHref();
    }

    protected String getJwtSigningKey(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationResult result) {
        return getJwtSigningKeyResolver().getSigningKey(request, response, result);
    }

    @SuppressWarnings("UnusedParameters")
    protected int getJwtTtlSeconds(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationResult result) {
        return getJwtTtl();
    }
}
