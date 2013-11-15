package com.stormpath.sdk.group;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.group.Group} record in Stormpath.
 *
 * @see com.stormpath.sdk.application.Application#createGroup(com.stormpath.sdk.group.CreateGroupRequest)
 * @since 0.9
 */
public interface CreateGroupRequest {

    /**
     * Returns the Group instance for which a new record will be created in Stormpath.
     *
     * @return the Group instance for which a new record will be created in Stormpath.
     */
    Group getGroup();

    /**
     *
     * @return
     */
    boolean isGroupCriteriaSpecified();

    /**
     *
     * @return
     */
    GroupCriteria getGroupCriteria();
}
