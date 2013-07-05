package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultGroupOptions extends DefaultOptions<GroupOptions> implements GroupOptions<GroupOptions> {

    @Override
    public GroupOptions expandDirectory() {
        return expand(Groups.DIRECTORY);
    }

    @Override
    public GroupOptions expandTenant() {
        return expand(Groups.TENANT);
    }

    @Override
    public GroupOptions expandAccounts() {
        return expand(Groups.ACCOUNTS);
    }

    @Override
    public GroupOptions expandAccounts(int limit) {
        return expand(Groups.ACCOUNTS, limit);
    }

    @Override
    public GroupOptions expandAccounts(int limit, int offset) {
        return expand(Groups.ACCOUNTS, limit, offset);
    }

    @Override
    public GroupOptions expandAccountMemberships() {
        return expand(Groups.ACCOUNT_MEMBERSHIPS);
    }

    @Override
    public GroupOptions expandAccountMemberships(int limit) {
        return expand(Groups.ACCOUNT_MEMBERSHIPS, limit);
    }

    @Override
    public GroupOptions expandAccountMemberships(int limit, int offset) {
        return expand(Groups.ACCOUNT_MEMBERSHIPS, limit, offset);
    }
}
