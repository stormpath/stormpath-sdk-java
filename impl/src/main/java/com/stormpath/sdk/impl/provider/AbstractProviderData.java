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

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * AbstractProviderData is an abstract representation for Provider-specific resources like {@link DefaultGoogleProviderData} or
 * {@link DefaultFacebookProviderData}.
 *
 * @since 1.0.beta
 */
public abstract class AbstractProviderData<T extends ProviderData> extends AbstractResource implements ProviderData {

    // SIMPLE PROPERTIES
    public static final StringProperty PROVIDER_ID = new StringProperty("providerId");
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");
    static final StringProperty ACCESS_TOKEN = new StringProperty("accessToken");
    static final StringProperty CODE = new StringProperty("code");

    public AbstractProviderData(InternalDataStore dataStore) {
        //noinspection unchecked
        this(dataStore, Collections.EMPTY_MAP);
    }

    public AbstractProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        setProperty(PROVIDER_ID, getConcreteProviderId());
        if (properties.containsKey(ACCESS_TOKEN.getName())) {
            setProperty(ACCESS_TOKEN, properties.get(ACCESS_TOKEN.getName()));
        }
        if (properties.containsKey(CODE.getName())) {
            setProperty(CODE, properties.get(CODE.getName()));
        }
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

    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    @SuppressWarnings("unchecked")
    public T setAccessToken(String accessToken) {
        setProperty(ACCESS_TOKEN, accessToken);
        return (T) this;
    }

    public String getCode() {
        return getString(CODE);
    }

    @SuppressWarnings("unchecked")
    public T setCode(String code) {
        setProperty(CODE, code);
        return (T) this;
    }


    /**
     * Each Provider-specific reification of this class must provide the concrete Stormpath ID it represents
     * @return the concrete Stormpath ID the concrete class represents.
     */
    protected abstract String getConcreteProviderId();

}
