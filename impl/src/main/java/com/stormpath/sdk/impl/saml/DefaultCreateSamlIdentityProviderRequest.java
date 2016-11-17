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
import com.stormpath.sdk.saml.CreateSamlIdentityProviderRequest;
import com.stormpath.sdk.saml.SamlIdentityProvider;
import com.stormpath.sdk.saml.SamlIdentityProviderOptions;

/**
 * @since 1.2.0
 */
public class DefaultCreateSamlIdentityProviderRequest implements CreateSamlIdentityProviderRequest {

    private final SamlIdentityProvider samlIdentityProvider;
    private final SamlIdentityProviderOptions options;

    public DefaultCreateSamlIdentityProviderRequest(SamlIdentityProvider samlIdentityProvider, SamlIdentityProviderOptions options) {
        Assert.notNull(samlIdentityProvider, "samlIdentityProvider cannot be null.");
        this.samlIdentityProvider = samlIdentityProvider;
        this.options = options;
    }

    @Override
    public SamlIdentityProvider getSamlIdentityProvider() {
        return this.samlIdentityProvider;
    }

    @Override
    public boolean hasSamlIdentityProviderOptions() {
        return this.options != null;
    }

    @Override
    public SamlIdentityProviderOptions getSamlIdentityProviderOptions() throws IllegalStateException {
        return options;
    }

}
