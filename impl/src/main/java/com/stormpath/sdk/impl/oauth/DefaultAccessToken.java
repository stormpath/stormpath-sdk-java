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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.0.RC5
 */
public class DefaultAccessToken extends AbstractInstanceResource implements AccessToken {

    static final StringProperty JWT = new StringProperty("jwt");
    static final DateProperty CREATED_AT = new DateProperty("created_at");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class);
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(JWT, ACCOUNT, APPLICATION, TENANT, CREATED_AT);

    public DefaultAccessToken(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccessToken(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getJwt() {
        return getString(JWT);
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
