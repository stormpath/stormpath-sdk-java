/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.resource.Resource
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
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

        expect(internalDataStore.deleteResourceProperty(defaultCustomData, "trueProperty"))
        expect(internalDataStore.save(defaultCustomData)).andDelegateTo(
                new DefaultDataStoreDelegateTo(createNiceMock(RequestExecutor), defaultCustomData))
        expect(internalDataStore.delete(defaultCustomData))

        replay internalDataStore

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

        verify internalDataStore

    }

}

// @since 1.0.RC
private class DefaultDataStoreDelegateTo extends DefaultDataStore {

    private CustomData customData;

    DefaultDataStoreDelegateTo(RequestExecutor requestExecutor, CustomData customData) {
        super(requestExecutor)
        this.customData = customData
    }

    public void save(Resource resource) {
        Map properties = getValue(AbstractResource, customData, "properties")
        Map dirtyProperties = getValue(AbstractResource, customData, "dirtyProperties")
        HashSet deletedPropertyNames = getValue(AbstractResource, customData, "deletedPropertyNames")
        properties.putAll(dirtyProperties)
        setValue(AbstractResource, customData, "properties", properties)
        dirtyProperties.clear()
        setValue(AbstractResource, customData, "dirtyProperties", dirtyProperties)
        deletedPropertyNames.clear();
        setValue(AbstractResource, customData, "deletedPropertyNames", deletedPropertyNames)
    }

    private Object getValue(Class clazz, Object object, String fieldName){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

    private void setValue(Class clazz, Object object, String fieldName, value){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }


}
