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

import com.stormpath.sdk.authc.*;
import com.stormpath.sdk.client.*;
import com.stormpath.sdk.impl.util.*;
import com.stormpath.sdk.lang.*;
import io.jsonwebtoken.*;

import javax.crypto.spec.*;
import javax.servlet.http.*;
import java.security.*;

/**
 * @since 1.0.RC3
 */
public class DefaultJwtSigningKeyResolver implements JwtSigningKeyResolver {

    private static final String RSA_ERR_MSG = "RSA signatures are not currently supported by the " +
                                              DefaultJwtSigningKeyResolver.class.getName() + " implementation.  You " +
                                              "may want to implement your own JwtSigningKeyResolver implementation " +
                                              "to support RSA keys.";

    private static final String EC_ERR_MSG = "Elliptic Curve signatures are not currently supported by the " +
                                             DefaultJwtSigningKeyResolver.class.getName() + " implementation.  You " +
                                             "may want to implement your own JwtSigningKeyResolver implementation " +
                                             "to support Elliptic Curve keys.";

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result,
                             SignatureAlgorithm alg) {
        Assert.isTrue(!alg.isRsa(), RSA_ERR_MSG);
        Assert.isTrue(!alg.isEllipticCurve(), EC_ERR_MSG);
        return getSigningKey(request, alg);
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, JwsHeader jwsHeader,
                             Claims claims) {
        return getSigningKey(request, SignatureAlgorithm.forName(jwsHeader.getAlgorithm()));
    }

    protected Key getSigningKey(HttpServletRequest request, SignatureAlgorithm alg) {

        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "Client must be accessible as a request attribute.");

        String apiKeySecret = client.getApiKey().getSecret();

        //Stormpath API Keys are base-64-encoded secure random byte arrays:
        byte[] apiKeySecretBytes = Base64.decodeBase64(apiKeySecret);

        return new SecretKeySpec(apiKeySecretBytes, alg.getJcaName());

    }
}
