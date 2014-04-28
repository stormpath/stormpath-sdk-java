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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.provider.GoogleProviderData
import com.stormpath.sdk.provider.ProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultProviderAccountAccessTest {

    @Test
    void testGetPropertyDescriptors() {
        ProviderAccountAccess<GoogleProviderData> providerAccountAccess = new DefaultProviderAccountAccess<GoogleProviderData>(createStrictMock(InternalDataStore))

        def propertyDescriptors = providerAccountAccess.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 1)
        assertTrue(propertyDescriptors.get("providerData") instanceof ResourceReference && propertyDescriptors.get("providerData").getType().equals(ProviderData))
    }

    @Test
    void testMethods() {
        def internalDataStore = createMock(InternalDataStore)
        def providerData = createMock(ProviderData)
        def properties = new HashMap<String, Object>()

        def providerAccountAccess = new DefaultProviderAccountAccess<GoogleProviderData>(internalDataStore, properties)

        assertNull(providerAccountAccess.getProviderData())
        providerAccountAccess.setProviderData(providerData)
        def returnedProviderData = providerAccountAccess.getProviderData()
        assertSame(returnedProviderData, providerData)
    }

}
