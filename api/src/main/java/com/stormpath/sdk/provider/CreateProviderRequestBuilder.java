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
package com.stormpath.sdk.provider;

/**
 * A Builder to construct {@link CreateProviderRequest}s.
 *
 * @see com.stormpath.sdk.directory.CreateDirectoryRequestBuilder#forProvider(CreateProviderRequest)
 * @since 1.0.beta
 */
public interface CreateProviderRequestBuilder<T extends CreateProviderRequestBuilder> {

    /**
     * Setter for the the App ID of your Provider application (e.g. for "google" it looks similar to "143482128708.apps.googleusercontent.com").
     *
     * @param clientId the App ID for your Provider application.
     * @return the builder instance for method chaining.
     */
    T setClientId(String clientId);

    /**
     * Setter for the the App Secret of your Provider application (e.g. for "google" it looks similar to "U-IdloztzwLn2_2M4QjpulPq").
     *
     * @param clientSecret the App Secret for your Provider application.
     * @return the current builder instance for method chaining.
     */
    T setClientSecret(String clientSecret);

    /**
     * Creates a new {@code CreateProviderRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateProviderRequest} instance based on the current builder state.
     */
    CreateProviderRequest build();
}
