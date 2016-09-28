/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderData;

/**
 * @since 1.0.beta
 */
public class DefaultProviderAccountRequest implements ProviderAccountRequest {

    ProviderData providerData;

    public DefaultProviderAccountRequest(ProviderData providerData) {
        Assert.notNull(providerData, "providerData cannot be null.");
        Assert.hasText(providerData.getProviderId(), "providerId within ProviderData instance must be specified.");
        this.providerData = providerData;
    }

    @Override
    public ProviderData getProviderData() {
        return this.providerData;
    }

}