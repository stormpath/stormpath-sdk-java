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
import com.stormpath.sdk.provider.FacebookProvider;

import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultFacebookProvider extends AbstractOAuthProvider<FacebookProvider> implements FacebookProvider {

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, CLIENT_ID, CLIENT_SECRET, SCOPE, USER_INFO_MAPPING_RULES);

    public DefaultFacebookProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultFacebookProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.FACEBOOK.getNameKey();
    }

    /**
     * @since 1.3.0
     */
    @Override
    public String getProviderType() {
        return IdentityProviderType.FACEBOOK.getNameKey();
    }
}
