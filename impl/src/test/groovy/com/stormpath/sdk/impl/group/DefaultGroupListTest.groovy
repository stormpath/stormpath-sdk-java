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
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 0.8
 */
class DefaultGroupListTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        DefaultGroupList resourceWithDS = new DefaultGroupList(internalDataStore)
        DefaultGroupList resourceWithProps = new DefaultGroupList(internalDataStore, [href: "https://api.stormpath.com/v1/directories/werw84u2834wejofe/groups"])
        DefaultGroupList resourceWithQueryString = new DefaultGroupList(internalDataStore, [href: "https://api.stormpath.com/v1/directories/werw84u2834wejofe/groups"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultGroupList && resourceWithProps instanceof DefaultGroupList && resourceWithQueryString instanceof DefaultGroupList)

        assertEquals(resourceWithQueryString.getItemType(), Group)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), Group)

    }
}
