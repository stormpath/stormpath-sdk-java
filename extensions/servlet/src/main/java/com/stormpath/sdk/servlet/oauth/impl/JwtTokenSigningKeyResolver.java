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
package com.stormpath.sdk.servlet.oauth.impl;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * This SigningKeyResolver will generate a key that is compatible with our <a href="https://docs.stormpath.com/guides/token-management/">Backend-generated Tokens</a>.
 * <p>It can therefore be used to parse an access/refresh token obtained from Stormpath. For example:</p>
 * <pre>
 *     Key signingKey = jwtTokenSigningKeyResolver.getSigningKey(request, response, null, alg);
 *     Claims claims = Jwts.parser().setSigningKey(signingKey.getEncoded()).parseClaimsJws(jwt).getBody();
 * </pre>
 *
 * @since 1.0.RC8.3
 */
public class JwtTokenSigningKeyResolver implements JwtSigningKeyResolver {

    private static final String RSA_ERR_MSG = "RSA signatures are not currently supported by the " +
            DefaultJwtSigningKeyResolver.class.getName() + " implementation.  You " +
            "may want to implement your own JwtSigningKeyResolver implementation " +
            "to support RSA keys.";

    private static final String EC_ERR_MSG = "Elliptic Curve signatures are not currently supported by the " +
            DefaultJwtSigningKeyResolver.class.getName() + " implementation.  You " +
            "may want to implement your own JwtSigningKeyResolver implementation " +
            "to support Elliptic Curve keys.";

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result, SignatureAlgorithm alg) {
        Assert.isTrue(!alg.isRsa(), RSA_ERR_MSG);
        Assert.isTrue(!alg.isEllipticCurve(), EC_ERR_MSG);
        return getSigningKey(request, alg);
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, JwsHeader jwsHeader, Claims claims) {
        return getSigningKey(request, SignatureAlgorithm.forName(jwsHeader.getAlgorithm()));
    }

    protected Key getSigningKey(HttpServletRequest request, SignatureAlgorithm alg) {

        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "Client must be accessible as a request attribute.");

        String apiKeySecret = client.getApiKey().getSecret();

        return new SecretKeySpec(apiKeySecret.getBytes(Charset.forName("UTF-8")), alg.getJcaName());
    }

}
