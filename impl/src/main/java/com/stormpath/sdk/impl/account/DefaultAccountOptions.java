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

import com.stormpath.sdk.account.AccountOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultAccountOptions extends DefaultOptions<AccountOptions> implements AccountOptions<AccountOptions> {

    @Override
    public AccountOptions withCustomData() {
        return expand(DefaultAccount.CUSTOM_DATA);
    }

    @Override
    public AccountOptions withDirectory() {
        return expand(DefaultAccount.DIRECTORY);
    }

    @Override
    public AccountOptions withTenant() {
        return expand(DefaultAccount.TENANT);
    }

    @Override
    public AccountOptions withGroups() {
        return expand(DefaultAccount.GROUPS);
    }

    @Override
    public AccountOptions withGroups(int limit) {
        return expand(DefaultAccount.GROUPS, limit);
    }

    @Override
    public AccountOptions withGroups(int limit, int offset) {
        return expand(DefaultAccount.GROUPS, limit, offset);
    }

    @Override
    public AccountOptions withGroupMemberships() {
        return expand(DefaultAccount.GROUP_MEMBERSHIPS);
    }

    @Override
    public AccountOptions withGroupMemberships(int limit) {
        return expand(DefaultAccount.GROUP_MEMBERSHIPS, limit);
    }

    @Override
    public AccountOptions withGroupMemberships(int limit, int offset) {
        return expand(DefaultAccount.GROUP_MEMBERSHIPS, limit, offset);
    }

    @Override
    public AccountOptions withLinkedAccounts() {
        return expand(DefaultAccount.LINKED_ACCOUNTS);
    }

    @Override
    public AccountOptions withLinkedAccounts(int limit) {
        return expand(DefaultAccount.LINKED_ACCOUNTS, limit);
    }

    @Override
    public AccountOptions withLinkedAccounts(int limit, int offset) {
        return expand(DefaultAccount.LINKED_ACCOUNTS, limit, offset);
    }

    @Override
    public AccountOptions withAccountLinks() {
        return expand(DefaultAccount.ACCOUNT_LINKS);
    }

    @Override
    public AccountOptions withAccountLinks(int limit) {
        return expand(DefaultAccount.ACCOUNT_LINKS, limit);
    }

    @Override
    public AccountOptions withAccountLinks(int limit, int offset) {
        return expand(DefaultAccount.ACCOUNT_LINKS, limit, offset);
    }
}
