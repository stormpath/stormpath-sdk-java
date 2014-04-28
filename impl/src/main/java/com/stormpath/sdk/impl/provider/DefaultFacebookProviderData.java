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
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.FacebookProviderData;

import java.util.Map;

public class DefaultFacebookProviderData extends AbstractProviderData implements FacebookProviderData {

    // SIMPLE PROPERTIES
    static final StringProperty ACCESS_TOKEN = new StringProperty("accessToken");

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, ACCESS_TOKEN);

    public DefaultFacebookProviderData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultFacebookProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    public FacebookProviderData setAccessToken(String accessToken) {
        setProperty(ACCESS_TOKEN, accessToken);
        return this;
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.FACEBOOK.getNameKey();
    }


}
