/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.convert;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Converts a <code>Map&lt;String,?&gt;</code> to a compact JWT string.
 *
 * <p>See the {@link #MapToJwtConverter(Map, Map, String, SignatureAlgorithm, Key) constructor JavaDoc} for usage
 * options.</p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * @since 1.3.0
 */
public class MapToJwtConverter implements Function<Map<String, ?>, String> {

    private final Map<String,?> baseHeader;

    private final Map<String, ?> baseClaims;

    private final String valueClaimName;

    private final Long expirationSeconds;

    private final Long notBeforeSeconds;

    private final SignatureAlgorithm signatureAlgorithm;

    private final Key signingKey;

    /**
     * Creates a new instance based on the specified arguments.
     *
     * <h5>{@code baseHeader}</h5>
     *
     * <p>The optional (nullable) {@code baseHeader} argument allows you to supply any base header name/value pairs
     * that should be in the JWT before the JWT is constructed.  Any header values required by JWT construction
     * will overwrite any identically named values in this map.  For example, {@code iat} - 'issued at' will always
     * be the timestamp when the JWT is constructed, even if {@code baseHeader} contains an {@code iat} member.</p>
     *
     * <p>A common reason for setting this value would be, for example, to set the identifier of the signing key used
     * during signing (via the {@code kid} header).  This allows a recipient of the JWT to look up the same signing
     * key based on the {@code kid} header so the recipient can verify the signature.</p>
     *
     * <h5>{@code baseClaims}</h5>
     *
     * <p>The optional (nullable) {@code baseClaims} argument allows you to supply any base claims you wish to add as
     * JWT claims <em>before</em> the function value's name/value pairs are applied as claims.</p>
     *
     * <p>For example, consider the following code with a {@code baseClaims} constructor argument:</p>
     *
     * <pre><code>
     * Map&lt;String,Object&gt; baseClaims = new HashMap&lt;&gt;();
     * baseClaims.put("iss", "My Company");
     * baseClaims.put("aud", "mywebapp.com");
     *
     * MapToJwtConverter converter = new MapToJwtConverter(baseClaims, null, null, null);
     * </code></pre>
     *
     * <p>If you were to invoke this converter with a Map value:</p>
     *
     * <pre><code>
     * Map&lt;String,Object&gt; value = new HashMap&lt;&gt;();
     * value.put("username", "jsmith");
     * value.put("email", "jsmith@mailinator.com");
     *
     * String jwt = converter.apply(value);
     * </code></pre>
     *
     * <p>The resulting JWT claims will be the set union of both maps and look like this:</p>
     *
     * <pre><code>
     *     {
     *         "iss": "My Company",
     *         "aud": "webapp.com",
     *         "username": "jsmith",
     *         "email", "jsmith@mailinator.com"
     *     }
     * </code></pre>
     *
     * <p>Any name/value pairs in the function value take precedence and will overwrite (replace) any identically
     * named pairs from the {@code baseClaims}.</p>
     *
     * <p>If you don't want the {@code value} name/value pairs to be set as common/top-level claims and instead want
     * them to be set as a single claim with nested pairs, specify the {@code valueClaimName} argument.</p>
     *
     * <p>A {@code null} {@code baseClaims} argument value indicates no base claims need to be represented in the
     * resulting JWT.</p>
     *
     * <h5>{@code valueClaimName}</h5>
     *
     * <p>By default, any name/value pairs in the Map value supplied to the {@link #apply(Map) apply} method will be
     * merged and potentially overwrite any pairs that might have been set in the {@code baseClaims}.</p>
     *
     * <p>If you don't want the function value's name/value pairs to be intermixed with any other JWT base claims, you
     * can set the {@code valueClaimName} argument and that will be used to set a single top-level JWT claim using
     * the entire Map value as the claim value.</p>
     *
     * <p>For example, consider the following code with a {@code valueClaimName} constructor argument:</p>
     *
     * <pre><code>
     * MapToJwtConverter converter = new MapToJwtConverter(null, "account", null, null, null);
     * </code></pre>
     *
     * <p>If you were to invoke this converter with a Map value:</p>
     *
     * <pre><code>
     * Map&lt;String,Object&gt; value = new HashMap&lt;&gt;();
     * value.put("username", "jsmith");
     * value.put("email", "jsmith@mailinator.com");
     *
     * String jwt = converter.apply(value);
     * </code></pre>
     *
     * <p>The resulting JWT claims will include an {@code account} claim with the same value as the function
     * argument.  The resulting JWT claims would look like this:</p>
     *
     * <pre><code>
     *     {
     *         "iat": "2016-12-15T19:58:55.272Z",
     *         //other claims added by the JWT building process truncated for brevity...
     *         "account": {
     *             "username": "jsmith",
     *             "email": "jsmith@mailinator.com"
     *         }
     *     }
     * </code></pre>
     *
     * <p>The entire map value is 'nested' under a claim name equal to the specified {@code valueClaimName} argument
     * value ("account" in this example).</p>
     *
     * <p>A {@code null} {@code valueClaimName} argument value indicates that any name/value pairs from the function
     * value should not be nested, and instead be represented as common/top-level JWT claims.</p>
     *
     * <h5>{@code signatureAlgorithm}</h5>
     *
     * <p>The JWT signature algorithm to use when signing the JWT with the specified {@code signingKey} argument.</p>
     *
     * <p>A {@code null} value indicates the JWT should not be signed at all.
     * <b>WARNING:</b> JWT values in most production environments should usually always be signed to ensure the JWT
     * cannot be manipulated after construction.  Not signing JWTs usually leads to security risks.</p>
     *
     * <p>If {@code signatureAlgorithm} is specified (non-null), the {@code signingKey} argument must be specified as
     * well.</p>
     *
     * <h5>{@code signingKey}</h5>
     *
     * <p>The signing key to use when signing the JWT based on the specified {@code signatureAlgorithm} argument.</p>
     *
     * <p>If {@code signatureAlgorithm} is provided, the {@code signingKey} must be provided as well.</p>
     *
     * @param baseHeader any base name/value pairs to add to the JWT header before constructing the JWT, or
     *                   {@code null} if no base header pairs are desired.
     * @param baseClaims any base claims to add to the JWT before the function value is applied as JWT baseClaims,
     *                   or {@code null} if no base claims are desired.
     * @param valueClaimName the name of the JWT claim to represent/'wrap' the function value's name/value pairs, or
     *                  {@code null} if any value pairs should be added directly as common/top-level JWT claims.
     * @param signatureAlgorithm the signature algorithm to use when signing the key or {@code null} if the JWT should
     *                           not be signed.
     * @param signingKey the signing key to use with the specified {@code signatureAlgorithm} or {@code null} if the
     *                   JWT should not be signed.
     * @param expirationSeconds the number of seconds to add to the JWT's creation timestamp, the resulting value of
     *                          which will be used to set the {@code exp} Date claim.  A {@code null} value indicates
     *                          that the {@code exp} claim will not be set.
     * @param notBeforeSeconds the number of seconds to add (or subtract) to the JWT's creation timestamp, the
     *                         resulting value of which will be used to set the {@code nbf} Date claim.  A {@code null}
     *                         value indicates that the {@code nbf} claim will not be set.
     */
    public MapToJwtConverter(Map<String,?> baseHeader, Map<String, ?> baseClaims, String valueClaimName,
                             SignatureAlgorithm signatureAlgorithm, Key signingKey, Long expirationSeconds,
                             Long notBeforeSeconds) {

        this.valueClaimName = valueClaimName;

        if (baseHeader == null) {
            this.baseHeader = java.util.Collections.emptyMap();
        } else {
            this.baseHeader = baseHeader;
        }

        if (baseClaims == null) {
            this.baseClaims = java.util.Collections.emptyMap();
        } else {
            this.baseClaims = baseClaims;
        }

        this.expirationSeconds = expirationSeconds;
        this.notBeforeSeconds = notBeforeSeconds;

        this.signatureAlgorithm = signatureAlgorithm;
        this.signingKey = signingKey;

        if (signatureAlgorithm != null) {
            Assert.notNull(signingKey, "A signing Key argument is required when specifying a SignatureAlgorithm.");
        } else if (signingKey != null) {
            String msg = "A SignatureAlgorithm argument is required when specifying a signing Key.";
            throw new IllegalArgumentException(msg);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String apply(Map<String, ?> value) {

        JwtBuilder builder = Jwts.builder();

        if (!Collections.isEmpty(baseHeader)) {
            builder.setHeader((Map<String,Object>) baseHeader);
        }

        if (!Collections.isEmpty(baseClaims)) {
            builder.setClaims((Map<String, Object>) baseClaims);
        }

        if (!Collections.isEmpty(value)) {
            if (valueClaimName != null) {
                builder.claim(valueClaimName, value);
            } else {
                for (Map.Entry<String, ?> entry : value.entrySet()) {
                    builder.claim(entry.getKey(), entry.getValue());
                }
            }
        }

        Date now = new Date();
        builder.setIssuedAt(now);

        long nowMillis = now.getTime();

        if (this.expirationSeconds != null) {
            long expMillis = nowMillis + (expirationSeconds * 1000);
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        if (this.notBeforeSeconds != null) {
            long nbfMillis = nowMillis + (notBeforeSeconds * 1000);
            Date nbf = new Date(nbfMillis);
            builder.setNotBefore(nbf);
        }

        if (signatureAlgorithm != null) {
            Assert.notNull(signingKey, "Illegal state: signingKey cannot be null if signatureAlgorithm exists.");
            builder.signWith(signatureAlgorithm, signingKey);
        }

        return builder.compact();
    }
}
