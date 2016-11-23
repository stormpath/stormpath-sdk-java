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
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.saml.CreateRegisteredSamlServiceProviderRequest;
import com.stormpath.sdk.saml.CreateRegisteredSamlServiceProviderRequestBuilder;
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;
import com.stormpath.sdk.saml.RegisteredSamlServiceProviderOptions;

/**
 * @since 1.2.1
 */
public class DefaultCreateRegisteredSamlServiceProviderRequestBuilder implements CreateRegisteredSamlServiceProviderRequestBuilder {

    private final RegisteredSamlServiceProvider registeredSamlServiceProvider;
    private RegisteredSamlServiceProviderOptions options;

    public DefaultCreateRegisteredSamlServiceProviderRequestBuilder(RegisteredSamlServiceProvider registeredSamlServiceProvider) {
        Assert.notNull(registeredSamlServiceProvider, "RegisteredSamlServiceProvider can't be null.");
        this.registeredSamlServiceProvider = registeredSamlServiceProvider;
    }

    @Override
    public CreateRegisteredSamlServiceProviderRequestBuilder withResponseOptions(RegisteredSamlServiceProviderOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateRegisteredSamlServiceProviderRequest build() {
        return new DefaultCreateRegisteredSamlServiceProviderRequest(registeredSamlServiceProvider, options);
    }
}
