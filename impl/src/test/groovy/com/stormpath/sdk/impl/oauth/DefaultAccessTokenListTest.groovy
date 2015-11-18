/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import com.stormpath.sdk.oauth.AccessToken
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMockBuilder
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail
import static org.easymock.EasyMock.verify

/**
 * Test for DefaultAccessTokenList class
 *
 * @since 1.0.RC7
 */
class DefaultAccessTokenListTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def resourceWithDS = new DefaultAccessTokenList(internalDataStore)
        def resourceWithProps = new DefaultAccessTokenList(internalDataStore, [href: "http://localhost:9191/v1/accounts/1ExISVdfbP89SBBEx6usdn/accessTokens"])
        def resourceWithQueryString = new DefaultAccessTokenList(internalDataStore, [href: "http://localhost:9191/v1/accounts/1ExISVdfbP89SBBEx6usdn/accessTokens"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultAccessTokenList && resourceWithProps instanceof DefaultAccessTokenList && resourceWithQueryString instanceof DefaultAccessTokenList)

        assertEquals(resourceWithQueryString.getItemType(), AccessToken)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), AccessToken)
    }

    @Test
    void testSingle() {

        def partiallyMockedDefaultAccessTokenList = createMockBuilder(DefaultAccessTokenList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def accessToken = createStrictMock(AccessToken)

        expect(partiallyMockedDefaultAccessTokenList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accessToken)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccessTokenList, iterator, accessToken

        assertEquals(partiallyMockedDefaultAccessTokenList.single(), accessToken)

        verify partiallyMockedDefaultAccessTokenList, iterator, accessToken
    }

    @Test
    void testSingleEmpty() {

        def partiallyMockedDefaultAccessTokenList = createMockBuilder(DefaultAccessTokenList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)

        expect(partiallyMockedDefaultAccessTokenList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccessTokenList, iterator

        try {
            partiallyMockedDefaultAccessTokenList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "This list is empty while it was expected to contain one (and only one) element.")
        }

        verify partiallyMockedDefaultAccessTokenList, iterator
    }

    @Test
    void testNotSingle() {

        def partiallyMockedDefaultAccessTokenList = createMockBuilder(DefaultAccessTokenList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def accessToken = createStrictMock(AccessToken)

        expect(partiallyMockedDefaultAccessTokenList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accessToken)
        expect(iterator.hasNext()).andReturn(true)

        replay partiallyMockedDefaultAccessTokenList, iterator, accessToken

        try {
            partiallyMockedDefaultAccessTokenList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Only a single resource was expected, but this list contains more than one item.")
        }

        verify partiallyMockedDefaultAccessTokenList, iterator, accessToken
    }
}
