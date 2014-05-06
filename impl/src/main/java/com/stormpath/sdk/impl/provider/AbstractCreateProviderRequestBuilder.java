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
import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.CreateProviderRequestBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract class that each Provider-specific {@link CreateProviderRequestBuilder} can extend to facilitate its work.
 *
 * @since 1.0.beta
 */
abstract class AbstractCreateProviderRequestBuilder<T extends CreateProviderRequestBuilder<T>> implements CreateProviderRequestBuilder<T> {

    protected String clientId;
    protected String clientSecret;

    @Override
    public T setClientId(String clientId) {
        this.clientId = clientId;
        return (T) this;
    }

    @Override
    public T setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return (T) this;
    }

    public CreateProviderRequest build() {
        Assert.state(Strings.hasText(this.clientId), "clientId is a required property. It must be provided before building.");
        Assert.state(Strings.hasText(this.clientSecret), "clientSecret is a required property. It must be provided before building.");

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
     * Hook method to give Provider-specific subclasses the responsibility to construct the {@link CreateProviderRequest} with
     * their own properties.
     *
     * @param map Provider-wide properties that each Provider will need in order to construct the {@link CreateProviderRequest}
     * @return the actual request each Provider-specific Builder constructs based on the set properties.
     */
    protected abstract CreateProviderRequest doBuild(Map<String, Object> map);

}
