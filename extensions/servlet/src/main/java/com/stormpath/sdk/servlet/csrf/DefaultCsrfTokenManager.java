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
package com.stormpath.sdk.servlet.csrf;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

public class DefaultCsrfTokenManager implements CsrfTokenManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultCsrfTokenManager.class);

    private final Cache<String,String> nonceCache;
    private final String signingKey;
    private final long ttlMillis;

    public DefaultCsrfTokenManager(Cache<String,String> nonceCache, String signingKey, long ttlMillis) {
        Assert.notNull(nonceCache, "nonce cache cannot be null.");
        this.nonceCache = nonceCache;
        Assert.hasText(signingKey, "signingKey cannot be null or empty.");
        this.signingKey = signingKey;
        Assert.isTrue(ttlMillis > 0, "ttlMillis must be greater than zero.");
        this.ttlMillis = ttlMillis;
    }

    @Override
    public String createCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        String id = UUID.randomUUID().toString().replace("-", "");

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + ttlMillis);

        return Jwts.builder()
                   .setId(id)
                   .setIssuedAt(now)
                   .setNotBefore(now)
                   .setExpiration(exp)
                   .signWith(SignatureAlgorithm.HS256, signingKey)
                   .compact();
    }

    protected Client getClient(HttpServletRequest request) {
        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "Client must be present as a request attribute.");
        return client;
    }

    @Override
    public boolean isValidCsrfToken(HttpServletRequest request, HttpServletResponse response, String csrfToken) {

        if (csrfToken == null) {
            return false;
        }

        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(csrfToken);

            //signature is valid, now let's ensure it hasn't been submitted before:

            String id = jws.getBody().getId();

            String usedNonce = nonceCache.get(id);

            if (usedNonce == null) {
                //CSRF token hasn't been used yet, mark it as used:
                nonceCache.put(id, csrfToken);
                return true;
            }
        } catch (Exception e) {
            log.debug("CSRF token is invalid (this is likely to happen and not an error condition).", e);
        }

        return false;
    }
}
