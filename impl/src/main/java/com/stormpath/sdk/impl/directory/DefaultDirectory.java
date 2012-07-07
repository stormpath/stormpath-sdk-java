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
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultDirectory extends AbstractInstanceResource implements Directory {

    private static String NAME = "name";
    private static String DESCRIPTION = "description";
    private static String STATUS = "status";
    private static String ACCOUNTS = "accounts";
    private static String GROUPS = "groups";
    private static String TENANT = "tenant";


    public DefaultDirectory(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultDirectory(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public void createAccount(Account account) {
        AccountList accounts = getAccounts();
        String href = accounts.getHref();
        getDataStore().create(href, account);
    }

    @Override
    public void createAccount(Account account, boolean registrationWorkflowEnabled) {
        AccountList accounts = getAccounts();
        String href = accounts.getHref();
        href += "?registrationWorkflowEnabled=" + registrationWorkflowEnabled;
        getDataStore().create(href, account);
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS, AccountList.class);
    }

    @Override
    public GroupList getGroups() {
        return getResourceProperty(GROUPS, GroupList.class);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT, Tenant.class);
    }
}
