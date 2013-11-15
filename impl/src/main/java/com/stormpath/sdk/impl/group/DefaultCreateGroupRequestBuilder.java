package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.CreateGroupRequestBuilder;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.Groups;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class DefaultCreateGroupRequestBuilder implements CreateGroupRequestBuilder {

    private final Group group;
    private GroupCriteria criteria;

    public DefaultCreateGroupRequestBuilder(Group group) {
        Assert.notNull(group, "Group can't be null.");
        this.group = group;
    }

    @Override
    public CreateGroupRequestBuilder withCustomData() {
        criteria = Groups.criteria().withCustomData();
        return this;
    }

    @Override
    public CreateGroupRequest build() {
        return new DefaultCreateGroupRequest(group, criteria);
    }
}
