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

import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultApplicationCriteria extends DefaultCriteria<ApplicationCriteria, ApplicationOptions> implements ApplicationCriteria {

    public DefaultApplicationCriteria() {
        super(new DefaultApplicationOptions());
    }

    @Override
    public ApplicationCriteria orderByName() {
        return orderBy(DefaultApplication.NAME);
    }

    @Override
    public ApplicationCriteria orderByDescription() {
        return orderBy(DefaultApplication.DESCRIPTION);
    }

    @Override
    public ApplicationCriteria orderByStatus() {
        return orderBy(DefaultApplication.STATUS);
    }

    public ApplicationCriteria withAccounts() {
        getOptions().withAccounts();
        return this;
    }

    public ApplicationCriteria withAccounts(int limit) {
        getOptions().withAccounts(limit);
        return this;
    }

    public ApplicationCriteria withAccounts(int limit, int offset) {
        getOptions().withAccounts(limit, offset);
        return this;
    }

    public ApplicationCriteria withGroups() {
        getOptions().withGroups();
        return this;
    }

    public ApplicationCriteria withGroups(int limit) {
        getOptions().withGroups(limit);
        return this;
    }

    public ApplicationCriteria withGroups(int limit, int offset) {
        getOptions().withGroups(limit, offset);
        return this;
    }

    public ApplicationCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public ApplicationCriteria withAccountStoreMappings() {
        getOptions().withAccountStoreMappings();
        return this;
    }

    @Override
    public ApplicationCriteria withAccountStoreMappings(int limit) {
        getOptions().withAccountStoreMappings(limit);
        return this;
    }

    @Override
    public ApplicationCriteria withAccountStoreMappings(int limit, int offset) {
        getOptions().withAccountStoreMappings(limit, offset);
        return this;
    }
}
