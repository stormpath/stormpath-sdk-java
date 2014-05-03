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
package com.stormpath.sdk.api;

/**
 * A Builder to construct {@link CreateApiKeyRequest}s.
 *
 * @see com.stormpath.sdk.account.Account#createApiKey(CreateApiKeyRequest)
 * @since 1.1.beta
 */
public interface CreateApiKeyRequestBuilder {

    /**
     * Ensures that after an ApiKey is created, the creation response is retrieved with the specified api key's
     * options. This enhances performance by leveraging a single request to retrieve multiple related
     * resources you know you will use.
     * <p/>
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code options} is null.
     */
    CreateApiKeyRequestBuilder withResponseOptions(ApiKeyOptions options) throws IllegalArgumentException;

    /**
     * Directive to retrieve the api key's secret encrypted as part of Stormpath's response upon successful
     * api key creation.
     * <p/>
     *
     * <b>
     *     If {@code encryptSecret} is {@code false}, or if this property is not specified, the api key will not be encrypted
     *      upon successful api key creation.
     * </b>
     *  <p/>
     *
     * @param encryptSecret whether or not to retrieve the api key's secret encrypted as part of Stormpath's response upon successful
     *                      api key creation.
     *
     * @return the builder instance for method chaining.
     */
    CreateApiKeyRequestBuilder setEncryptSecret(boolean encryptSecret);

    /**
     * Directive to retrieve the api key's secret encrypted with a specific key size as part of Stormpath's response upon successful
     * api key creation.
     * <p/>
     *
     * @param encryptionKeySize the encryption key size that will be used to encrypt the api key secret upon successful
     *                      api key creation.
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code encryptionKeySize} is not a positive integer.
     */
    CreateApiKeyRequestBuilder setEncryptionKeySize(int encryptionKeySize) throws IllegalArgumentException;

    /**
     * Directive to retrieve the api key's secret encrypted with a specific key iteration number as part of Stormpath's response upon successful
     * api key creation.
     *
     * @param encryptionKeyIterations the encryption key iteration number that will be used to encrypt the api key secret upon successful
     *                      api key creation.
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code encryptionKeyIterations} is not a positive integer.
     */
    CreateApiKeyRequestBuilder setEncryptionKeyIterations(int encryptionKeyIterations) throws IllegalArgumentException;

    /**
     * Directive to retrieve the api key's secret encrypted with a specific key salt as part of Stormpath's response upon successful
     * api key creation.
     *
     * @param encryptionKeySalt the encryption key salt that will be used to encrypt the api key secret upon successful
     *                      api key creation.
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code encryptionKeySalt} is null or empty.
     */
    CreateApiKeyRequestBuilder setEncryptionKeySalt(String encryptionKeySalt) throws IllegalArgumentException;

    /**
     * Creates a new {@code CreateApiKeyRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateApiKeyRequest} instance based on the current builder state.
     */
    CreateApiKeyRequest build();
}
