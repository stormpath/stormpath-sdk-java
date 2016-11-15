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
package com.stormpath.sdk.impl.application.webconfig;

import com.stormpath.sdk.application.webconfig.MeExpansionConfig;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;

import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultMeExpansionConfig extends ConfigurableProperty implements MeExpansionConfig {

    private static BooleanProperty API_KEYS = new BooleanProperty("apiKeys");
    private static BooleanProperty APPLICATIONS = new BooleanProperty("applications");
    private static BooleanProperty CUSTOM_DATA = new BooleanProperty("customData");
    private static BooleanProperty DIRECTORY = new BooleanProperty("directory");
    private static BooleanProperty GROUP_MEMBERSHIPS = new BooleanProperty("groupMemberships");
    private static BooleanProperty GROUPS = new BooleanProperty("groups");
    private static BooleanProperty PROVIDER_DATA = new BooleanProperty("providerData");
    private static BooleanProperty TENANT = new BooleanProperty("tenant");

    public DefaultMeExpansionConfig(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public boolean isApiKeys() {
        return getBoolean(API_KEYS);
    }

    @Override
    public MeExpansionConfig setApiKeys(boolean apiKeys) {
        setProperty(API_KEYS, apiKeys);
        return this;
    }

    @Override
    public boolean isApplications() {
        return getBoolean(APPLICATIONS);
    }

    @Override
    public MeExpansionConfig setApplications(boolean applications) {
        setProperty(APPLICATIONS, applications);
        return this;
    }

    @Override
    public boolean isCustomData() {
        return getBoolean(CUSTOM_DATA);
    }

    @Override
    public MeExpansionConfig setCustomData(boolean customData) {
        setProperty(CUSTOM_DATA, customData);
        return this;
    }

    @Override
    public boolean isDirectory() {
        return getBoolean(DIRECTORY);
    }

    @Override
    public MeExpansionConfig setDirectory(boolean directory) {
        setProperty(DIRECTORY, directory);
        return this;
    }

    @Override
    public boolean isGroupMemberships() {
        return getBoolean(GROUP_MEMBERSHIPS);
    }

    @Override
    public MeExpansionConfig setGroupMemberships(boolean groupMemberships) {
        setProperty(GROUP_MEMBERSHIPS, groupMemberships);
        return this;
    }

    @Override
    public boolean isGroups() {
        return getBoolean(GROUPS);
    }

    @Override
    public MeExpansionConfig setGroups(boolean groups) {
        setProperty(GROUPS, groups);
        return this;
    }

    @Override
    public boolean isProviderData() {
        return getBoolean(PROVIDER_DATA);
    }

    @Override
    public MeExpansionConfig setProviderData(boolean providerData) {
        setProperty(PROVIDER_DATA, providerData);
        return this;
    }

    @Override
    public boolean isTenant() {
        return getBoolean(TENANT);
    }

    @Override
    public MeExpansionConfig setTenant(boolean tenant) {
        setProperty(TENANT, tenant);
        return this;
    }
}
