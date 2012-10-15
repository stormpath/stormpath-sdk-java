/*
 * Copyright 2012 Stormpath, Inc.
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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultGroup extends AbstractInstanceResource implements Group {

    private static String NAME = "name";
    private static String DESCRIPTION = "description";
    private static String STATUS = "status";
    private static String TENANT = "tenant";
    private static String DIRECTORY = "directory";
    private static String ACCOUNTS = "accounts";

    public DefaultGroup(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroup(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getStringProperty(NAME);
    }

    @Override
    public void setName(String name) {
        setProperty(NAME, name);
    }

    @Override
    public String getDescription() {
        return getStringProperty(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

    @Override
    public Status getStatus() {
        String value = getStringProperty(STATUS);
        if (value == null) {
            return null;
        }
        return Status.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(Status status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT, Tenant.class);
    }

    @Override
    public Directory getDirectory() {
        return getResourceProperty(DIRECTORY, Directory.class);
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS, AccountList.class);
    }

    @Override
    public GroupMembership addAccount(Account account) {
        return DefaultGroupMembership.create(account, this, getDataStore());
    }
}
