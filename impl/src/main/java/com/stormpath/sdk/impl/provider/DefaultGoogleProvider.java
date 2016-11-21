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
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.GoogleProvider;
import com.stormpath.sdk.provider.GoogleProviderAccessType;
import com.stormpath.sdk.provider.GoogleProviderDisplay;

import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultGoogleProvider extends AbstractOAuthProvider<GoogleProvider> implements GoogleProvider {

    // SIMPLE PROPERTIES
    // REDIRECT_URI is here for backwards compatibility, but the recommended way to specify the redirectUri is
    // to specify it as part of the com.stormpath.sdk.provider.ProviderAccountRequest
    private static final StringProperty REDIRECT_URI = new StringProperty("redirectUri");
    private static final StringProperty HD = new StringProperty("hd");
    private static final EnumProperty<GoogleProviderAccessType> ACCESS_TYPE = new EnumProperty<>("accessType", GoogleProviderAccessType.class);
    private static final EnumProperty<GoogleProviderDisplay> DISPLAY = new EnumProperty<>("display", GoogleProviderDisplay.class);

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID , CREATED_AT, MODIFIED_AT, CLIENT_ID, CLIENT_SECRET, SCOPE, REDIRECT_URI, HD, DISPLAY, ACCESS_TYPE);

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

    @Override
    public String getHd() {
        return getString(HD);
    }

    @Override
    public GoogleProviderDisplay getDisplay() {
        String value = getStringProperty(DISPLAY.getName());
        if (value == null) {
            return null;
        }
        return GoogleProviderDisplay.valueOf(value.toUpperCase());
    }

    @Override
    public GoogleProviderAccessType getAccessType() {
        String value = getStringProperty(ACCESS_TYPE.getName());
        if (value == null) {
            return null;
        }
        return GoogleProviderAccessType.valueOf(value.toUpperCase());
    }
}
