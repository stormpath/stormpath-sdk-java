/*
* Copyright 2016 Stormpath, Inc.
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
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.saml.*;

import java.util.Date;
import java.util.Map;

//todo: saml javadoc
public class DefaultSamlIdentityProvider extends AbstractInstanceResource implements SamlIdentityProvider {

    static final EnumProperty<SamlIdentityProviderStatus> STATUS = new EnumProperty<>("status", SamlIdentityProviderStatus.class);
    static final MapProperty SSO_LOGIN_ENDPOINT = new MapProperty("ssoLoginEndpoint");
    static final StringProperty SIGNATURE_ALGORITHM = new StringProperty("signatureAlgorithm");
    static final MapProperty X509_SIGNING_CERT = new MapProperty("x509SigningCert");
    static final ResourceReference<SamlIdentityProviderMetadata> SAML_IDENTITY_PROVIDER_METADATA = new ResourceReference<>("metadata", SamlIdentityProviderMetadata.class);
    static final ResourceReference<AttributeStatementMappingRules> ATTRIBUTE_STATEMENT_MAPPING_RULES = new ResourceReference("attributeStatementMappingRules", AttributeStatementMappingRules.class);
    static final CollectionReference<RegisteredSamlServiceProviderList, RegisteredSamlServiceProvider> REGISTERED_SAML_SERVICE_PROVIDERS = new CollectionReference<>("registeredSamlServiceProviders", RegisteredSamlServiceProviderList.class, RegisteredSamlServiceProvider.class);
    //todo: saml uncomment this once registrations created
    //static final CollectionReference<SamlServiceProviderRegistrationList, SamlServiceProviderRegistration> SAML_SERVICE_PROVIDER_REGISTRATIONS = new CollectionReference<>("samlServiceProviderRegistrations", SamlServiceProviderRegistrationList.class, SamlServiceProviderRegistration.class);

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(STATUS, SSO_LOGIN_ENDPOINT, SIGNATURE_ALGORITHM, X509_SIGNING_CERT, SAML_IDENTITY_PROVIDER_METADATA, ATTRIBUTE_STATEMENT_MAPPING_RULES, REGISTERED_SAML_SERVICE_PROVIDERS, /*SAML_SERVICE_PROVIDER_REGISTRATIONS,*/ CREATED_AT, MODIFIED_AT);

    public DefaultSamlIdentityProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlIdentityProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public SamlIdentityProviderStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return SamlIdentityProviderStatus.valueOf(value.toUpperCase());
    }

    @Override
    public SamlIdentityProvider setStatus(SamlIdentityProviderStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public Map<String, String> getSsoLoginEndpoint() {
        return getMap(SSO_LOGIN_ENDPOINT);
    }

    @Override
    public SamlIdentityProvider setSsoLoginEndpoint(String ssoLoginEndpoint) {
        setProperty(SSO_LOGIN_ENDPOINT, ssoLoginEndpoint);
        return this;
    }

    @Override
    public String getSignatureAlgorithm() {
        return getString(SIGNATURE_ALGORITHM);
    }

    @Override
    public SamlIdentityProvider setSignatureAlgorithm(String signatureAlgorithm) {
        setProperty(SIGNATURE_ALGORITHM, signatureAlgorithm);
        return this;
    }

    @Override
    public Map<String, String> getX509SigninCert() {
        return getMap(X509_SIGNING_CERT);
    }

    @Override
    public SamlIdentityProvider setX509SigninCert(Map<String, String> x509SigninCert) {
        setProperty(X509_SIGNING_CERT, x509SigninCert);
        return this;
    }

    @Override
    public SamlIdentityProviderMetadata getSamlIdentityProviderMetadata() {
        return getResourceProperty(SAML_IDENTITY_PROVIDER_METADATA);
    }

    @Override
    public AttributeStatementMappingRules getAttributeStatementMappingRules() {
        return getResourceProperty(ATTRIBUTE_STATEMENT_MAPPING_RULES);
    }

    @Override
    public SamlIdentityProvider setAttributeStatementMappingRules(AttributeStatementMappingRules attributeStatementMappingRules) {
        setProperty(ATTRIBUTE_STATEMENT_MAPPING_RULES, attributeStatementMappingRules);
        return this;
    }

    @Override
    public RegisteredSamlServiceProviderList getRegisteredSamlServiceProviders() {
        return getResourceProperty(REGISTERED_SAML_SERVICE_PROVIDERS);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
