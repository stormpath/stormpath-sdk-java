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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.provider.ProviderAccountResult;

import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultProviderAccountResult extends AbstractResource implements ProviderAccountResult {

    // SIMPLE PROPERTIES
    static final BooleanProperty NEW_ACCOUNT = new BooleanProperty("isNewAccount");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class);

    private static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(NEW_ACCOUNT, ACCOUNT);

    public DefaultProviderAccountResult(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultProviderAccountResult(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore);

        if (properties != null) {
            setProperty(NEW_ACCOUNT, properties.get(NEW_ACCOUNT.getName()));
            properties.remove(NEW_ACCOUNT.getName());
            Account account = getDataStore().instantiate(Account.class, properties);
            setProperty(ACCOUNT, account);
        }
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public boolean isNewAccount() {
        return getBoolean(NEW_ACCOUNT);
    }

    //@since 1.0.RC3
    @Override
    public String getHref() {
        throw new UnsupportedOperationException("Although this class implements the Resource interface, it is technically " +
                "not a Resource and therefore does not have an href property");
    }

}
