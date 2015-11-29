/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.AccountStoreHolder;
import com.stormpath.sdk.application.AccountStoreMapping;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;

import java.util.Map;

/**
 * @since 1.0.RC7
 */
public abstract class AbstractAccountStoreMapping<T extends AccountStoreMapping> extends AbstractInstanceResource implements AccountStoreMapping {

    // SIMPLE PROPERTIES:
    public static final IntegerProperty LIST_INDEX = new IntegerProperty("listIndex");
    public static final BooleanProperty DEFAULT_ACCOUNT_STORE = new BooleanProperty("isDefaultAccountStore");
    public static final BooleanProperty DEFAULT_GROUP_STORE = new BooleanProperty("isDefaultGroupStore");

    // INSTANCE RESOURCE REFERENCES:
    public static final ResourceReference<AccountStore> ACCOUNT_STORE = new ResourceReference<AccountStore>("accountStore", AccountStore.class);

    public AbstractAccountStoreMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    public AbstractAccountStoreMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

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

    public AccountStoreMapping setAccountStore(AccountStore accountStore) {
        setResourceProperty(ACCOUNT_STORE, accountStore);
        return (T) this;
    }

    public int getListIndex() {
        return getInt(LIST_INDEX);
    }

    public T setListIndex(int listIndex) {
        setProperty(LIST_INDEX, listIndex);
        return (T) this;
    }

    public boolean isDefaultAccountStore() {
        return getBoolean(DEFAULT_ACCOUNT_STORE);
    }

    public T setDefaultAccountStore(boolean defaultAccountStore) {
        setProperty(DEFAULT_ACCOUNT_STORE, defaultAccountStore);
        return (T) this;
    }

    public T setDefaultGroupStore(boolean defaultGroupStore) {
        setProperty(DEFAULT_GROUP_STORE, defaultGroupStore);
        return (T) this;
    }

    public boolean isDefaultGroupStore() {
        return getBoolean(DEFAULT_GROUP_STORE);
    }

    public void delete() {
        getDataStore().delete(this);
    }
//
//    @Override
//    public AccountStoreMapping setHolder(AccountStoreHolder accountStoreHolder) {
//        if
//    }
//
//    @Override
//    public AccountStoreHolder getHolder(AccountStoreHolder accountStoreHolder) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
}
