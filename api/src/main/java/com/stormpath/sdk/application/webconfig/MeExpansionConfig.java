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
package com.stormpath.sdk.application.webconfig;

public interface MeExpansionConfig {

    boolean getApiKeys();

    void setApiKeys(boolean apiKeys);

    boolean getApplications();

    void setApplications(boolean applications);

    boolean getCustomData();

    void setCustomData(boolean customData);

    boolean getDirectory();

    void setDirectory(boolean directory);

    boolean getGroupMemberships();

    void setGroupMemberships(boolean groupMemberships);

    boolean getGroups();

    void setGroups(boolean groups);

    boolean getProviderData();

    void setProviderData(boolean providerData);

    boolean getTenant();

    void setTenant(boolean tenant);

}
