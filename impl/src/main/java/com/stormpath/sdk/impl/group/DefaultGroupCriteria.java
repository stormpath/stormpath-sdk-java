package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultGroupCriteria extends DefaultCriteria<GroupCriteria, GroupOptions> implements GroupCriteria {

    public DefaultGroupCriteria() {
        super(new DefaultGroupOptions());
    }

    @Override
    public GroupCriteria orderByName() {
        return orderBy(Groups.NAME);
    }

    @Override
    public GroupCriteria orderByDescription() {
        return orderBy(Groups.DESCRIPTION);
    }

    @Override
    public GroupCriteria orderByStatus() {
        return orderBy(Groups.STATUS);
    }

    @Override
    public GroupCriteria expandDirectory() {
        getOptions().expandDirectory();
        return this;
    }

    @Override
    public GroupCriteria expandTenant() {
        getOptions().expandTenant();
        return this;
    }

    @Override
    public GroupCriteria expandAccounts() {
        getOptions().expandAccounts();
        return this;
    }

    @Override
    public GroupCriteria expandAccounts(int limit) {
        getOptions().expandAccounts(limit);
        return this;
    }

    @Override
    public GroupCriteria expandAccounts(int limit, int offset) {
        getOptions().expandAccounts(limit, offset);
        return this;
    }

    @Override
    public GroupCriteria expandAccountMemberships() {
        getOptions().expandAccountMemberships();
        return this;
    }

    @Override
    public GroupCriteria expandAccountMemberships(int limit) {
        getOptions().expandAccountMemberships(limit);
        return this;
    }

    @Override
    public GroupCriteria expandAccountMemberships(int limit, int offset) {
        getOptions().expandAccountMemberships(limit, offset);
        return this;
    }
}
