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


package com.stormpath.sdk.impl.group

import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupCriteria
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.9
 */
class DefaultCreateGroupRequestTest {

    @Test
    void testDefault() {
        def group = createStrictMock(Group)
        def request = new DefaultCreateGroupRequest(group, null)

        assertSame(request.group, group)
        assertFalse request.isGroupOptionsSpecified()
    }

    @Test
    void testGetGroupCriteria() {
        def group = createStrictMock(Group)
        def criteria = createStrictMock(GroupCriteria)
        def request = new DefaultCreateGroupRequest(group, criteria)

        assertSame(request.group, group)
        assertTrue request.isGroupOptionsSpecified()
        assertSame(request.groupOptions, criteria)
    }

    @Test(expectedExceptions = IllegalStateException)
    void testAccountCriteriaNotSpecifiedButAccessed() {
        def group = createStrictMock(Group)
        def request = new DefaultCreateGroupRequest(group, null)

        request.getGroupOptions()
    }
}
