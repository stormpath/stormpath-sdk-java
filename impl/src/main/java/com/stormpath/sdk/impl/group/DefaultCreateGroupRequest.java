package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class DefaultCreateGroupRequest implements CreateGroupRequest {

    private final Group group;

    private final GroupCriteria criteria;

    public DefaultCreateGroupRequest(Group group, GroupCriteria criteria) {
        Assert.notNull(group, "Group cannot be null.");
        this.group = group;
        this.criteria = criteria;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }

    @Override
    public boolean isGroupCriteriaSpecified() {
        return this.criteria != null;
    }

    @Override
    public GroupCriteria getGroupCriteria() {
        if(this.criteria == null){
            throw new IllegalStateException("GroupCriteria has not been configured. Use the isGroupCriteriaSpecified method to check first before invoking this method.");
        }
        return this.criteria;
    }
}
