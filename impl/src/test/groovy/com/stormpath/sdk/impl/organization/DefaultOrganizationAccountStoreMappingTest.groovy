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
package com.stormpath.sdk.impl.organization

import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ArrayProperty
import com.stormpath.sdk.impl.resource.IntegerProperty
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC7
 */
class DefaultOrganizationAccountStoreMappingTest {

    @Test
    void testAll() {
        def internalDataStore = createStrictMock(InternalDataStore)

        DefaultOrganizationAccountStoreMappingList resourceWithDS = new DefaultOrganizationAccountStoreMappingList(internalDataStore)
        DefaultOrganizationAccountStoreMappingList resourceWithProps = new DefaultOrganizationAccountStoreMappingList(internalDataStore, [href: "https://api.stormpath.com/v1/organizations/werw84u2834wejofe/organizationAccountStoreMappings"])
        DefaultOrganizationAccountStoreMappingList resourceWithQueryString = new DefaultOrganizationAccountStoreMappingList(internalDataStore, [href: "https://api.stormpath.com/v1/organizations/werw84u2834wejofe/organizationAccountStoreMappings"], [q: "blah"])

        assertTrue(resourceWithDS instanceof DefaultOrganizationAccountStoreMappingList && resourceWithProps instanceof DefaultOrganizationAccountStoreMappingList && resourceWithQueryString instanceof DefaultOrganizationAccountStoreMappingList)

        assertEquals(resourceWithQueryString.getItemType(), OrganizationAccountStoreMapping)

        def propertyDescriptors = resourceWithProps.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)
        assertTrue(propertyDescriptors.get("items") instanceof ArrayProperty && propertyDescriptors.get("offset") instanceof IntegerProperty && propertyDescriptors.get("limit") instanceof IntegerProperty)
        assertEquals(propertyDescriptors.get("items").getType(), OrganizationAccountStoreMapping)
    }

    @Test
    void testMethodChaining() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def organization = createStrictMock(Organization)

        def accountStoreHref = "https://api.stormpath.com/v1/directories/fwerh23348ru2euwEouh"
        def organizationHref = "https://api.stormpath.com/v1/organizations/3TqbyZ2qo73eDM4gTp2H94"


        OrganizationAccountStoreMapping OrganizationAccountStoreMapping = new DefaultOrganizationAccountStoreMapping(internalDataStore)

        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(organization.getHref()).andReturn(organizationHref)
        expect(internalDataStore.getResource(accountStoreHref, Directory)).andReturn(accountStore)
        expect(internalDataStore.instantiate(Organization, [href:organizationHref])).andReturn(organization)

        replay(internalDataStore, accountStore, organization)

        OrganizationAccountStoreMapping = OrganizationAccountStoreMapping.setAccountStore(accountStore)
                .setOrganization(organization)
                .setDefaultAccountStore(true)
                .setDefaultGroupStore(true)
                .setListIndex(2)

        assertEquals(OrganizationAccountStoreMapping.getAccountStore(), accountStore)
        assertEquals(OrganizationAccountStoreMapping.getOrganization(), organization)
        assertEquals(OrganizationAccountStoreMapping.getListIndex(), 2)

        verify(internalDataStore, accountStore, organization)

    }


}
