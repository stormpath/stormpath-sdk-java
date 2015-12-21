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
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.AssertionConsumerServicePostEndpoint;
import com.stormpath.sdk.saml.SamlServiceProviderMetadata;

import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlServiceProviderMetadata extends AbstractInstanceResource implements SamlServiceProviderMetadata {

    // SIMPLE PROPERTIES
    static final StringProperty X509_CERTIFICATE_ID = new StringProperty("x509CertificateId");

    static final StringProperty SAML_ENTITY_ID = new StringProperty("entityId");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<AssertionConsumerServicePostEndpoint> ASSERTION_CONSUMER_SERVICE_POST_ENDPOINT = new ResourceReference<AssertionConsumerServicePostEndpoint>("assertionConsumerServicePostEndpoint", AssertionConsumerServicePostEndpoint.class);

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(X509_CERTIFICATE_ID, SAML_ENTITY_ID);

    public DefaultSamlServiceProviderMetadata(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlServiceProviderMetadata(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public String getX509CertificateId() {
        return getString(X509_CERTIFICATE_ID);
    }

    public String getSamlEntityId() {
        return getString(SAML_ENTITY_ID);
    }

    public AssertionConsumerServicePostEndpoint getAssertionConsumerServicePostEndpoint() {
        return getResourceProperty(ASSERTION_CONSUMER_SERVICE_POST_ENDPOINT);
    }
}
