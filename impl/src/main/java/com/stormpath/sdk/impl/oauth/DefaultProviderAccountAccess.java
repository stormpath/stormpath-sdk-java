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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.oauth.ProviderData;

import java.util.Map;

//public class DefaultProviderAccountAccess<T extends ProviderData> extends AbstractResource implements ProviderAccountAccessAsMario<T> {
public class DefaultProviderAccountAccess<T extends ProviderData> extends AbstractResource implements ProviderAccountAccess<T> {

    static final ResourceReference<? extends ProviderData> PROVIDER_DATA = new ResourceReference("providerData", ProviderData.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_DATA);

    public DefaultProviderAccountAccess(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultProviderAccountAccess(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public T getProviderData() {
        return (T) getProperty(PROVIDER_DATA.getName());
    }

    @Override
    public void setProviderData(T providerData) {
        setProperty(PROVIDER_DATA, providerData);
    }


}
