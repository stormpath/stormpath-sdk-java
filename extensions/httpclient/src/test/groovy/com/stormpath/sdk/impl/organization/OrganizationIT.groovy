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
 * @since 1.0.RC7
 */
class OrganizationIT extends ClientIT {

    @Test
    void testCreateAndRetrieveOrganization(){

        def tenant = client.currentTenant

        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganization"))
            .setDescription("Organization Description")
            .setNameKey(uniquify("test"))
            .setStatus(OrganizationStatus.ENABLED)

        org = client.createOrganization(org)
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
    void testCreateOrganizationWithDefaultDirectory(){

        def tenant = client.currentTenant

        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganizationWithDefaultDirectory"))
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)

        def retrievedOrg = client.createOrganization(Organizations.newCreateRequestFor(org).createDirectory().build())
        assertNotNull(retrievedOrg)

        deleteOnTeardown(retrievedOrg.getDefaultAccountStore() as Directory)
        deleteOnTeardown(retrievedOrg)
        assertEquals retrievedOrg.defaultAccountStore.href, retrievedOrg.defaultGroupStore.href

        // test using directory name
        org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganizationWithDefaultDirectory2"))
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)

        def dirName = uniquify("JSDK_test_org_creation_with_dir")

        retrievedOrg = tenant.createOrganization(Organizations.newCreateRequestFor(org).createDirectoryNamed(dirName).build())
        def dir = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()
        assertNotNull(retrievedOrg)

        assertEquals dir.name, dirName

        deleteOnTeardown(retrievedOrg.getDefaultAccountStore() as Directory)
        deleteOnTeardown(retrievedOrg)
    }

    @Test
    void testFilterOrganizations(){

        def tenant = client.currentTenant

        def org1 = client.instantiate(Organization)
        org1.setName(uniquify("JSDK_OrganizationIT_testFilterOrganizations_01"))
                .setNameKey(uniquify("test"))
                .setDescription('testFilterOrganizations_01')
                .setStatus(OrganizationStatus.ENABLED)

        def retrievedOrg1 = client.createOrganization(Organizations.newCreateRequestFor(org1).createDirectory().build())
        assertNotNull(retrievedOrg1)

        def org2 = client.instantiate(Organization)
        org2.setName(uniquify("JSDK_OrganizationIT_FilterOrganizations_02"))
                .setNameKey(uniquify("test"))
                .setDescription('testFilterOrganizations_02')
                .setStatus(OrganizationStatus.ENABLED)

        def retrievedOrg2 = client.createOrganization(Organizations.newCreateRequestFor(org2).createDirectory().build())
        assertNotNull(retrievedOrg2)

        deleteOnTeardown(retrievedOrg1.getDefaultAccountStore() as Directory)
        deleteOnTeardown(retrievedOrg1)

        deleteOnTeardown(retrievedOrg2.getDefaultAccountStore() as Directory)
        deleteOnTeardown(retrievedOrg2)

        //verify that the filter search works with a combination of criteria
        def foundOrgs2 = tenant.getOrganizations(Organizations.where(Organizations.filter('FilterOrganizations')).and(Organizations.description().endsWithIgnoreCase('02')))
        def foundOrg2 = foundOrgs2.iterator().next()
        assertEquals(foundOrg2.href, retrievedOrg2.href)

        //verify that the filter search works
        def allOrgs = tenant.getOrganizations(Organizations.where(Organizations.filter('FilterOrganizations')))
        assertEquals(allOrgs.size(), 2)

        //verify that the filter search returns an empty collection if there is no match
        def emptyCollection = tenant.getOrganizations(Organizations.where(Organizations.filter('not_found')))
        assertTrue(emptyCollection.size() == 0)

        //verify that a non matching criteria added to a matching criteria is working as a final non matching criteria
        //ie. there are no properties matching 'not_found' but there are 1 account matching 'description=02'
        def emptyCollection2 = tenant.getOrganizations(Organizations.where(Organizations.filter('not_found')).and(Organizations.description().endsWithIgnoreCase('02')))
        assertTrue(emptyCollection2.size() == 0)

        //verify that the filter search match with substrings
        def allOrgs2 = tenant.getOrganizations(Organizations.where(Organizations.filter("FilterOrganizations")))
        assertEquals(allOrgs2.size(), 2)

        //test delete:
        for (def org : allOrgs){
            org.delete()
        }
    }

    @Test
    void testGetOrganizationsWithCustomData() {

        def tenant = client.currentTenant
        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testGetOrganizationsWithCustomData")
        organization.setNameKey(uniquify("test"))
        organization.customData.put("someKey", "someValue")
        organization = client.createOrganization(organization);
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
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: OrganizationIT.testAddAccountStoreDirs")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)

        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testAddAccountStoreDirs")
        organization.setNameKey(uniquify("test"))
        organization.customData.put("someKey", "someValue")
        organization = client.createOrganization(organization);
        deleteOnTeardown(organization)

        assertAccountStoreMappingListSize(organization.getAccountStoreMappings(), 0)

        def retrievedAccountStoreMapping = organization.addAccountStore(dir)
        assertAccountStoreMappingListSize(organization.getAccountStoreMappings(), 1)
        assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)

        retrievedAccountStoreMapping.delete()
        assertAccountStoreMappingListSize(organization.getAccountStoreMappings(), 0)

        retrievedAccountStoreMapping = organization.addAccountStore(dir)
        assertAccountStoreMappingListSize(organization.getAccountStoreMappings(), 1)
        assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)
    }

    @Test
    void testAddAccountDirWithInvalidDir(){
        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testAddAccountDirWithInvalidDir")
        organization.setNameKey(uniquify("test"))
        organization = client.createOrganization(organization);
        deleteOnTeardown(organization)

        def accountStoreMapping = organization.addAccountStore("non-existent Dir")
        assertNull accountStoreMapping
    }

    private assertAccountStoreMappingListSize(OrganizationAccountStoreMappingList accountStoreMappings, int expectedSize) {
        int qty = 0;
        for(OrganizationAccountStoreMapping accountStoreMapping : accountStoreMappings) {
            qty++;
        }
        assertEquals(qty, expectedSize)
    }
}
