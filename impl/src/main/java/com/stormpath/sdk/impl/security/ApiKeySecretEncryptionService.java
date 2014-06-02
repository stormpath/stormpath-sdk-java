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
package com.stormpath.sdk.impl.security;

import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;

/**
 * @since 1.0.RC
 */
public class ApiKeySecretEncryptionService implements EncryptionService {

    private static String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final int BITS_PER_BYTE = 8;

    private final SecretKey key;

    private final Builder builder;

    private final Cipher cipher;

    private ApiKeySecretEncryptionService(Builder builder) {

        char[] password = builder.password;

        byte[] base64Salt = builder.base64Salt;

        int keySize = builder.keySize;

        int iterations = builder.iterations;

        this.builder = builder;

        Assert.state(password != null && password.length > 0, "password cannot be null or empty.");

        Assert.state(base64Salt != null && base64Salt.length > 0, "salt cannot be null or empty.");

        Assert.state(keySize > 0, "the key size must be greater than zero.");

        Assert.state(iterations > 0, "the number of iterations must be greater than zero.");

        key = initKey(password, Base64.decodeBase64(base64Salt), keySize, iterations);

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey initKey(char[] password, byte[] salt, int keySize, int iterations) {

        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);

            KeySpec keySpec = new PBEKeySpec(password, salt, iterations, keySize);

            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);

            SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

            return secret;

        } catch (Exception e) {
            throw new IllegalStateException("Could not create the encryption key.", e);
        }
    }

    @Override
    public String decryptBase64String(String base64EncryptedValue) {
        Assert.hasText(base64EncryptedValue);
        Assert.isTrue(Base64.isBase64(base64EncryptedValue.getBytes()), "encryptedValue argument must be Base64.");

        byte[] encryptedValue = Base64.decodeBase64(base64EncryptedValue);
        return new String(decrypt(encryptedValue), UTF_8);
    }

    private byte[] decrypt(byte[] encryptedValue) {

        try {

            int ivSize = builder.keySize;
            int ivByteSize = ivSize / BITS_PER_BYTE;

            byte[] iv = new byte[ivByteSize];
            System.arraycopy(encryptedValue, 0, iv, 0, ivByteSize);

            byte[] rawEncryptedValue = new byte[encryptedValue.length - ivByteSize];

            int encryptedSize = encryptedValue.length - ivByteSize;
            System.arraycopy(encryptedValue, ivByteSize, rawEncryptedValue, 0, encryptedSize);

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] plainTxtBytes = cipher.doFinal(rawEncryptedValue);

            return plainTxtBytes;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class Builder {

        private char[] password;

        private byte[] base64Salt;

        private int keySize;

        private int iterations;

        public Builder setPassword(char[] password) {
            this.password = password;
            return this;
        }

        public Builder setBase64Salt(byte[] base64Salt) {
            this.base64Salt = base64Salt;
            return this;
        }

        public Builder setKeySize(int keySize) {
            this.keySize = keySize;
            return this;
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public EncryptionService build() {
            return new ApiKeySecretEncryptionService(this);
        }
    }
}
