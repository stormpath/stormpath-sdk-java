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
import com.stormpath.sdk.saml.CreateSamlServiceProviderRegistrationRequest;
import com.stormpath.sdk.saml.CreateSamlServiceProviderRegistrationRequestBuilder;
import com.stormpath.sdk.saml.SamlServiceProviderRegistration;
import com.stormpath.sdk.saml.SamlServiceProviderRegistrationOptions;

/**
 * @since 1.3.0
 */
public class DefaultCreateSamlServiceProviderRegistrationRequestBuilder implements CreateSamlServiceProviderRegistrationRequestBuilder {

    private final SamlServiceProviderRegistration samlServiceProviderRegistration;
    private SamlServiceProviderRegistrationOptions options;

    public DefaultCreateSamlServiceProviderRegistrationRequestBuilder(SamlServiceProviderRegistration samlServiceProviderRegistration) {
        Assert.notNull(samlServiceProviderRegistration, "SamlServiceProviderRegistration can't be null.");
        this.samlServiceProviderRegistration = samlServiceProviderRegistration;
    }

    @Override
    public CreateSamlServiceProviderRegistrationRequestBuilder withResponseOptions(SamlServiceProviderRegistrationOptions options) throws IllegalArgumentException {
        Assert.notNull(options, "options can't be null.");
        this.options = options;
        return this;
    }

    @Override
    public CreateSamlServiceProviderRegistrationRequest build() {
        return new DefaultCreateSamlServiceProviderRegistrationRequest(samlServiceProviderRegistration, options);
    }
}
