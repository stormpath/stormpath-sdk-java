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

import com.stormpath.sdk.application.MeExpansionOptions;

public class DefaultMeExpansionOptions implements MeExpansionOptions {

    private boolean expandApiKeys;
    private boolean expandApplications;
    private boolean expandCustomData;
    private boolean expandDirectory;
    private boolean expandGroupMemberships;
    private boolean expandProviderData;
    private boolean expandTenant;

    @Override
    public boolean getExpandApiKeys() {
        return expandApiKeys;
    }

    @Override
    public boolean getExpandApplications() {
        return expandApplications;
    }

    @Override
    public boolean getExpandCustomData() {
        return expandCustomData;
    }

    @Override
    public boolean getExpandDirectory() {
        return expandDirectory;
    }

    @Override
    public boolean getExpandGroupMemberships() {
        return expandGroupMemberships;
    }

    @Override
    public boolean getExpandProviderData() {
        return expandProviderData;
    }

    @Override
    public boolean getExpandTenant() {
        return expandTenant;
    }

    public void setExpandApiKeys(boolean expandApiKeys) {
        this.expandApiKeys = expandApiKeys;
    }

    public void setExpandApplications(boolean expandApplications) {
        this.expandApplications = expandApplications;
    }

    public void setExpandCustomData(boolean expandCustomData) {
        this.expandCustomData = expandCustomData;
    }

    public void setExpandDirectory(boolean expandDirectory) {
        this.expandDirectory = expandDirectory;
    }

    public void setExpandGroupMemberships(boolean expandGroupMemberships) {
        this.expandGroupMemberships = expandGroupMemberships;
    }

    public void setExpandProviderData(boolean expandProviderData) {
        this.expandProviderData = expandProviderData;
    }

    public void setExpandTenant(boolean expandTenant) {
        this.expandTenant = expandTenant;
    }
}
