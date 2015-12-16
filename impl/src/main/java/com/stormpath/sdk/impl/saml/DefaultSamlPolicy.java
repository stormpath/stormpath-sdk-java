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
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.provider.SamlProvider;
import com.stormpath.sdk.saml.SamlPolicy;

import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlPolicy extends AbstractInstanceResource implements SamlPolicy {

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<SamlProvider> SAML_PROVIDER = new ResourceReference<SamlProvider>("serviceProvider", SamlProvider.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SAML_PROVIDER);

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public DefaultSamlPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public SamlProvider getSamlProvider() {
        return getResourceProperty(SAML_PROVIDER);
    }
}
