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

import com.stormpath.sdk.account.Account;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.api.ApiKey} record in Stormpath.
 *
 * @see Account#createApiKey(CreateApiKeyRequest)
 * @since 1.1.beta
 */
public interface CreateApiKeyRequest {

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's references as part of Stormpath's response upon successful
     * api key creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getApiKeyOptions()} method.
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the expanded references in the Stormpath's response upon successful
     *         api key creation.
     */
    boolean isApiKeyOptionsSpecified();

    /**
     * Returns the {@code AccountOptions} to be used in the CreateApiKeyRequest s to retrieve the api key's
     * references as part of Stormpath's response upon successful api key creation.
     * <p/>
     * Always call the {@link #isApiKeyOptionsSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return {@link ApiKeyOptions} to be used in the CreateApiKeyRequest s to retrieve the api key's
     *         references as part of Stormpath's response upon successful api key creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isApiKeyOptionsSpecified()} is {@code false}.
     */
    ApiKeyOptions getApiKeyOptions() throws IllegalStateException;;

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's secret encrypted as part of Stormpath's response upon successful
     * api key creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #isEncryptSecret()} method.
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the api key's secret encrypted in the Stormpath's response upon successful
     *         api key creation.
     */
    boolean isEncryptSecretOptionSpecified();

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's secret encrypted as part of Stormpath's response upon successful
     * api key creation.
     * <p/>
     * Always call the {@link #isEncryptSecretOptionSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the api key's secret encrypted in the Stormpath's response upon successful
     *         api key creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isEncryptSecretOptionSpecified()} is {@code false}.
     */
    boolean isEncryptSecret() throws IllegalStateException;;

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's secret encrypted with a specific key size as part of
     * Stormpath's response upon successful api key creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getEncryptionKeySize()} method.
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the api key's secret encrypted with a specific key size in the
     *         Stormpath's response upon successful api key creation.
     */
    boolean isEncryptionKeySizeOptionSpecified();

    /**
     * Returns the {@code encryption key size} to be used in the CreateApiKeyRequest to retrieve the api key's secret
     * encrypted with a specific key size as part of Stormpath's response upon successful api key creation.
     * <p/>
     * Always call the {@link #isEncryptionKeySizeOptionSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return the {@code encryption key size} to be used in the CreateApiKeyRequest to retrieve
     *         the api key's secret encrypted with a specific key size in the Stormpath's response upon
     *         successful api key creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isEncryptionKeySizeOptionSpecified()} is {@code false}.
     */
    int getEncryptionKeySize() throws IllegalStateException;;

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's secret encrypted with a specific key iteration number as part of
     * Stormpath's response upon successful api key creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getEncryptionKeyIterations()} method.
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the api key's secret encrypted with a specific key iteration number in the
     *         Stormpath's response upon successful api key creation.
     */
    boolean isEncryptionKeyIterationsOptionSpecified();

    /**
     * Returns the {@code encryption key iteration number} to be used in the CreateApiKeyRequest to retrieve the api key's secret
     * encrypted with a specific key iteration number as part of Stormpath's response upon successful api key creation.
     * <p/>
     * Always call the {@link #isEncryptionKeyIterationsOptionSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return the {@code encryption key iteration number} to be used in the CreateApiKeyRequest to retrieve
     *         the api key's secret encrypted with a specific key iteration number in the Stormpath's response upon
     *         successful api key creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isEncryptionKeyIterationsOptionSpecified()} is {@code false}.
     */
    int getEncryptionKeyIterations() throws IllegalStateException;;

    /**
     * Returns {@code true} if the the request reflects that the CreateApiKey (POST) message will be sent with
     * URL query parameters to retrieve the api key's secret encrypted with a specific key salt as part of
     * Stormpath's response upon successful api key creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getEncryptionKeySalt()} method.
     *
     * @return {@code true} if the request reflects that the CreateApiKey (POST) message will be sent with
     *         URL query parameters to retrieve the api key's secret encrypted with a specific key salt in the
     *         Stormpath's response upon successful api key creation.
     */
    boolean isEncryptionKeySaltOptionSpecified();

    /**
     * Returns the {@code encryption key salt} to be used in the CreateApiKeyRequest to retrieve the api key's secret
     * encrypted with a specific key salt as part of Stormpath's response upon successful api key creation.
     * <p/>
     * Always call the {@link #isEncryptionKeySaltOptionSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return the {@code encryption key salt} to be used in the CreateApiKeyRequest to retrieve
     *         the api key's secret encrypted with a specific key salt in the Stormpath's response upon
     *         successful api key creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isEncryptionKeySaltOptionSpecified()} is {@code false}.
     */
    String getEncryptionKeySalt() throws IllegalStateException;;

}
