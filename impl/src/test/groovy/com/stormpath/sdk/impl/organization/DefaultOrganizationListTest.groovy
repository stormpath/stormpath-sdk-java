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
package com.stormpath.sdk.impl.organization

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import com.stormpath.sdk.organization.Organization
import org.testng.annotations.Test
import static org.easymock.EasyMock.createMockBuilder
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.RC7
 */
class DefaultOrganizationListTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def resourceWithDS = new DefaultOrganizationList(internalDataStore)
        def resourceWithProps = new DefaultOrganizationList(internalDataStore, [href: "https://api.stormpath.com/v1/organizations"])
        def resourceWithQueryString = new DefaultOrganizationList(internalDataStore, [href: "https://api.stormpath.com/v1/organizations"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultOrganizationList && resourceWithProps instanceof DefaultOrganizationList && resourceWithQueryString instanceof DefaultOrganizationList)

        assertEquals(resourceWithQueryString.getItemType(), Organization)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), Organization)

    }

    @Test
    void testSingle() {

        def partiallyMockedDefaultOrganizationList = createMockBuilder(DefaultOrganizationList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def organization = createStrictMock(Organization)

        expect(partiallyMockedDefaultOrganizationList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(organization)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultOrganizationList, iterator, organization

        assertEquals(partiallyMockedDefaultOrganizationList.single(), organization)

        verify partiallyMockedDefaultOrganizationList, iterator, organization
    }

    @Test
    void testSingleEmpty() {

        def partiallyMockedDefaultOrganizationList = createMockBuilder(DefaultOrganizationList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)

        expect(partiallyMockedDefaultOrganizationList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultOrganizationList, iterator

        try {
            partiallyMockedDefaultOrganizationList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "This list is empty while it was expected to contain one (and only one) element.")
        }

        verify partiallyMockedDefaultOrganizationList, iterator
    }

    @Test
    void testNotSingle() {

        def partiallyMockedDefaultOrganizationList = createMockBuilder(DefaultOrganizationList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def organization = createStrictMock(Organization)

        expect(partiallyMockedDefaultOrganizationList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(organization)
        expect(iterator.hasNext()).andReturn(true)

        replay partiallyMockedDefaultOrganizationList, iterator, organization

        try {
            partiallyMockedDefaultOrganizationList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Only a single resource was expected, but this list contains more than one item.")
        }

        verify partiallyMockedDefaultOrganizationList, iterator, organization
    }
}
