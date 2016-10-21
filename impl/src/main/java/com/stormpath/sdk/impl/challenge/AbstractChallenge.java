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
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public abstract class AbstractChallenge<T extends Factor, R extends Enum> extends AbstractInstanceResource implements Challenge<T,R> {

    public static final EnumProperty<Enum> STATUS = new EnumProperty<>("status", Enum.class);
    static final StringProperty CODE = new StringProperty("code");
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);
    static final ResourceReference<? extends Factor> FACTOR = new ResourceReference<>("factor", Factor.class);
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    protected static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(STATUS, CODE, ACCOUNT, FACTOR, CREATED_AT, MODIFIED_AT);

    public AbstractChallenge(InternalDataStore dataStore) {
        super(dataStore);
    }

    public AbstractChallenge(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Challenge setAccount(Account account) {
        setResourceProperty(ACCOUNT,account);
        return this;
    }

    @Override
    public T getFactor() {
        return (T) getResourceProperty(FACTOR);
    }

    @Override
    public Challenge setFactor(T factor) {
        setResourceProperty(FACTOR, factor);
        return this;
    }

    @Override
    public boolean validate(String code) {
        Assert.notNull(code, "code can not be null.");
        setCode(code);
        Challenge returnedChallenge = getDataStore().create(getHref(), this);
        if ((returnedChallenge.getStatus()).name().equals("SUCCESS")) {
            return true;
        }
        return false;
    }

    protected Challenge setCode(String code) {
        setProperty(CODE, code);
        return this;
    }
}
