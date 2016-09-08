/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.schema;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.schema.AccountSchema;
import com.stormpath.sdk.schema.Field;
import com.stormpath.sdk.schema.FieldList;

import java.util.Map;

public class DefaultField extends AbstractInstanceResource implements Field {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    public static final BooleanProperty REQUIRED = new BooleanProperty("required");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<AccountSchema> ACCOUNT_SCHEMA = new ResourceReference<AccountSchema>("accountSchema", AccountSchema.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, REQUIRED);

    public DefaultField(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultField(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public Field setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public boolean isRequired() {
        return getBoolean(REQUIRED);
    }

    @Override
    public Field setRequired(boolean required) {
        setProperty(REQUIRED, required);
        return this;
    }

    @Override
    public AccountSchema getAccountSchema() {
        return getResourceProperty(ACCOUNT_SCHEMA);
    }

    public DefaultField setAccountSchema(AccountSchema accountSchema) {
        setResourceProperty(ACCOUNT_SCHEMA, accountSchema);
        return this;
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
