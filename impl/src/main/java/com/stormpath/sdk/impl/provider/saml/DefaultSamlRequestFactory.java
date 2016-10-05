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
package com.stormpath.sdk.impl.provider.saml;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.provider.saml.SamlRequestFactory;
import com.stormpath.sdk.provider.saml.CreateSamlProviderRequestBuilder;
import com.stormpath.sdk.provider.saml.SamlAccountRequestBuilder;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlRequestFactory implements SamlRequestFactory {

    public CreateSamlProviderRequestBuilder builder() {
        return (CreateSamlProviderRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.saml.DefaultCreateSamlProviderRequestBuilder");
    }

    @Override
    public SamlAccountRequestBuilder account() {
        return (SamlAccountRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.saml.DefaultSamlAccountRequestBuilder");
    }
}
