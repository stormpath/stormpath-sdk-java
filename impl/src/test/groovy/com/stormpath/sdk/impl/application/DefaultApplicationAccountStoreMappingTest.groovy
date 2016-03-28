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
package com.stormpath.sdk.impl.application

import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import com.stormpath.sdk.impl.resource.Property
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 0.9
 */
class DefaultApplicationAccountStoreMappingTest {

    @Test
    void testAll() {
        def internalDataStore = createStrictMock(InternalDataStore)

        DefaultApplicationAccountStoreMappingList resourceWithDS = new DefaultApplicationAccountStoreMappingList(internalDataStore)
        DefaultApplicationAccountStoreMappingList resourceWithProps = new DefaultApplicationAccountStoreMappingList(internalDataStore, [href: "https://api.stormpath.com/v1/applications/werw84u2834wejofe/accountStoreMappings"])
        DefaultApplicationAccountStoreMappingList resourceWithQueryString = new DefaultApplicationAccountStoreMappingList(internalDataStore, [href: "https://api.stormpath.com/v1/applications/werw84u2834wejofe/accountStoreMappings"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultApplicationAccountStoreMappingList && resourceWithProps instanceof DefaultApplicationAccountStoreMappingList && resourceWithQueryString instanceof DefaultApplicationAccountStoreMappingList)

        assertEquals(resourceWithQueryString.getItemType(), ApplicationAccountStoreMapping)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), ApplicationAccountStoreMapping)
    }

    @Test
    void testMethodChaining() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def application = createStrictMock(Application)

        def accountStoreHref = "https://api.stormpath.com/v1/directories/fwerh23348ru2euwEouh"
        def applicationHref = "https://api.stormpath.com/v1/applications/3TqbyZ2qo73eDM4gTp2H94"


        ApplicationAccountStoreMapping accountStoreMapping = new DefaultApplicationAccountStoreMapping(internalDataStore)

        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(application.getHref()).andReturn(applicationHref)
        expect(internalDataStore.getResource(accountStoreHref, Directory)).andReturn(accountStore)
        expect(internalDataStore.instantiate(Application, [href:applicationHref])).andReturn(application)

        replay(internalDataStore, accountStore, application)

        accountStoreMapping = accountStoreMapping.setAccountStore(accountStore)
            .setApplication(application)
            .setDefaultAccountStore(true)
            .setDefaultGroupStore(true)
            .setListIndex(2)

        assertEquals(accountStoreMapping.getAccountStore(), accountStore)
        assertEquals(accountStoreMapping.getApplication(), application)
        assertEquals(accountStoreMapping.getListIndex(), 2)

        verify(internalDataStore, accountStore, application)

    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testGetPropertyDescriptors() {

        def internalDataStore = createStrictMock(InternalDataStore)
        ApplicationAccountStoreMapping accountStoreMapping = new DefaultApplicationAccountStoreMapping(internalDataStore)

        Map<String, Property> props = accountStoreMapping.getPropertyDescriptors()

        assertTrue props.containsKey(DefaultApplicationAccountStoreMapping.LIST_INDEX.name)
        assertTrue props.containsKey(DefaultApplicationAccountStoreMapping.APPLICATION.name)
        assertTrue props.containsKey(DefaultApplicationAccountStoreMapping.ACCOUNT_STORE.name)
        assertTrue props.containsKey(DefaultApplicationAccountStoreMapping.DEFAULT_GROUP_STORE.name)
        assertTrue props.containsKey(DefaultApplicationAccountStoreMapping.DEFAULT_ACCOUNT_STORE.name)

        assertEquals props.size(), 5
    }


}
