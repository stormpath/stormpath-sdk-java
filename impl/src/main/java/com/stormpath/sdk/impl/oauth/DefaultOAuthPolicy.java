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
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.oauth.openidconnect.Scope;
import com.stormpath.sdk.oauth.openidconnect.ScopeList;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthPolicy extends AbstractInstanceResource implements OAuthPolicy {

    // SIMPLE PROPERTIES
    static final StringProperty ACCESS_TOKEN_TTL = new StringProperty("accessTokenTtl");
    static final StringProperty REFRESH_TOKEN_TTL = new StringProperty("refreshTokenTtl");
    static final StringProperty ID_TOKEN_TTL = new StringProperty("idTokenTtl");

    static final StringProperty TOKEN_ENDPOINT = new StringProperty("tokenEndpoint");
    static final StringProperty REVOCATION_ENDPOINT = new StringProperty("revocationEndpoint");

    static final CollectionReference<ScopeList, Scope> SCOPES =
            new CollectionReference<>("scopes", ScopeList.class, Scope.class);

    static final MapProperty ACCESS_TOKEN_ATTRIBUTE_MAPPINGS = new MapProperty("accessTokenAttributeMappings");
    static final MapProperty ID_TOKEN_ATTRIBUTE_MAPPINGS = new MapProperty("idTokenAttributeMappings");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            ACCESS_TOKEN_TTL, REFRESH_TOKEN_TTL, ID_TOKEN_TTL, TOKEN_ENDPOINT, SCOPES, ACCESS_TOKEN_ATTRIBUTE_MAPPINGS, ID_TOKEN_ATTRIBUTE_MAPPINGS, APPLICATION, TENANT);

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public DefaultOAuthPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuthPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public String getIdTokenTtl() { return getString(ID_TOKEN_TTL); }

    @Override
    public String getTokenEndpoint() {
        return getString(TOKEN_ENDPOINT);
    }

    @Override
    public String getRevocationEndpoint() {
        return getString(REVOCATION_ENDPOINT);
    }

    @Override
    public OAuthPolicy setAccessTokenTtl(String accessTokenTtl) {
        Assert.notNull(accessTokenTtl, "accessTokenTtl cannot be null.");
        setProperty(ACCESS_TOKEN_TTL, accessTokenTtl);
        return this;
    }

    @Override
    public OAuthPolicy setRefreshTokenTtl(String refreshTokenTtl) {
        Assert.notNull(refreshTokenTtl, "refreshTokenTtl cannot be null.");
        setProperty(REFRESH_TOKEN_TTL, refreshTokenTtl);
        return this;
    }

    @Override
    public OAuthPolicy setIdTokenTtl(String idTokenTtl) {
        Assert.notNull(idTokenTtl, "idTokenTtl cannot be null.");
        setProperty(ID_TOKEN_TTL, idTokenTtl);
        return this;
    }

    @Override
    public Scope createScope(Scope scope) throws ResourceException {
        Assert.notNull(scope, "Scope instance cannot be null.");
        return getDataStore().create(getScopes().getHref(), scope);
    }

    @Override
    public ScopeList getScopes() {
        return getResourceProperty(SCOPES);
    }

    @Override
    public Map<String, String> getAccessTokenAttributeMap() {
        return getMap(ACCESS_TOKEN_ATTRIBUTE_MAPPINGS);
    }

    @Override
    public OAuthPolicy setAccessTokenAttributeMap(Map<String, String> accessTokenAttributeMap) {
        setProperty(ACCESS_TOKEN_ATTRIBUTE_MAPPINGS, accessTokenAttributeMap);
        return this;
    }

    @Override
    public Map<String, String> getIdTokenAttributeMap() {
        return getMap(ID_TOKEN_ATTRIBUTE_MAPPINGS);
    }

    @Override
    public OAuthPolicy setIdTokenAttributeMap(Map<String, String> idTokenAttributeMap) {
        setProperty(ID_TOKEN_ATTRIBUTE_MAPPINGS, idTokenAttributeMap);
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
