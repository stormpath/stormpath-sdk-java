/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultApplicationOptions extends DefaultOptions<ApplicationOptions> implements ApplicationOptions {

    public ApplicationOptions withAccounts() {
        return expand(DefaultApplication.ACCOUNTS);
    }

    public ApplicationOptions withAccounts(int limit) {
        return expand(DefaultApplication.ACCOUNTS, limit);
    }

    public ApplicationOptions withAccounts(int limit, int offset) {
        return expand(DefaultApplication.ACCOUNTS, limit, offset);
    }

    public ApplicationOptions withGroups() {
        return expand(DefaultApplication.GROUPS);
    }

    public ApplicationOptions withGroups(int limit) {
        return expand(DefaultApplication.GROUPS, limit);
    }

    public ApplicationOptions withGroups(int limit, int offset) {
        return expand(DefaultApplication.GROUPS, limit, offset);
    }

    public ApplicationOptions withTenant() {
        return expand(DefaultApplication.TENANT);
    }

    @Override
    public Object withAccountStoreMappings() {
        return expand(DefaultApplication.ACCOUNT_STORE_MAPPINGS);
    }

    @Override
    public Object withAccountStoreMappings(int limit) {
        return expand(DefaultApplication.ACCOUNT_STORE_MAPPINGS, limit);
    }

    @Override
    public Object withAccountStoreMappings(int limit, int offset) {
        return expand(DefaultApplication.ACCOUNT_STORE_MAPPINGS, limit, offset);
    }

    /**
     * @since 1.0.0
     */
    @Override
    public ApplicationOptions withCustomData() {
        return expand(DefaultApplication.CUSTOM_DATA);
    }

    @Override
    public Object withSamlPolicy() {
        return expand(DefaultApplication.SAML_POLICY);
    }

    @Override
    public ApplicationOptions withWebConfig() {
        return expand(DefaultApplication.WEB_CONFIGURATION);
    }
}
