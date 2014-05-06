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
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.provider.ProviderAccountResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.beta
 */
public class DefaultProviderAccountResultHelper extends AbstractResource implements ProviderAccountResultHelper {

    private static final String PROVIDER_ACCOUNT_RESULT = "providerAccountResult";

    private static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap();

    public DefaultProviderAccountResultHelper(InternalDataStore dataStore) {
        super(dataStore);
    }

    //The information about whether the account was created during the request or not is mixed with Account-specific information.
    //This helper class is in charge of extracting that information from the map and setting the correct properties.
    public DefaultProviderAccountResultHelper(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore);
        if (properties != null && properties.size() > 0) {
            Map<String, Object> providerAccountResultProperties = new LinkedHashMap<String, Object>();
            Map<String, Object> accountProperties = new LinkedHashMap<String, Object>();
            providerAccountResultProperties.put(DefaultProviderAccountResult.NEW_ACCOUNT.getName(), properties.get(DefaultProviderAccountResult.NEW_ACCOUNT.getName()));
            accountProperties.putAll(properties);
            accountProperties.remove(DefaultProviderAccountResult.NEW_ACCOUNT.getName());
            Account account = getDataStore().instantiate(Account.class, accountProperties);
            providerAccountResultProperties.put(DefaultProviderAccountResult.ACCOUNT.getName(), account);
            DefaultProviderAccountResult providerAccountResult = new DefaultProviderAccountResult(dataStore, providerAccountResultProperties);
            setProperty(PROVIDER_ACCOUNT_RESULT, providerAccountResult);
        }
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public ProviderAccountResult getProviderAccountResult() {
        return (ProviderAccountResult) getProperty(PROVIDER_ACCOUNT_RESULT);
    }

}
