/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.api.SaveApiKeyRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.1.beta
 */
public class DefaultApiKey extends AbstractInstanceResource implements ApiKey {

    // SIMPLE PROPERTIES
    static final StringProperty ID = new StringProperty("id");
    static final StringProperty SECRET = new StringProperty("secret");
    static final StatusProperty<ApiKeyStatus> STATUS = new StatusProperty<ApiKeyStatus>(ApiKeyStatus.class);

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
    public void save(SaveApiKeyRequest request) {
        Assert.notNull(request, "Request argument cannot be null.");

        String href = getHref();
        StringBuilder hrefBuilder = new StringBuilder(href);

        boolean parameterSet = false;
        if (request.isEncryptSecretOptionSpecified()) {
            hrefBuilder.append(String.format("?encryptSecret=%s", request.isEncryptSecret()));
            parameterSet = true;
        }

        if (request.isEncryptionKeySizeOptionSpecified()) {
            hrefBuilder.append(parameterSet ? "&" : "?");
            hrefBuilder.append(String.format("encryptionKeySize=%d", request.getEncryptionKeySize()));
            parameterSet = true;
        }

        if (request.isEncryptionKeyIterationsOptionSpecified()) {
            hrefBuilder.append(parameterSet ? "&" : "?");
            hrefBuilder.append(String.format("encryptionKeyIterations=%d", request.getEncryptionKeyIterations()));
            parameterSet = true;
        }

        if (request.isEncryptionKeySaltOptionSpecified()) {
            hrefBuilder.append(parameterSet ? "&" : "?");
            hrefBuilder.append(String.format("encryptionKeySalt=%s", request.getEncryptionKeySalt()));
        }

        properties.put(HREF_PROP_NAME, hrefBuilder.toString());

        if (request.isApiKeyOptionsSpecified()) {
            getDataStore().save(this, request.getApiKeyOptions());
            return;
        }

        getDataStore().save(this);
    }
}
