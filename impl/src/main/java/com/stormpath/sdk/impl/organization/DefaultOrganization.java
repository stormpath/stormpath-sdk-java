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
package com.stormpath.sdk.impl.organization;

import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationStatus;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.0.RC4.6
 */
public class DefaultOrganization extends AbstractExtendableInstanceResource implements Organization {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StatusProperty<OrganizationStatus> STATUS = new StatusProperty<OrganizationStatus>(OrganizationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, TENANT, CUSTOM_DATA);

    public DefaultOrganization(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOrganization(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public Organization setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public OrganizationStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return OrganizationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Organization setStatus(OrganizationStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Organization setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

}
