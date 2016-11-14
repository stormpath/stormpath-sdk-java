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
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultRegisteredSamlServiceProvider extends AbstractInstanceResource implements RegisteredSamlServiceProvider {

    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StringProperty ASSERTION_CONSUMER_SERVICE_URL = new StringProperty("assertionConsumerServiceURL");
    static final StringProperty NAME_ID_FORMAT = new StringProperty("nameIdFormat");
    static final StringProperty ENTITY_ID = new StringProperty("entityId");
    static final StringProperty ENCODED_X509_CERTIFICATE = new StringProperty("encodedX509Certificate");

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(NAME, DESCRIPTION, ASSERTION_CONSUMER_SERVICE_URL, NAME_ID_FORMAT, ENTITY_ID, ENCODED_X509_CERTIFICATE, CREATED_AT, MODIFIED_AT, TENANT);

    public DefaultRegisteredSamlServiceProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultRegisteredSamlServiceProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public RegisteredSamlServiceProvider setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public RegisteredSamlServiceProvider setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public String getAssertionConsumerServiceURL() {
        return getString(ASSERTION_CONSUMER_SERVICE_URL);
    }

    @Override
    public RegisteredSamlServiceProvider setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
        setProperty(ASSERTION_CONSUMER_SERVICE_URL, assertionConsumerServiceURL);
        return this;
    }

    @Override
    public String getEntityId() {
        return getString(ENTITY_ID);
    }

    @Override
    public RegisteredSamlServiceProvider setEntityId(String entityId) {
        setProperty(ENTITY_ID, entityId);
        return this;
    }

    @Override
    public String getNameIdFormat() {
        return getString(NAME_ID_FORMAT);
    }

    @Override
    public RegisteredSamlServiceProvider setNameIdFormat(String nameIdFormat) {
        setProperty(NAME_ID_FORMAT, nameIdFormat);
        return this;
    }

    @Override
    public String getEncodedX509SigningCert() {
        return getString(ENCODED_X509_CERTIFICATE);
    }

    @Override
    public RegisteredSamlServiceProvider setEncodedX509SigningCert(String x509SigningCert) {
        setProperty(ENCODED_X509_CERTIFICATE, x509SigningCert);
        return this;
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
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
