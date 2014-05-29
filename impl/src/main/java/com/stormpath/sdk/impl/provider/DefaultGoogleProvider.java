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
import com.stormpath.sdk.provider.GoogleProvider;

import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultGoogleProvider extends AbstractProvider implements GoogleProvider {

    // SIMPLE PROPERTIES
    static final StringProperty CLIENT_ID = new StringProperty("clientId");
    static final StringProperty CLIENT_SECRET = new StringProperty("clientSecret");
    static final StringProperty REDIRECT_URI = new StringProperty("redirectUri");

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

    public DefaultGoogleProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGoogleProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getClientId() {
        return getString(CLIENT_ID);
    }

    public GoogleProvider setClientId(String clientId) {
        setProperty(CLIENT_ID, clientId);
        return this;
    }

    @Override
    public String getClientSecret() {
        return getString(CLIENT_SECRET);
    }

    public GoogleProvider setClientSecret(String clientSecret) {
        setProperty(CLIENT_SECRET, clientSecret);
        return this;
    }

    @Override
    public String getRedirectUri() {
        return getString(REDIRECT_URI);
    }

    public GoogleProvider setRedirectUri(String redirectUri) {
        setProperty(REDIRECT_URI, redirectUri);
        return this;
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.GOOGLE.getNameKey();
    }

}
