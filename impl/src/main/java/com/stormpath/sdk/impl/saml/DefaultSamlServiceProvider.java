/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.SamlServiceProvider;

import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlServiceProvider extends AbstractInstanceResource implements SamlServiceProvider {

    // SIMPLE PROPERTIES
    static final StringProperty SSO_INITIALIZATION_ENDPOINT = new StringProperty("ssoInitiationEndpoint");

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SSO_INITIALIZATION_ENDPOINT);

    public DefaultSamlServiceProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultSamlServiceProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getSsoInitiationEndpoint() {
        return getString(SSO_INITIALIZATION_ENDPOINT);
    }
}
