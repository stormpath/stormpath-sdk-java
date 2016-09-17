/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * This implementation represents the api key resource that belongs to a Stormpath {@link Account}.
 * @since 1.0.RC
 */
public class DefaultApiKey extends AbstractInstanceResource implements ApiKey {

    // SIMPLE PROPERTIES
    static final StringProperty ID = new StringProperty("id");
    public static final StringProperty SECRET = new StringProperty("secret");
    static final EnumProperty<ApiKeyStatus> STATUS = new EnumProperty<ApiKeyStatus>(ApiKeyStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ID, SECRET, STATUS, ACCOUNT, TENANT);

    public DefaultApiKey(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApiKey(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getId() {
        return getString(ID);
    }

    @Override
    public String getSecret() {
        return getString(SECRET);
    }

    @Override
    public ApiKeyStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return ApiKeyStatus.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(ApiKeyStatus status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public void save(ApiKeyOptions options) {
        Assert.notNull(options, "options argument cannot be null.");
        getDataStore().save(this, options);
    }

    @Override
    protected boolean isPrintableProperty(String name) {
        return !SECRET.getName().equalsIgnoreCase(name);
    }
}
