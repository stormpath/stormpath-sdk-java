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
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class DefaultCreateGroupRequest implements CreateGroupRequest {

    private final Group group;

    private final GroupOptions options;

    public DefaultCreateGroupRequest(Group group, GroupOptions options) {
        Assert.notNull(group, "group cannot be null.");
        this.group = group;
        this.options = options;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }

    @Override
    public boolean isGroupOptionsSpecified() {
        return this.options != null;
    }

    @Override
    public GroupOptions getGroupOptions() {
        if(this.options == null){
            throw new IllegalStateException("GroupOptions has not been configured. Use the isGroupOptionsSpecified method to check first before invoking this method.");
        }
        return this.options;
    }
}
