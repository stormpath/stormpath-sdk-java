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
package com.stormpath.sdk.impl.oauth.issuer;


import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import org.apache.oltu.oauth2.as.issuer.ValueGenerator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class HmacValueGenerator implements ValueGenerator {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String ALGORITHM = "HmacSHA256";

    private final static byte SEPARATOR = 0x3A; //UTF-8 value for semicolon.

    private final byte[] secretKey;

    /**
     * @param secretKey - This is the the application's secret.
     */
    public HmacValueGenerator(String secretKey) {
        Assert.hasText(secretKey);
        this.secretKey = secretKey.getBytes(UTF_8);
    }

    @Override
    public String generateValue() throws OAuthSystemException {
        return generateValue(UUID.randomUUID().toString());
    }

    @Override
    public String generateValue(String message) throws OAuthSystemException {
        Assert.hasText(message);

        byte[] messageBytes = message.getBytes(UTF_8);

        byte[] hmac = computeHmac(messageBytes);

        byte[] resultMessage = new byte[hmac.length + messageBytes.length + 1];

        System.arraycopy(messageBytes, 0, resultMessage, 0, messageBytes.length);

        resultMessage[messageBytes.length] = SEPARATOR;

        System.arraycopy(hmac, 0, resultMessage, messageBytes.length + 1, hmac.length);

        //return Base64.encodeBase64URLSafeString(resultMessage);
        return Base64.encodeToString(resultMessage, false);
    }

    private byte[] computeHmac(byte[] messageBytes) {
        Assert.notNull(messageBytes, "msg to digest cannot be null or empty");
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey, ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(messageBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The algorithm provided is not valid: " + ALGORITHM, e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("The calculated SecretKey is not valid for algorithm: " + ALGORITHM, e);
        }
    }
}
