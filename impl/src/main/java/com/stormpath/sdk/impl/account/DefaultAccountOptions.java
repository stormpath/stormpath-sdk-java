package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountOptions;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultAccountOptions extends DefaultOptions<AccountOptions> implements AccountOptions<AccountOptions> {

    @Override
    public AccountOptions expandDirectory() {
        return expand(Accounts.DIRECTORY);
    }

    @Override
    public AccountOptions expandTenant() {
        return expand(Accounts.TENANT);
    }

    @Override
    public AccountOptions expandGroups() {
        return expand(Accounts.GROUPS);
    }

    @Override
    public AccountOptions expandGroups(int limit) {
        return expand(Accounts.GROUPS, limit);
    }

    @Override
    public AccountOptions expandGroups(int limit, int offset) {
        return expand(Accounts.GROUPS, limit, offset);
    }

    @Override
    public AccountOptions expandGroupMemberships() {
        return expand(Accounts.GROUP_MEMBERSHIPS);
    }

    @Override
    public AccountOptions expandGroupMemberships(int limit) {
        return expand(Accounts.GROUP_MEMBERSHIPS, limit);
    }

    @Override
    public AccountOptions expandGroupMemberships(int limit, int offset) {
        return expand(Accounts.GROUP_MEMBERSHIPS, limit, offset);
    }
}
