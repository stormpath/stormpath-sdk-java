package com.stormpath.zuul.account;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Function;

/**
 * @since 1.1.0
 */
public class GroupNameFunction implements Function<Group, String> {

    @Override
    public String apply(Group group) {
        return group.getName();
    }
}
