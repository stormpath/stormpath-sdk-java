package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountLink
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.1.0
 */
class DefaultAccountLinkListTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def resourceWithDS = new DefaultAccountLinkList(internalDataStore)
        def resourceWithProps = new DefaultAccountLinkList(internalDataStore, [href: "https://api.stormpath.com/v1/accounts/werw84u2834wejofe/accountLinks"])
        def resourceWithQueryString = new DefaultAccountLinkList(internalDataStore, [href: "https://api.stormpath.com/v1/accounts/werw84u2834wejofe/accountLinks"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultAccountLinkList && resourceWithProps instanceof DefaultAccountLinkList && resourceWithQueryString instanceof DefaultAccountLinkList)

        assertEquals(resourceWithQueryString.getItemType(), AccountLink)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), AccountLink)

    }

    @Test
    void testSingle() {

        def partiallyMockedDefaultAccountLinkList = createMockBuilder(DefaultAccountLinkList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def accountLink = createStrictMock(AccountLink)

        expect(partiallyMockedDefaultAccountLinkList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountLink)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccountLinkList, iterator, accountLink

        assertEquals(partiallyMockedDefaultAccountLinkList.single(), accountLink)

        verify partiallyMockedDefaultAccountLinkList, iterator, accountLink
    }

    @Test
    void testSingleEmpty() {

        def partiallyMockedDefaultAccountLinkList = createMockBuilder(DefaultAccountLinkList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)

        expect(partiallyMockedDefaultAccountLinkList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(false)

        replay partiallyMockedDefaultAccountLinkList, iterator

        try {
            partiallyMockedDefaultAccountLinkList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "This list is empty while it was expected to contain one (and only one) element.")
        }

        verify partiallyMockedDefaultAccountLinkList, iterator
    }

    @Test
    void testNotSingle() {

        def partiallyMockedDefaultAccountLinkList = createMockBuilder(DefaultAccountLinkList.class)
                .addMockedMethod("iterator").createMock()
        def iterator = createStrictMock(Iterator)
        def accountLink = createStrictMock(AccountLink)

        expect(partiallyMockedDefaultAccountLinkList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountLink)
        expect(iterator.hasNext()).andReturn(true)

        replay partiallyMockedDefaultAccountLinkList, iterator, accountLink

        try {
            partiallyMockedDefaultAccountLinkList.single()
            fail("should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Only a single resource was expected, but this list contains more than one item.")
        }

        verify partiallyMockedDefaultAccountLinkList, iterator, accountLink
    }

}
