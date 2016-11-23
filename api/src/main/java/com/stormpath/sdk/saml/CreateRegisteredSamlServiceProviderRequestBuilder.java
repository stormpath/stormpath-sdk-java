/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.saml;

/**
 * A Builder to construct {@link CreateRegisteredSamlServiceProviderRequest}s.
 *
 * @see com.stormpath.sdk.tenant.Tenant#createRegisterdSamlServiceProvider(RegisteredSamlServiceProvider)
 * @since 1.2.1
 */
public interface CreateRegisteredSamlServiceProviderRequestBuilder {

    /**
     * Ensures that after a RegisteredSamlServiceProvider is created, the creation response is retrieved with the specified registeredSamlServiceProvider's
     * options. This enhances performance by leveraging a single request to retrieve multiple related
     * resources you know you will use.
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code options} is null.
     */
    CreateRegisteredSamlServiceProviderRequestBuilder withResponseOptions(RegisteredSamlServiceProviderOptions options) throws IllegalArgumentException;

    /**
     * Creates a new {@code CreateRegisteredSamlServiceProviderRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateRegisteredSamlServiceProviderRequest} instance based on the current builder state.
     */
    CreateRegisteredSamlServiceProviderRequest build();
}
