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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.SamlProvider;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;
import com.stormpath.sdk.saml.SAMLServiceProviderMetadata;
import com.stormpath.sdk.saml.SamlServiceProvider;

import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlProvider extends AbstractProvider implements SamlProvider {

    // SIMPLE PROPERTIES
    static final StringProperty SSO_LOGIN_URL = new StringProperty("ssoLoginUrl");
    static final StringProperty SSO_LOGOUT_URL = new StringProperty("ssoLogoutUrl");
    static final StringProperty ENCODED_X509_SIGNING_CERT = new StringProperty("encodedX509SigningCert");
    static final StringProperty REQUEST_SIGNATURE_ALGORITHM = new StringProperty("requestSignatureAlgorithm");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<AttributeStatementMappingRules> ATTRIBUTE_STATEMENT_MAPPING_RULES = new ResourceReference<AttributeStatementMappingRules>("attributeStatementMappingRules", AttributeStatementMappingRules.class);

    static final ResourceReference<SAMLServiceProviderMetadata> SERVICE_PROVIDER_METADATA  = new ResourceReference<SAMLServiceProviderMetadata>("serviceProviderMetadata", SAMLServiceProviderMetadata.class);

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, SSO_LOGIN_URL, SSO_LOGOUT_URL, ENCODED_X509_SIGNING_CERT, REQUEST_SIGNATURE_ALGORITHM, SERVICE_PROVIDER_METADATA, ATTRIBUTE_STATEMENT_MAPPING_RULES);

    public DefaultSamlProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.SAML.getNameKey();
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public String getSsoLoginUrl() {
        return getString(SSO_LOGIN_URL);
    }

    public String getSsoLogoutUrl() {
        return getString(SSO_LOGOUT_URL);
    }

    public String getEncodedX509SigningCert() {
        return getString(ENCODED_X509_SIGNING_CERT);
    }

    public String getRequestSignatureAlgorithm() {
        return getString(REQUEST_SIGNATURE_ALGORITHM);
    }

    public AttributeStatementMappingRules getAttributeStatementMappingRules() {
        return getResourceProperty(ATTRIBUTE_STATEMENT_MAPPING_RULES);
    }

    public SAMLServiceProviderMetadata getServiceProviderMetadata() {
        return getResourceProperty(SERVICE_PROVIDER_METADATA);
    }
}
