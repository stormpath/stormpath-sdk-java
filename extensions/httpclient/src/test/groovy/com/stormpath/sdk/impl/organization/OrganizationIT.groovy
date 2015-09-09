/*
 * Copyright 2015 Stormpath, Inc.
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

import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList
import com.stormpath.sdk.organization.OrganizationList
import com.stormpath.sdk.organization.OrganizationStatus
import com.stormpath.sdk.organization.Organizations
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC4.6
 */
class OrganizationIT extends ClientIT {

    @Test
    void testCreateAndRetrieveOrganization(){

        def tenant = client.currentTenant

        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganization"))
            .setDescription("Organization Description")
            .setNameKey(uniquify("test").substring(2, 8))
            .setStatus(OrganizationStatus.ENABLED)

        org = tenant.createOrganization(org)
        assertNotNull org.href
        deleteOnTeardown(org)

        // get organizations without criteria
        OrganizationList orgList = tenant.getOrganizations()
        assertTrue orgList.iterator().hasNext()

        // get organizations without criteria
        tenant.getOrganizations()

        // get organizations with criteria
        orgList = tenant.getOrganizations(Organizations.where(Organizations.name().containsIgnoreCase("JSDK_OrganizationIT_testCreateOrganization")))
        assertTrue orgList.iterator().hasNext()
        def retrieved = orgList.iterator().next()
        assertEquals org.href, retrieved.href
        assertEquals org.name, retrieved.name
    }

    @Test
    void testGetOrganizationsWithCustomData() {

        def tenant = client.currentTenant
        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testGetOrganizationsWithCustomData")
        organization.setNameKey(uniquify("test").substring(2, 8))
        organization.customData.put("someKey", "someValue")
        organization = tenant.createOrganization(organization);
        assertNotNull organization.href

        deleteOnTeardown(organization)

        def dirList = tenant.getOrganizations(Organizations.where(Organizations.name().containsIgnoreCase("OrganizationIT.testGetOrganizationsWithCustomData")).withCustomData())

        def count = 0
        for (Organization org : dirList) {
            count++
            assertNotNull(org.getHref())
            assertEquals(org.getCustomData().size(), 4)
        }
        assertEquals(count, 1)
    }

    @Test
    void testAddAccountStoreDirs() {
        def tenant = client.currentTenant

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: OrganizationIT.testAddAccountStoreDirs")
        dir.description = dir.name + "-Description"
        dir = tenant.createDirectory(dir);
        deleteOnTeardown(dir)

        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testAddAccountStoreDirs")
        organization.setNameKey(uniquify("test").substring(2, 8))
        organization.customData.put("someKey", "someValue")
        organization = tenant.createOrganization(organization);
        deleteOnTeardown(organization)

        assertAccountStoreMappingListSize(organization.getOrganizationAccountStoreMappings(), 0)

        def retrievedAccountStoreMapping = organization.addAccountStore(dir)
        assertAccountStoreMappingListSize(organization.getOrganizationAccountStoreMappings(), 1)
        assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)

        retrievedAccountStoreMapping.delete()
        assertAccountStoreMappingListSize(organization.getOrganizationAccountStoreMappings(), 0)

        retrievedAccountStoreMapping = organization.addAccountStore(dir)
        assertAccountStoreMappingListSize(organization.getOrganizationAccountStoreMappings(), 1)
        assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)

        // Test Non-existent
        retrievedAccountStoreMapping = organization.addAccountStore("non-existent Dir")
        assertNull(retrievedAccountStoreMapping)
    }

    private assertAccountStoreMappingListSize(OrganizationAccountStoreMappingList accountStoreMappings, int expectedSize) {
        int qty = 0;
        for(OrganizationAccountStoreMapping accountStoreMapping : accountStoreMappings) {
            qty++;
        }
        assertEquals(qty, expectedSize)
    }
}
