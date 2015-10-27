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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultAccountListTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def resourceWithDS = new DefaultAccountList(internalDataStore)
        def resourceWithProps = new DefaultAccountList(internalDataStore, [href: "https://api.stormpath.com/v1/directories/werw84u2834wejofe/accounts"])
        def resourceWithQueryString = new DefaultAccountList(internalDataStore, [href: "https://api.stormpath.com/v1/directories/werw84u2834wejofe/accounts"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultAccountList && resourceWithProps instanceof DefaultAccountList && resourceWithQueryString instanceof DefaultAccountList)

        assertEquals(resourceWithQueryString.getItemType(), Account)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), Account)

    }

    /* @since 1.0.RC4.4 */
    @Test
    void testSingle() {

        def partiallyMockedDefaultAccountList = createMockBuilder(DefaultAccountList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def account = createStrictMock(Account)

        expect(partiallyMockedDefaultAccountList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(account)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccountList, iterator, account

        assertEquals(partiallyMockedDefaultAccountList.single(), account)

        verify partiallyMockedDefaultAccountList, iterator, account
    }

    /* @since 1.0.RC4.4 */
    @Test
    void testSingleEmpty() {

        def partiallyMockedDefaultAccountList = createMockBuilder(DefaultAccountList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)

        expect(partiallyMockedDefaultAccountList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccountList, iterator

        try {
            partiallyMockedDefaultAccountList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "This list is empty while it was expected to contain one (and only one) element.")
        }

        verify partiallyMockedDefaultAccountList, iterator
    }

    /* @since 1.0.RC4.4 */
    @Test
    void testNotSingle() {

        def partiallyMockedDefaultAccountList = createMockBuilder(DefaultAccountList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def account = createStrictMock(Account)

        expect(partiallyMockedDefaultAccountList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(account)
        expect(iterator.hasNext()).andReturn(true)

        replay partiallyMockedDefaultAccountList, iterator, account

        try {
            partiallyMockedDefaultAccountList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Only a single resource was expected, but this list contains more than one item.")
        }

        verify partiallyMockedDefaultAccountList, iterator, account
    }

}
