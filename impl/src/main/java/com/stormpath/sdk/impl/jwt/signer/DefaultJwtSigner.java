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
package com.stormpath.sdk.impl.jwt.signer;

import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;

import java.nio.charset.Charset;

/**
 * DefaultJwtSigner is the default impl of the JwtSigner interface
 *
 * @since 1.0.RC
 */
public class DefaultJwtSigner implements JwtSigner {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final String BASE64_URL_JWT_SIGN_HEADER;

    public final String JWT_SIGN_HEADER;

    private static final char JWT_TOKEN_SEPARATOR = '.';

    private static final String SHA256_ALGORITHM = "HmacSHA256";

    private final HmacGenerator sha256HmacGenerator;

    private final byte[] signingKey;
    
    public DefaultJwtSigner(String kid, String signingKey) {
        JWT_SIGN_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\",\"kid\":\"" + kid + "\"}";

        BASE64_URL_JWT_SIGN_HEADER = Base64.encodeBase64URLSafeString(JWT_SIGN_HEADER.getBytes(UTF_8));

        this.signingKey = signingKey.getBytes(UTF_8);

        sha256HmacGenerator = new HmacGenerator(SHA256_ALGORITHM, UTF_8);
    }

    @Override
    public String sign(String jsonPayload) {
        Assert.hasText(jsonPayload, "jsonPayload cannot be null or empty.");

        String base64UrlJsonPayload = Base64.encodeBase64URLSafeString(jsonPayload.getBytes(UTF_8));

        String signature = calculateSignature(BASE64_URL_JWT_SIGN_HEADER, base64UrlJsonPayload);

        return new StringBuilder(BASE64_URL_JWT_SIGN_HEADER)
            .append(JWT_TOKEN_SEPARATOR)
            .append(base64UrlJsonPayload)
            .append(JWT_TOKEN_SEPARATOR)
            .append(signature)
            .toString();
    }

    @Override
    public String calculateSignature(String base64Header, String base64JsonPayload) {

        String jwsInput = new StringBuilder(base64Header).append(JWT_TOKEN_SEPARATOR)
                                                         .append(base64JsonPayload).toString();

        byte[] hmac = sha256HmacGenerator.computeHmac(jwsInput, signingKey);

        return Base64.encodeBase64URLSafeString(hmac);
    }
}
