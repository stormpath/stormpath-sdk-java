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
import com.stormpath.sdk.provider.Provider;

import java.util.Date;
import java.util.Map;

public abstract class AbstractProvider extends AbstractResource implements Provider {

    // SIMPLE PROPERTIES
    static final StringProperty PROVIDER_ID = new StringProperty("providerId");
    static final DateProperty CREATED_AT = new DateProperty("createdAt");
    static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    public AbstractProvider(InternalDataStore dataStore) {
        this(dataStore, null);
    }

    public AbstractProvider(InternalDataStore dataStore, Map<String, Object> properties) {
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

    protected abstract String getConcreteProviderId();

}
