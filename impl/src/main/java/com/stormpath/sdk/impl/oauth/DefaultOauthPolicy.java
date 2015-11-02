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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OauthPolicy;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.0.RC6
 */
public class DefaultOauthPolicy extends AbstractInstanceResource implements OauthPolicy {

    // SIMPLE PROPERTIES
    static final StringProperty ACCESS_TOKEN_TTL = new StringProperty("accessTokenTtl");
    static final StringProperty REFRESH_TOKEN_TTL = new StringProperty("refreshTokenTtl");

    static final StringProperty TOKEN_ENDPOINT = new StringProperty("tokenEndpoint");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            ACCESS_TOKEN_TTL, REFRESH_TOKEN_TTL, TOKEN_ENDPOINT, APPLICATION, TENANT);

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public DefaultOauthPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOauthPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getAccessTokenTtl() {
        return getString(ACCESS_TOKEN_TTL);
    }

    @Override
    public String getRefreshTokenTtl() {
        return getString(REFRESH_TOKEN_TTL);
    }

    @Override
    public String getTokenEndpoint() {
        return getString(TOKEN_ENDPOINT);
    }

    @Override
    public OauthPolicy setAccessTokenTtl(String accessTokenTtl) {
        Assert.notNull(accessTokenTtl, "accessTokenTtl cannot be null.");
        setProperty(ACCESS_TOKEN_TTL, accessTokenTtl);
        return this;
    }

    @Override
    public OauthPolicy setRefreshTokenTtl(String refreshTokenTtl) {
        Assert.notNull(refreshTokenTtl, "refreshTokenTtl cannot be null.");
        setProperty(REFRESH_TOKEN_TTL, refreshTokenTtl);
        return this;
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }
}
