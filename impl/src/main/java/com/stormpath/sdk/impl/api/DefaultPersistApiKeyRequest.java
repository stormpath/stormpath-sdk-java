/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.api.CreateApiKeyRequest;

/**
 * @since 1.1.beta
 */
public class DefaultPersistApiKeyRequest implements CreateApiKeyRequest {

    private final ApiKeyOptions options;
    private final Boolean encryptSecret;
    private final Integer encryptionKeySize;
    private final Integer encryptionKeyIterations;
    private final String encryptionKeySalt;

    public DefaultPersistApiKeyRequest(ApiKeyOptions options, Boolean encryptSecret, Integer encryptionKeySize, Integer encryptionKeyIterations, String encryptionKeySalt) {
        this.options = options;
        this.encryptSecret = encryptSecret;
        this.encryptionKeySize = encryptionKeySize;
        this.encryptionKeyIterations = encryptionKeyIterations;
        this.encryptionKeySalt = encryptionKeySalt;
    }

    @Override
    public boolean isApiKeyOptionsSpecified() {
        return this.options != null;

    }

    @Override
    public ApiKeyOptions getApiKeyOptions() {
        if (this.options == null) {
            throw new IllegalStateException("apiKeyOptions has not been configured. Use the isApiKeyOptionsSpecified method to check first before invoking this method.");
        }
        return this.options;
    }

    @Override
    public boolean isEncryptSecretOptionSpecified() {
        return this.encryptSecret != null;
    }

    @Override
    public boolean isEncryptSecret() throws IllegalStateException {
        if (this.encryptSecret == null) {
            throw new IllegalStateException("encryptSecret has not been configured. Use the isEncryptSecretOptionSpecified method to check first before invoking this method.");
        }
        return this.encryptSecret;
    }

    @Override
    public boolean isEncryptionKeySizeOptionSpecified() {
        return this.encryptionKeySize != null;
    }

    @Override
    public int getEncryptionKeySize() throws IllegalStateException {
        if (this.encryptionKeySize == null) {
            throw new IllegalStateException("encryptionKeySize has not been configured. Use the isEncryptionKeySizeOptionSpecified method to check first before invoking this method.");
        }
        return this.encryptionKeySize;
    }

    @Override
    public boolean isEncryptionKeyIterationsOptionSpecified() {
        return this.encryptionKeyIterations != null;
    }

    @Override
    public int getEncryptionKeyIterations() throws IllegalStateException {
        if (this.encryptionKeyIterations == null) {
            throw new IllegalStateException("encryptionKeyIterations has not been configured. Use the isEncryptionKeyIterationsOptionSpecified method to check first before invoking this method.");
        }
        return this.encryptionKeyIterations;
    }

    @Override
    public boolean isEncryptionKeySaltOptionSpecified() {
        return this.encryptionKeySalt != null;
    }

    @Override
    public String getEncryptionKeySalt() throws IllegalStateException {
        if (this.encryptionKeySalt == null) {
            throw new IllegalStateException("encryptionKeySalt has not been configured. Use the isEncryptionKeySaltOptionSpecified method to check first before invoking this method.");
        }
        return this.encryptionKeySalt;
    }
}
