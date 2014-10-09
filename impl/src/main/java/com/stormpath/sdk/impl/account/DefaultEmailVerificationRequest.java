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
package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.EmailVerificationRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultEmailVerificationRequest extends AbstractResource implements EmailVerificationRequest {

    // SIMPLE PROPERTIES
    static final StringProperty LOGIN = new StringProperty("login");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<AccountStore> ACCOUNT_STORE = new ResourceReference<AccountStore>("accountStore", AccountStore.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(LOGIN, ACCOUNT_STORE);

    public DefaultEmailVerificationRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultEmailVerificationRequest(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getLogin() {
        return getString(LOGIN);
    }

    @Override
    public EmailVerificationRequest setLogin(String usernameOrEmail) {
        setProperty(LOGIN, usernameOrEmail);
        return this;
    }

    @Override
    public AccountStore getAccountStore() {
        return getResourceProperty(ACCOUNT_STORE);
    }

    @Override
    public EmailVerificationRequest setAccountStore(AccountStore accountStore) {
        setProperty(ACCOUNT_STORE, accountStore);
        return this;
    }

}
