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
import com.stormpath.sdk.saml.SamlIdentityProvider;
import com.stormpath.sdk.saml.SamlIdentityProviderMetadata;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.2.1
 */
public class DefaultSamlIdentityProviderMetadata extends AbstractInstanceResource implements SamlIdentityProviderMetadata {

    static final StringProperty SAML_ENTITY_ID = new StringProperty("entityId");
    static final MapProperty X509_SIGNING_CERT = new MapProperty("x509SigningCert");
    static final ResourceReference<SamlIdentityProvider> SAML_IDENTITY_PROVIDER = new ResourceReference<>("identityProvider", SamlIdentityProvider.class);

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SAML_ENTITY_ID, X509_SIGNING_CERT, SAML_IDENTITY_PROVIDER, CREATED_AT, MODIFIED_AT);

    public DefaultSamlIdentityProviderMetadata(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlIdentityProviderMetadata(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors(){
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getEntityId() {
        return getString(SAML_ENTITY_ID);
    }

    @Override
    public Map<String, String> getX509SigninCert() {
        return getMap(X509_SIGNING_CERT);
    }

    @Override
    public SamlIdentityProvider getSamlIdentityProvider() {
        return getResourceProperty(SAML_IDENTITY_PROVIDER);
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
