/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.lang.Assert;

import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultProviderModelFactoryResolver implements ProviderModelFactoryResolver {

    private static final ProviderModelFactory DISABLED_MODEL_FACTORY = new DisabledProviderModelFactory();

    private final Map<String,ProviderModelFactory> providerModelFactoryMap;

    public DefaultProviderModelFactoryResolver(Map<String, ProviderModelFactory> providerModelFactoryMap) {
        Assert.notNull(providerModelFactoryMap, "providerModelFactoryMap cannot be null.");
        this.providerModelFactoryMap = providerModelFactoryMap;
    }

    @Override
    public ProviderModelFactory getProviderModelFactory(ProviderModelContext context) {

        String providerId = context.getProvider().getProviderId();

        ProviderModelFactory factory = providerModelFactoryMap.get(providerId);

        return factory != null ? factory : DISABLED_MODEL_FACTORY;
    }
}
