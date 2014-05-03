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
import com.stormpath.sdk.api.CreateApiKeyRequestBuilder;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.1.beta
 */
public class DefaultPersistApiKeyRequestBuilder implements CreateApiKeyRequestBuilder {

    private ApiKeyOptions options;
    private Boolean encryptSecret;
    private Integer encryptionKeySize;
    private Integer encryptionKeyIterations;
    private String encryptionKeySalt;

    @Override
    public CreateApiKeyRequestBuilder withResponseOptions(ApiKeyOptions options) throws IllegalArgumentException {
        Assert.notNull(options);
        this.options = options;
        return this;
    }

    @Override
    public CreateApiKeyRequestBuilder setEncryptSecret(boolean encryptSecret) {
        this.encryptSecret = encryptSecret;
        return this;
    }

    @Override
    public CreateApiKeyRequestBuilder setEncryptionKeySize(int encryptionKeySize) throws IllegalArgumentException {
        this.encryptionKeySize = encryptionKeySize;
        return this;
    }

    @Override
    public CreateApiKeyRequestBuilder setEncryptionKeyIterations(int encryptionKeyIterations) throws IllegalArgumentException {
        this.encryptionKeyIterations = encryptionKeyIterations;
        return this;
    }

    @Override
    public CreateApiKeyRequestBuilder setEncryptionKeySalt(String encryptionKeySalt) throws IllegalArgumentException {
        this.encryptionKeySalt = encryptionKeySalt;
        return this;
    }

    @Override
    public CreateApiKeyRequest build() {
        return new DefaultPersistApiKeyRequest(options, encryptSecret, encryptionKeySize, encryptionKeyIterations, encryptionKeySalt);
    }
}
