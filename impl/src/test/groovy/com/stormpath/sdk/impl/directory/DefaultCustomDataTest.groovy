package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.9
 */
class DefaultCustomDataTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultCustomData = new DefaultCustomData(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultCustomData.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 2)

        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
    }

    @Test
    void testMethods() {
        int defaultPropertiesSize = 3

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/customData",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-01T23:38:55.000Z"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultCustomData = new DefaultCustomData(internalDataStore, properties)

        assertNotNull defaultCustomData.getCreatedAt()
        assertNotNull defaultCustomData.getModifiedAt()

        defaultCustomData.japaneseCharacters = "アナリストは「Apacheの史郎は、今日日本のソフトウェアエンジニアのた"
        defaultCustomData.spanishCharacters = "El niño está usando Stormpath para su aplicación."
        defaultCustomData.trueProperty = true

        assertEquals defaultPropertiesSize + 3, defaultCustomData.size()

        assertFalse defaultCustomData.isEmpty()

        assertTrue defaultCustomData.containsKey("trueProperty")

        assertFalse defaultCustomData.containsKey("notinthere")

        assertTrue defaultCustomData.trueProperty

        assertEquals "El niño está usando Stormpath para su aplicación.", defaultCustomData.getProperty("spanishCharacters")

        assertNotNull defaultCustomData.entrySet()

        defaultCustomData.putAll(["falseProperty": false, "integerProperty": 1234])

        assertEquals defaultPropertiesSize + 5, defaultCustomData.keySet().size()

        assertEquals defaultPropertiesSize + 5, defaultCustomData.values().size()

        assertTrue defaultCustomData.containsValue("アナリストは「Apacheの史郎は、今日日本のソフトウェアエンジニアのた")

        defaultCustomData.remove("trueProperty")

        defaultCustomData.save()

        assertEquals defaultPropertiesSize + 4, defaultCustomData.values().size()

        defaultCustomData.clear()

        assertEquals defaultPropertiesSize, defaultCustomData.size()

        defaultCustomData.delete()

    }


}
