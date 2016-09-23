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
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract class that each Provider-specific {@link ProviderAccountRequestBuilder} can extend to facilitate its work.
 *
 * @since 1.0.beta
 */
public abstract class AbstractProviderAccountRequestBuilder<T extends ProviderAccountRequestBuilder<T>> implements ProviderAccountRequestBuilder<T> {

    protected String accessToken;

    @Override
    public T setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return (T) this;
    }

    @Override
    public ProviderAccountRequest build() {
        final String providerId = getConcreteProviderId();
        Assert.state(Strings.hasText(providerId), "The providerId property is missing.");

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("providerId", providerId);

        return doBuild(Collections.unmodifiableMap(properties));
    }

    /**
     * Abstract method to force subclass not to forget to provide the Stormpath ID for the provider.
     *
     * @return the Stormpath ID for the specific Provider the subclass represents.
     */
    protected abstract String getConcreteProviderId();

    /**
     * Hook method to give Provider-specific subclasses the responsibility to construct the {@link ProviderAccountRequest} with
     * their own properties.
     *
     * @param map Provider-wide properties that each Provider will need in order to construct the {@link ProviderAccountRequest}
     * @return the actual request each Provider-specific Builder constructs based on the set properties.
     */
    protected abstract ProviderAccountRequest doBuild(Map<String, Object> map);

}
