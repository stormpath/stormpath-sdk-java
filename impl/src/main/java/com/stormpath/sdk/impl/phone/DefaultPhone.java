/*
* Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.phone;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.phone.PhoneStatus;
import com.stormpath.sdk.phone.PhoneVerificationStatus;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultPhone extends AbstractInstanceResource implements Phone {

    static final StringProperty NUMBER = new StringProperty("number");
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final EnumProperty<PhoneStatus> STATUS = new EnumProperty<>("status", PhoneStatus.class);
    static final EnumProperty<PhoneVerificationStatus> VERIFICATION_STATUS = new EnumProperty<>("verificationStatus",PhoneVerificationStatus.class);

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(NUMBER, NAME, DESCRIPTION, STATUS, VERIFICATION_STATUS, ACCOUNT, CREATED_AT, MODIFIED_AT);

    public DefaultPhone(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultPhone(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    public String getNumber() {
        return getString(NUMBER);
    }

    @Override
    public Phone setNumber(String number) {
        setProperty(NUMBER, number);
        return this;
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public Phone setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public Phone setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    @Override
    public PhoneStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return PhoneStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Phone setStatus(PhoneStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public PhoneVerificationStatus getVerificationStatus() {
        String value = getStringProperty(VERIFICATION_STATUS.getName());
        if (value == null) {
            return null;
        }
        return PhoneVerificationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Phone setVerificationStatus(PhoneVerificationStatus verificationStatus) {
        setProperty(VERIFICATION_STATUS, verificationStatus.name());
        return this;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Phone setAccount(Account account) {
        setMaterializableResourceProperty(ACCOUNT, account);
        return this;
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

}
