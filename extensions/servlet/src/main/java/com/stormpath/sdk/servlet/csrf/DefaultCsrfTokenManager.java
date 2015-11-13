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
package com.stormpath.sdk.servlet.csrf;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
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

/**
 * Default implementation of the {@link com.stormpath.sdk.servlet.csrf.CsrfTokenManager} interface.  To ensure correct
 * behavior, the specified TTL <em>MUST</em> be the equal to or greater than the specified nonce cache's TTL.
 *
 * @since 1.0.RC3
 */
public class DefaultCsrfTokenManager implements CsrfTokenManager {

    private static final String DEFAULT_CSRF_TOKEN_NAME = "csrfToken";

    private static final Logger log = LoggerFactory.getLogger(DefaultCsrfTokenManager.class);

    private final Cache<String, String> nonceCache;
    private final String signingKey;
    private final long ttlMillis;
    private final String tokenName;

    /**
     * Instantiates a new DefaultCacheManager.
     *
     * @param tokenName  The name that will be used to identify the CSRF token. This name is used to obtain the token from the forms for example.
     * @param nonceCache a cache to place used CSRF tokens.  This is required to ensure the consumed token is never used
     *                   again, which is mandatory for CSRF protection.  This cache <em>MUST</em> have a TTL value equal
     *                   to or greater than {@code ttlMillis}. Cache key: a unique token ID, Cache value: the used
     *                   token
     * @param signingKey a (hopefully secure-random) cryptographic signing key used to digitally sign the CSRF token to
     *                   ensure it cannot be tampered with by HTTP clients.
     * @param ttlMillis  the length of time in milliseconds for which a generated CSRF token is valid.  When a token is
     *                   created, it cannot be used after this duration, even if it has not been consumed yet.
     */
    public DefaultCsrfTokenManager(String tokenName, Cache<String, String> nonceCache, String signingKey, long ttlMillis) {
        Assert.notNull(nonceCache, "nonce cache cannot be null.");
        this.nonceCache = nonceCache;
        Assert.hasText(signingKey, "signingKey cannot be null or empty.");
        this.signingKey = signingKey;
        Assert.isTrue(ttlMillis > 0, "ttlMillis must be greater than zero.");
        this.ttlMillis = ttlMillis;
        this.tokenName = Strings.hasText(tokenName) ? tokenName : DEFAULT_CSRF_TOKEN_NAME;
    }

    @Override
    public String getTokenName() {
        return this.tokenName;
    }

    @Override
    public String createCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        String id = UUID.randomUUID().toString().replace("-", "");

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + ttlMillis);

        return Jwts.builder().setId(id).setIssuedAt(now).setNotBefore(now).setExpiration(exp)
                   .signWith(SignatureAlgorithm.HS256, signingKey).compact();
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
