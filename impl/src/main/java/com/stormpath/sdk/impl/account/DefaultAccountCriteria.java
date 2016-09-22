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
        return orderBy(DefaultAccount.EMAIL);
    }

    @Override
    public AccountCriteria orderByUsername() {
        return orderBy(DefaultAccount.USERNAME);
    }

    @Override
    public AccountCriteria orderByGivenName() {
        return orderBy(DefaultAccount.GIVEN_NAME);
    }

    @Override
    public AccountCriteria orderByMiddleName() {
        return orderBy(DefaultAccount.MIDDLE_NAME);
    }

    @Override
    public AccountCriteria orderBySurname() {
        return orderBy(DefaultAccount.SURNAME);
    }

    @Override
    public AccountCriteria orderByStatus() {
        return orderBy(DefaultAccount.STATUS);
    }

    @Override
    public AccountCriteria withCustomData() {
        getOptions().withCustomData();
        return this;
    }

    @Override
    public AccountCriteria withDirectory() {
        getOptions().withDirectory();
        return this;
    }

    @Override
    public AccountCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public AccountCriteria withGroups() {
        getOptions().withGroups();
        return this;
    }

    @Override
    public AccountCriteria withGroups(int limit) {
        getOptions().withGroups(limit);
        return this;
    }

    @Override
    public AccountCriteria withGroups(int limit, int offset) {
        getOptions().withGroups(limit, offset);
        return this;
    }

    @Override
    public AccountCriteria withGroupMemberships() {
        getOptions().withGroupMemberships();
        return this;
    }

    @Override
    public AccountCriteria withGroupMemberships(int limit) {
        getOptions().withGroupMemberships(limit);
        return this;
    }

    @Override
    public AccountCriteria withGroupMemberships(int limit, int offset) {
        getOptions().withGroupMemberships(limit, offset);
        return this;
    }

    @Override
    public AccountCriteria withLinkedAccounts() {
        getOptions().withLinkedAccounts();
        return this;
    }

    @Override
    public AccountCriteria withLinkedAccounts(int limit) {
        getOptions().withLinkedAccounts(limit);
        return this;
    }

    @Override
    public AccountCriteria withLinkedAccounts(int limit, int offset) {
        getOptions().withLinkedAccounts(limit, offset);
        return this;
    }

    @Override
    public AccountCriteria withAccountLinks() {
        getOptions().withAccountLinks();
        return this;
    }

    @Override
    public AccountCriteria withAccountLinks(int limit) {
        getOptions().withAccountLinks(limit);
        return this;
    }

    @Override
    public AccountCriteria withAccountLinks(int limit, int offset) {
        getOptions().withAccountLinks(limit, offset);
        return this;
    }
}
