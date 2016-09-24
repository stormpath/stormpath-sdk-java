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
package com.stormpath.sdk.impl.challenge;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeStatus;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultChallenge extends AbstractInstanceResource implements Challenge {

    static final StringProperty MESSAGE = new StringProperty("message");
    static final StringProperty MESSAGE_ID = new StringProperty("messageId");
    static final EnumProperty<ChallengeStatus> STATUS = new EnumProperty<>("status", ChallengeStatus.class);
    static final StringProperty CODE = new StringProperty("code");
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);
    static final ResourceReference<Factor> FACTOR = new ResourceReference<>("factor", Factor.class);
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(MESSAGE, MESSAGE_ID, STATUS, CODE, ACCOUNT, FACTOR, CREATED_AT, MODIFIED_AT);

    public DefaultChallenge(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultChallenge(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    public String getMessage() {
        return getString(MESSAGE);
    }

    @Override
    public Challenge setMessage(String message) {
        setProperty(MESSAGE, message);
        return this;
    }

    @Override
    public ChallengeStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return ChallengeStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Challenge setStatus(ChallengeStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Challenge setAccount(Account account) {
        setResourceProperty(ACCOUNT,account);
        return this;
    }

    @Override
    public Factor getFactor() {
        return getResourceProperty(FACTOR);
    }

    @Override
    public Challenge setFactor(Factor smsFactor) {
        setResourceProperty(FACTOR, smsFactor);
        return this;
    }

    @Override
    public boolean validate(String code) {
        Assert.notNull(code, "code can not be null.");
        setCode(code);
        try {
            getDataStore().create(getHref(), this);
        }
        catch(ResourceException re){
            return false;
        }
        return true;
    }

    private Challenge setCode(String code) {
        setProperty(CODE, code);
        return null;
    }
}
