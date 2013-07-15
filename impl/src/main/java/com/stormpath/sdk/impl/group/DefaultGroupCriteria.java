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
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultGroupCriteria extends DefaultCriteria<GroupCriteria, GroupOptions> implements GroupCriteria {

    public DefaultGroupCriteria() {
        super(new DefaultGroupOptions());
    }

    @Override
    public GroupCriteria orderByName() {
        return orderBy(DefaultGroup.NAME);
    }

    @Override
    public GroupCriteria orderByDescription() {
        return orderBy(DefaultGroup.DESCRIPTION);
    }

    @Override
    public GroupCriteria orderByStatus() {
        return orderBy(DefaultGroup.STATUS);
    }

    @Override
    public GroupCriteria withDirectory() {
        getOptions().withDirectory();
        return this;
    }

    @Override
    public GroupCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public GroupCriteria withAccounts() {
        getOptions().withAccounts();
        return this;
    }

    @Override
    public GroupCriteria withAccounts(int limit) {
        getOptions().withAccounts(limit);
        return this;
    }

    @Override
    public GroupCriteria withAccounts(int limit, int offset) {
        getOptions().withAccounts(limit, offset);
        return this;
    }

    @Override
    public GroupCriteria withAccountMemberships() {
        getOptions().withAccountMemberships();
        return this;
    }

    @Override
    public GroupCriteria withAccountMemberships(int limit) {
        getOptions().withAccountMemberships(limit);
        return this;
    }

    @Override
    public GroupCriteria withAccountMemberships(int limit, int offset) {
        getOptions().withAccountMemberships(limit, offset);
        return this;
    }
}
