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
 * A Builder to construct {@link CreateSamlServiceProviderRegistrationRequest}s.
 *
 * @see com.stormpath.sdk.saml.SamlIdentityProvider#createSamlServiceProviderRegistration(CreateSamlServiceProviderRegistrationRequest)
 * @since 1.2.1
 */
public interface CreateSamlServiceProviderRegistrationRequestBuilder {

    /**
     * Ensures that after a SamlServiceProviderRegistration is created, the creation response is retrieved with the specified samlServiceProviderRegistration's
     * options. This enhances performance by leveraging a single request to retrieve multiple related
     * resources you know you will use.
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code options} is null.
     */
    CreateSamlServiceProviderRegistrationRequestBuilder withResponseOptions(SamlServiceProviderRegistrationOptions options) throws IllegalArgumentException;

    /**
     * Creates a new {@code CreateSamlServiceProviderRegistrationRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateSamlServiceProviderRegistrationRequest} instance based on the current builder state.
     */
    CreateSamlServiceProviderRegistrationRequest build();
}
