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
import com.stormpath.sdk.saml.SamlIdentityProvider;
import com.stormpath.sdk.saml.SamlServiceProviderRegistration;
import com.stormpath.sdk.saml.SamlServiceProviderRegistrationStatus;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultSamlServiceProviderRegistration extends AbstractInstanceResource implements SamlServiceProviderRegistration {

    static final EnumProperty<SamlServiceProviderRegistrationStatus> STATUS = new EnumProperty<>("status", SamlServiceProviderRegistrationStatus.class);
    static final ResourceReference<RegisteredSamlServiceProvider> SERVICE_PROVIDER = new ResourceReference<>("serviceProvider", RegisteredSamlServiceProvider.class);
    static final ResourceReference<SamlIdentityProvider> IDENTITY_PROVIDER = new ResourceReference<>("identityProvider", SamlIdentityProvider.class);
    static final StringProperty DEFAULT_RELAY_STATE = new StringProperty("defaultRelayState");

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(STATUS, SERVICE_PROVIDER, IDENTITY_PROVIDER, DEFAULT_RELAY_STATE, CREATED_AT, MODIFIED_AT);

    public DefaultSamlServiceProviderRegistration(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlServiceProviderRegistration(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public SamlServiceProviderRegistrationStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return SamlServiceProviderRegistrationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public SamlServiceProviderRegistration setStatus(SamlServiceProviderRegistrationStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public RegisteredSamlServiceProvider getServiceProvider() {
        return getResourceProperty(SERVICE_PROVIDER);
    }

    @Override
    public SamlServiceProviderRegistration setServiceProvider(RegisteredSamlServiceProvider registeredSamlServiceProvider) {
        setResourceProperty(SERVICE_PROVIDER, registeredSamlServiceProvider);
        return this;
    }

    @Override
    public SamlIdentityProvider getIdentityProvider() {
        return getResourceProperty(IDENTITY_PROVIDER);
    }

    @Override
    public SamlServiceProviderRegistration setIdentityProvider(SamlIdentityProvider samlIdentityProvider) {
        setResourceProperty(IDENTITY_PROVIDER, samlIdentityProvider);
        return this;
    }

    @Override
    public String getDefaultRelayState() {
        return getString(DEFAULT_RELAY_STATE);
    }

    @Override
    public SamlServiceProviderRegistration setDefaultRelayState(String defaultRelayState) {
        setProperty(DEFAULT_RELAY_STATE, defaultRelayState);
        return this;
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
