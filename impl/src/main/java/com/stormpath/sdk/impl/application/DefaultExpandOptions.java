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

import com.stormpath.sdk.application.ExpandOptions;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;

import java.util.Map;

public class DefaultExpandOptions extends ConfigurableProperty implements ExpandOptions {

    private static BooleanProperty API_KEYS = new BooleanProperty("apiKeys");
    private static BooleanProperty APPLICATIONS = new BooleanProperty("applications");
    private static BooleanProperty CUSTOM_DATA = new BooleanProperty("customData");
    private static BooleanProperty DIRECTORY = new BooleanProperty("directory");
    private static BooleanProperty GROUP_MEMBERSHIPS = new BooleanProperty("groupMemberships");
    private static BooleanProperty GROUPS = new BooleanProperty("groups");
    private static BooleanProperty PROVIDER_DATA = new BooleanProperty("providerData");
    private static BooleanProperty TENANT = new BooleanProperty("tenant");

    public DefaultExpandOptions(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public boolean getApiKeys() {
        return getBoolean(API_KEYS);
    }

    @Override
    public void setApiKeys(boolean apiKeys) {
        setProperty(API_KEYS, apiKeys);
    }

    @Override
    public boolean getApplications() {
        return getBoolean(APPLICATIONS);
    }

    @Override
    public void setApplications(boolean applications) {
        setProperty(APPLICATIONS, applications);
    }

    @Override
    public boolean getCustomData() {
        return getBoolean(CUSTOM_DATA);
    }

    @Override
    public void setCustomData(boolean customData) {
        setProperty(CUSTOM_DATA, customData);
    }

    @Override
    public boolean getDirectory() {
        return getBoolean(DIRECTORY);
    }

    @Override
    public void setDirectory(boolean directory) {
        setProperty(DIRECTORY, directory);
    }

    @Override
    public boolean getGroupMemberships() {
        return getBoolean(GROUP_MEMBERSHIPS);
    }

    @Override
    public void setGroupMemberships(boolean groupMemberships) {
        setProperty(GROUP_MEMBERSHIPS, groupMemberships);
    }

    @Override
    public boolean getGroups() {
        return getBoolean(GROUPS);
    }

    @Override
    public void setGroups(boolean groups) {
        setProperty(GROUPS, groups);
    }

    @Override
    public boolean getProviderData() {
        return getBoolean(PROVIDER_DATA);
    }

    @Override
    public void setProviderData(boolean providerData) {
        setProperty(PROVIDER_DATA, providerData);
    }

    @Override
    public boolean getTenant() {
        return getBoolean(TENANT);
    }

    @Override
    public void setTenant(boolean tenant) {
        setProperty(TENANT, tenant);
    }
}
