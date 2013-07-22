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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultGroupMembershipTest {

    @Test
    void testGetPropertyDescriptors() {

        DefaultGroupMembership defaultGroupMembership = new DefaultGroupMembership(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultGroupMembership.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 2)

        assertTrue(propertyDescriptors.get("account") instanceof ResourceReference && propertyDescriptors.get("account").getType().equals(Account))
        assertTrue(propertyDescriptors.get("group") instanceof ResourceReference && propertyDescriptors.get("group").getType().equals(Group))
    }

}
