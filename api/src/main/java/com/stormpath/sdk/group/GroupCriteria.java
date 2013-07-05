package com.stormpath.sdk.group;

import com.stormpath.sdk.query.Criteria;

/**
 * @since 0.8
 */
public interface GroupCriteria  extends Criteria<GroupCriteria>, GroupOptions<GroupCriteria> {

    GroupCriteria orderByName();

    GroupCriteria orderByDescription();

    GroupCriteria orderByStatus();

}
