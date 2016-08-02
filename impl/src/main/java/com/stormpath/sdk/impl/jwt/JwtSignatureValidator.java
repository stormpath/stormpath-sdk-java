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
package com.stormpath.sdk.impl.jwt;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.client.ClientCredentials;
import com.stormpath.sdk.error.jwt.InvalidJwtException;
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.jwt.signer.JwtSigner;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC
 */
public class JwtSignatureValidator {

    private final JwtSigner jwtSigner;

    public JwtSignatureValidator(ClientCredentials clientCredentials) {

        Assert.notNull(clientCredentials, "clientCredentials cannot be null.");

        jwtSigner = new DefaultJwtSigner(clientCredentials.getId(), clientCredentials.getSecret());
    }

    /**
     * @param jwtWrapper - A wrapper {@link JwtWrapper} instance containing the Json Web Token information.
     * @throws com.stormpath.sdk.error.jwt.InvalidJwtException
     */
    public void validate(JwtWrapper jwtWrapper) throws InvalidJwtException {
        Assert.notNull(jwtWrapper, "jwtWrapper cannot be null.");

        String calculatedSignature = jwtSigner.calculateSignature(jwtWrapper.getBase64JwtHeader(), jwtWrapper.getBase64JsonPayload());

        if (jwtWrapper.getBase64JwtSignature().equals(calculatedSignature)) {
            return;
        }
        throw new InvalidJwtException(InvalidJwtException.INVALID_JWT_SIGNATURE_ERROR);
    }
}
