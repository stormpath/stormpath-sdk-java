/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.WebConfiguration;
import com.stormpath.sdk.application.WebConfigurationStatus;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultWebConfiguration extends AbstractInstanceResource implements WebConfiguration {

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    // SIMPLE PROPERTIES:
    static final StringProperty DOMAIN_NAME = new StringProperty("domainName");
    static final StringProperty BASE_PATH = new StringProperty("basePath");
    static final EnumProperty<WebConfigurationStatus> STATUS = new EnumProperty<>(WebConfigurationStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<ApiKey> SIGNING_KEY = new ResourceReference<>("signingKey", ApiKey.class);
    static final ResourceReference<Application> APPLICATION = new ResourceReference<>("application", Application.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<>("tenant", Tenant.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(DOMAIN_NAME, BASE_PATH, STATUS,
            CREATED_AT, MODIFIED_AT, SIGNING_KEY, APPLICATION, TENANT);

    public DefaultWebConfiguration(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultWebConfiguration(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getDomainName() {
        return getString(DOMAIN_NAME);
    }

    @Override
    public String getBasePath() {
        return getString(BASE_PATH);
    }

    public void setBasePath(String basePath) {
        setProperty(BASE_PATH, basePath);
    }

    @Override
    public WebConfigurationStatus getStatus() {
        return getEnumProperty(STATUS);
    }

    public void setStatus(WebConfigurationStatus status) {
        setProperty(STATUS, status);
    }

    @Override
    public ApiKey getSigningKey() {
        return getResourceProperty(SIGNING_KEY);
    }

    public void setSigningKey(ApiKey apiKey) {
        setProperty(SIGNING_KEY, apiKey);
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
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
