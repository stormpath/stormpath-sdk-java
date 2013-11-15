package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import org.junit.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 0.9
 */
class DefaultCustomDataTest {

    @Test
    void testGetPropertyDescriptors(){

        def defaultCustomData = new DefaultCustomData(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultCustomData.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 2)

        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
    }


}
