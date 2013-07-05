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

import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountOptions;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultAccountCriteria extends DefaultCriteria<AccountCriteria, AccountOptions> implements AccountCriteria {

    public DefaultAccountCriteria() {
        super(new DefaultAccountOptions());
    }

    @Override
    public AccountCriteria orderByEmail() {
        return orderBy(Accounts.EMAIL);
    }

    @Override
    public AccountCriteria orderByUsername() {
        return orderBy(Accounts.USERNAME);
    }

    @Override
    public AccountCriteria orderByGivenName() {
        return orderBy(Accounts.GIVEN_NAME);
    }

    @Override
    public AccountCriteria orderByMiddleName() {
        return orderBy(Accounts.MIDDLE_NAME);
    }

    @Override
    public AccountCriteria orderBySurname() {
        return orderBy(Accounts.SURNAME);
    }

    @Override
    public AccountCriteria orderByStatus() {
        return orderBy(Accounts.STATUS);
    }

    @Override
    public AccountCriteria expandDirectory() {
        getOptions().expandDirectory();
        return this;
    }

    @Override
    public AccountCriteria expandTenant() {
        getOptions().expandTenant();
        return this;
    }

    @Override
    public AccountCriteria expandGroups() {
        getOptions().expandGroups();
        return this;
    }

    @Override
    public AccountCriteria expandGroups(int limit) {
        getOptions().expandGroups(limit);
        return this;
    }

    @Override
    public AccountCriteria expandGroups(int limit, int offset) {
        getOptions().expandGroups(limit, offset);
        return this;
    }

    @Override
    public AccountCriteria expandGroupMemberships() {
        getOptions().expandGroupMemberships();
        return this;
    }

    @Override
    public AccountCriteria expandGroupMemberships(int limit) {
        getOptions().expandGroupMemberships(limit);
        return this;
    }

    @Override
    public AccountCriteria expandGroupMemberships(int limit, int offset) {
        getOptions().expandGroupMemberships(limit, offset);
        return this;
    }
}
