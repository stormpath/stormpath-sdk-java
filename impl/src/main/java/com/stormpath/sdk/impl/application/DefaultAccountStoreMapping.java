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

import com.stormpath.sdk.application.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.IntegerProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;

import java.util.Map;

/**
 * @since 0.9
 */
public class DefaultAccountStoreMapping extends AbstractInstanceResource implements AccountStoreMapping {

    // SIMPLE PROPERTIES:
    static final IntegerProperty LIST_INDEX = new IntegerProperty("listIndex");
    static final BooleanProperty DEFAULT_ACCOUNT_STORE = new BooleanProperty("isDefaultAccountStore");
    static final BooleanProperty DEFAULT_GROUP_STORE = new BooleanProperty("isDefaultGroupStore");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class);
    static final ResourceReference<AccountStore> ACCOUNT_STORE = new ResourceReference<AccountStore>("accountStore", AccountStore.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            LIST_INDEX, DEFAULT_ACCOUNT_STORE, DEFAULT_GROUP_STORE, APPLICATION, ACCOUNT_STORE);

    public DefaultAccountStoreMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountStoreMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public void setApplication(Application application) {
        setResourceProperty(APPLICATION, application);
    }

    @Override
    public AccountStore getAccountStore() {
    // Unfortunately we cannot just call getResourceProperty(ACCOUNT_STORE) because there is no DefaultAccountStore class.
    // The href will tell us what we need to return and works since directories and groups are subclasses of AccountStore.
        String href = getAccountStoreHref();
        AccountStore accountStore = null;
        if (href.contains("directories")) {
            accountStore = getDataStore().getResource(href, Directory.class);
        } else if (href.contains("groups")) {
            accountStore = getDataStore().getResource(href, Group.class);
        }
        return accountStore;
    }

    private String getAccountStoreHref() {
        Map<String, String> map = (Map<String, String>) getProperty(ACCOUNT_STORE.getName());
        String href = null;
        if (map != null && !map.isEmpty()) {
            href = map.get(HREF_PROP_NAME);
        }
        return href;
    }

    @Override
    public void setAccountStore(AccountStore accountStore) {
        setResourceProperty(ACCOUNT_STORE, accountStore);
    }

    @Override
    public int getListIndex() {
        return getInt(LIST_INDEX);
    }

    @Override
    public void setListIndex(int listIndex) {
        setProperty(LIST_INDEX, listIndex);
    }

    @Override
    public boolean isDefaultAccountStore() {
        return getBoolean(DEFAULT_ACCOUNT_STORE);
    }

    @Override
    public void setDefaultAccountStore(boolean defaultAccountStore) {
        setProperty(DEFAULT_ACCOUNT_STORE, defaultAccountStore);
    }

    @Override
    public void setDefaultGroupStore(boolean defaultGroupStore) {
        setProperty(DEFAULT_GROUP_STORE, defaultGroupStore);
    }

    @Override
    public boolean isDefaultGroupStore() {
        return getBoolean(DEFAULT_GROUP_STORE);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
