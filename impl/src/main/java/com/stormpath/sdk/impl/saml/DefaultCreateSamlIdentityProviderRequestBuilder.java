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
import com.stormpath.sdk.saml.*;

/**
 * @since 1.2.0
 */
public class DefaultCreateSamlIdentityProviderRequestBuilder implements CreateSamlIdentityProviderRequestBuilder {

    private final SamlIdentityProvider samlIdentityProvider;
    private SamlIdentityProviderOptions options;

    public DefaultCreateSamlIdentityProviderRequestBuilder(SamlIdentityProvider samlIdentityProvider) {
        Assert.notNull(samlIdentityProvider, "SamlIdentityProvider can't be null.");
        this.samlIdentityProvider = samlIdentityProvider;
    }

    @Override
    public CreateSamlIdentityProviderRequestBuilder withResponseOptions(SamlIdentityProviderOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateSamlIdentityProviderRequest build() {
        return new DefaultCreateSamlIdentityProviderRequest(samlIdentityProvider, options);
    }
}
