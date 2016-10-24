/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.alpha
 */
public abstract class AbstractLoginAttempt extends AbstractResource implements LoginAttempt {

    private static final String HREF = "href";
    private static final String NAME_KEY = "nameKey";

    protected static final StringProperty TYPE = new StringProperty("type");

    // INSTANCE RESOURCE REFERENCES:
    protected static final MapProperty ACCOUNT_STORE = new MapProperty("accountStore");

    /**
     * This is a transient variable use to keep compatibility with the getAccountStore method, since the ACCOUNT_STORE property was change
     * to a MapProperty to accommodate the Organization nameKey.
     * <p>
     * https://github.com/stormpath/stormpath-sdk-java/issues/284
     *
     * @since 1.1.0
     */
    private AccountStore accountStore;

    public AbstractLoginAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public AbstractLoginAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getType() {
        return getString(TYPE);
    }

    @Override
    public void setType(String type) {
        setProperty(TYPE, type);
    }

    @Override
    public void setAccountStore(AccountStore accountStore) {
        Map<String, String> accountStoreRef = new HashMap<>();

        accountStoreRef.put(HREF, accountStore.getHref());

        setProperty(ACCOUNT_STORE, accountStoreRef);

        this.accountStore = accountStore;
    }

    @Deprecated
    @Override
    public AccountStore getAccountStore() {
        return this.accountStore;
    }

    /**
     * since 1.2.0
     */
    @Override
    public void setOrganizationNameKey(String nameKey) {
        Map<String, String> accountStoreRef = new HashMap<>();

        accountStoreRef.put(NAME_KEY, nameKey);

        setProperty(ACCOUNT_STORE, accountStoreRef);

        this.accountStore = null;
    }
}
