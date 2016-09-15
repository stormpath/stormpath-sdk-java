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

import com.stormpath.sdk.account.AccountLinkingPolicy
import com.stormpath.sdk.account.AccountLinkingStatus
import com.stormpath.sdk.account.AutomaticProvisioningStatus
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
import static org.testng.Assert.assertFalse
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
    void testAddAccountDirWithInvalidDir() {
        Organization organization = client.instantiate(Organization)
        organization.name = uniquify("Java SDK: OrganizationIT.testAddAccountDirWithInvalidDir")
        organization.setNameKey(uniquify("test"))
        organization = client.createOrganization(organization);
        deleteOnTeardown(organization)

        def accountStoreMapping = organization.addAccountStore("non-existent Dir")
        assertNull accountStoreMapping
    }

    /* @since 1.1.0 */
    @Test(enabled = false) //TODO: enable this test when AM-3404 from REST API is available in Production
    void testRetrieveAndUpdateAccountLinkingPolicy() {
        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganization"))
                .setDescription("Organization Description")
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)

        org = client.createOrganization(org)
        assertNotNull org.href
        deleteOnTeardown(org)

        AccountLinkingPolicy accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name() as String, 'DISABLED'
        assertFalse(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertNotNull accountLinkingPolicy.getAutomaticProvisioning()
        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name() as String, 'DISABLED'
        assertFalse(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED)
        accountLinkingPolicy.setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED)
        accountLinkingPolicy.setMatchingProperty("email")
        accountLinkingPolicy.save()

        accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNotNull accountLinkingPolicy.getMatchingProperty()
        assertEquals accountLinkingPolicy.getMatchingProperty(), 'email'
    }

    /* @since 1.1.0 */
    @Test(enabled = false) //TODO: enable this test when AM-3404 from REST API is available in Production
    void testRetrieveAndUpdateAccountLinkingPolicyPartially() {
        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationIT_testCreateOrganization"))
                .setDescription("Organization Description")
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)

        org = client.createOrganization(org)
        assertNotNull org.href
        deleteOnTeardown(org)

        AccountLinkingPolicy accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy

        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name() as String, 'DISABLED'
        assertFalse(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertNotNull accountLinkingPolicy.getAutomaticProvisioning()
        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name() as String, 'DISABLED'
        assertFalse(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED).save() // partially update status
        accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy

        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'DISABLED'
        assertFalse(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED).save() // partially update automatic provisioning
        accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setMatchingProperty("email") // partially update matchingProperty
        accountLinkingPolicy.save()

        accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNotNull accountLinkingPolicy.getMatchingProperty()
        assertEquals accountLinkingPolicy.getMatchingProperty(), 'email'

        accountLinkingPolicy.setMatchingProperty(null) // set matchingProperty to null
        accountLinkingPolicy.save()

        accountLinkingPolicy = org.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(org.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNull accountLinkingPolicy.getMatchingProperty()
    }

    private assertAccountStoreMappingListSize(OrganizationAccountStoreMappingList accountStoreMappings, int expectedSize) {
        int qty = 0;
        for(OrganizationAccountStoreMapping accountStoreMapping : accountStoreMappings) {
            qty++;
        }
        assertEquals(qty, expectedSize)
    }
}
