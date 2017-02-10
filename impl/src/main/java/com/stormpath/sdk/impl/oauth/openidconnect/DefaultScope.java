/*
* Copyright 2017 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth.openidconnect;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.oauth.openidconnect.Scope;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.6.0
 */
public class DefaultScope extends AbstractInstanceResource implements Scope {

    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty FRIENDLY_NAME = new StringProperty("friendlyName");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final MapProperty ATTRIBUTE_MAPPINGS = new MapProperty("attributeMappings");
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final ResourceReference<OAuthPolicy> O_AUTH_POLICY = new ResourceReference<>("oAuthPolicy", OAuthPolicy.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(NAME, FRIENDLY_NAME, DESCRIPTION, ATTRIBUTE_MAPPINGS, O_AUTH_POLICY, CREATED_AT, MODIFIED_AT);

    public DefaultScope(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultScope(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public Scope setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getFriendlyName() {
        return getString(FRIENDLY_NAME);
    }

    @Override
    public Scope setFriendlyName(String friendlyName) {
        setProperty(FRIENDLY_NAME, friendlyName);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Scope setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public Map<String, String> getAttributeMappings() {
        return getMap(ATTRIBUTE_MAPPINGS);
    }

    @Override
    public Scope setAttributeMappings(Map<String, String> attributeMappings) {
        setProperty(ATTRIBUTE_MAPPINGS, attributeMappings);
        return this;
    }

    @Override
    public OAuthPolicy getOAuthPolicy() {
        return getResourceProperty(O_AUTH_POLICY);
    }

    @Override
    public Scope setOAuthPolicy(OAuthPolicy oAuthPolicy) {
        setMaterializableResourceProperty(O_AUTH_POLICY, oAuthPolicy);
        return this;
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }
}
