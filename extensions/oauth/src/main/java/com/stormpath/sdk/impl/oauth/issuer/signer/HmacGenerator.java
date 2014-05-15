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
package com.stormpath.sdk.impl.oauth.issuer.signer;

import com.stormpath.sdk.lang.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @since 1.0.RC
 */
public class HmacGenerator {

    private final Charset ENCODING_CHARSET;

    private final String ALGORITHM;

    protected HmacGenerator(String algorithm, Charset charset) {
        Assert.hasText(algorithm, "algorithm cannot be null or empty.");
        Assert.notNull(charset, "charset cannot be null or empty.");
        this.ENCODING_CHARSET = charset;
        this.ALGORITHM = algorithm;
    }

    public byte[] computeHmac(String msg, byte[] secretKey) {
        Assert.hasText(msg, "msg to digest cannot be null or empty");
        Assert.notNull(secretKey, "secretKey cannot be null.");
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey, ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(msg.getBytes(ENCODING_CHARSET));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The algorithm provided is not valid: " + ALGORITHM, e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("The calculated SecretKey is not valid for algorithm: " + ALGORITHM, e);
        }
    }
}
