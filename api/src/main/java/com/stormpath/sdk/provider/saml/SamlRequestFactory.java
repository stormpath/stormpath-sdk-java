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
package com.stormpath.sdk.provider.saml;

/**
 * Saml-specific {@link com.stormpath.sdk.provider.ProviderRequestFactory} interface.
 *
 * @since 1.0.RC8
 */
public interface SamlRequestFactory  {

    /**
     * Creates a new {@link CreateSamlProviderRequestBuilder} that allows you to build Saml-specific {@link com.stormpath.sdk.provider.CreateProviderRequest}s.
     *
     * @return a new {@link CreateSamlProviderRequestBuilder} to build Saml-specific {@link com.stormpath.sdk.provider.CreateProviderRequest}s.
     */
    CreateSamlProviderRequestBuilder builder();

    /**
     * Returns a builder to generate an attempt to create or retrieve a Provider {@link com.stormpath.sdk.account.Account}
     * from Stormpath.
     *
     * @return a builder to generate an attempt to create or retrieve a Provider {@link com.stormpath.sdk.account.Account}
     * from Stormpath.
     */
    SamlAccountRequestBuilder account();
}
