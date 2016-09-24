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

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.ProviderData;

import java.util.Date;
import java.util.Map;

/**
 * AbstractProviderData is an abstract representation for Provider-specific resources like {@link DefaultGoogleProviderData} or
 * {@link DefaultFacebookProviderData}.
 *
 * @since 1.0.beta
 */
public abstract class AbstractProviderData extends AbstractResource implements ProviderData {

    // SIMPLE PROPERTIES
    public static final StringProperty PROVIDER_ID = new StringProperty("providerId");
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    public AbstractProviderData(InternalDataStore dataStore) {
        this(dataStore, null);
    }

    public AbstractProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        setProperty(PROVIDER_ID, getConcreteProviderId());
    }

    @Override
    public String getProviderId() {
        return getString(PROVIDER_ID);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    /**
     * Each Provider-specific reification of this class must provide the concrete Stormpath ID it represents
     * @return the concrete Stormpath ID the concrete class represents.
     */
    protected abstract String getConcreteProviderId();

}
