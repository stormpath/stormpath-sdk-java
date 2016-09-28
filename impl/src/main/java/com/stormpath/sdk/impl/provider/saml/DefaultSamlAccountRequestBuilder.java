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

import com.stormpath.sdk.impl.provider.AbstractProviderAccountRequestBuilder;
import com.stormpath.sdk.impl.provider.DefaultProviderAccountRequest;
import com.stormpath.sdk.impl.provider.IdentityProviderType;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.saml.SamlAccountRequestBuilder;

import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlAccountRequestBuilder extends AbstractProviderAccountRequestBuilder<SamlAccountRequestBuilder> implements SamlAccountRequestBuilder {

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.SAML.getNameKey();
    }

    @Override
    protected ProviderAccountRequest doBuild(Map<String, Object> map) {
        DefaultSamlProviderData providerData = new DefaultSamlProviderData(null, map);
        return new DefaultProviderAccountRequest(providerData);
    }

    @Override
    public SamlAccountRequestBuilder setAccessToken(String accessToken) {
        throw new UnsupportedOperationException("This method is not supported in SamlAccountRequestBuilder class.");
    }
}
