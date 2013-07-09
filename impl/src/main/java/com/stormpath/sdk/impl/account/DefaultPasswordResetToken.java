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
package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultPasswordResetToken extends AbstractResource implements PasswordResetToken {

    static final StringProperty EMAIL = new StringProperty("email");
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class, true);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(EMAIL, ACCOUNT);

    public DefaultPasswordResetToken(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultPasswordResetToken(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getEmail() {
        return getString(EMAIL);
    }

    @Override
    public void setEmail(String email) {
        setProperty(EMAIL, email);
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }
}
