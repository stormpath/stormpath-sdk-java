/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.organization

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.AccountStoreVisitor
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList
import com.stormpath.sdk.organization.OrganizationAccountStoreMappings
import com.stormpath.sdk.organization.OrganizationList
import com.stormpath.sdk.organization.OrganizationStatus
import com.stormpath.sdk.organization.Organizations
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertTrue
import static org.junit.Assert.assertNotEquals
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.fail

/**
 * Tests class for OrganizationAccountStoreMapping
 *
 * @since 1.0.RC5
 */
class OrganizationAccountStoreMappingIT extends ClientIT {


    List<Organization> organizations
    List<Directory> directories
    List<Group> groups
    Tenant tenant
    Organization org

    @BeforeClass
    void setUpClass() {
        organizations = new ArrayList<Organization>()
        directories = new ArrayList<Directory>()
        groups = new ArrayList<Group>()
        tenant = client.getCurrentTenant();
    }

    @BeforeMethod
    void setUpOrg() {
        org = client.instantiate(Organization)
        org.name = uniquify("JSDK_OrgIT")
        org.nameKey = (uniquify("test").substring(2, 8))
        org.status = OrganizationStatus.ENABLED
        org.description = uniquify("Test Organization Description")

        org = tenant.createOrganization(org)
        organizations.add(org)
    }

    @AfterClass
    void tearDown() {
        organizations.each { org ->
            if (!(org.name.equals('Stormpath'))) {
                org.delete()
            }
        }
        directories.each { dir ->
            if (!(dir.name.equals('Stormpath Administrators'))) {
                dir.delete()
            }
        }
        groups.each { group ->
            group.delete()
        }
    }

    @Test
    void testAccountStoreMappings() {

        OrganizationAccountStoreMappingList accountStoreMappings = org.getOrganizationAccountStoreMappings()

        6.times{
            org.addAccountStore(createDirectory())  // testing create
        }
        OrganizationAccountStoreMappingList mappings = org.getOrganizationAccountStoreMappings()

        int counter = 0;
        for(OrganizationAccountStoreMapping mapping : mappings) {
            counter++;
            mapping.delete()      //testing OrganizationAccountStoreMapping Delete
        }
        assertEquals(counter, 6) 

        Organization orgRefresh = client.getResource(org.href, Organization)

        OrganizationAccountStoreMappingList mappings2 = orgRefresh.getOrganizationAccountStoreMappings()
        counter = 0;
        for(OrganizationAccountStoreMapping mapping : mappings2) {
            counter++;
        }
        assertEquals(counter, 0) //making sure the previous mappings were deleted.

        5.times {
            Directory dir = createDirectory()
            def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
            orgAccountStoreMapping.setAccountStore(dir)
            orgAccountStoreMapping.setOrganization(orgRefresh)
            orgAccountStoreMapping.setDefaultAccountStore(true)
            orgAccountStoreMapping.setDefaultGroupStore(true)
            orgAccountStoreMapping.setListIndex(Integer.MAX_VALUE)
            orgRefresh.createOrganizationAccountStoreMapping(orgAccountStoreMapping) 
        }

        OrganizationList orgRefreshList = tenant.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(org.name)))
        orgRefresh = orgRefreshList.first()
        assertNotNull(orgRefresh.getDefaultAccountStore())
        assertNotNull(orgRefresh.getDefaultGroupStore())

        //check and make sure the newly created account store from above is the default account
        // store instead of the original org directory
        AccountStore accountStore = orgRefresh.getDefaultAccountStore()
        accountStore.accept(new AccountStoreVisitor() {
            @Override
            void visit(Group group) {
                //don't really care about this route since we set the default account store to a directory.
                fail("We should not have a group returned when we set a directory as the default account store.")
            }

            @Override
            void visit(Directory directory) {
                assertFalse(directory.name.contains(orgRefresh.name))
            }

            @Override
            void visit(Organization organization) {
                fail("We should not have an organization returned when we set a directory as the default account store.")
            }
        })

        AccountStore defaultAccountStore = orgRefresh.getDefaultGroupStore()
        assertNotNull(defaultAccountStore)

        mappings2 = orgRefresh.getOrganizationAccountStoreMappings()

        counter = 0;
        for(OrganizationAccountStoreMapping mapping : mappings2) {
            counter++;
            mapping.delete()
        }
        assertEquals(counter, 5)

        //Need to refresh org
        orgRefreshList = tenant.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(org.name)))
        orgRefresh = orgRefreshList.first()

        mappings2 = orgRefresh.getOrganizationAccountStoreMappings()
        counter = 0;
        for(OrganizationAccountStoreMapping mapping : mappings2) {
            counter++;
        }
        assertEquals(counter, 0) //should be zero because they all should have been deleted.
        AccountStore defaultGroupStore = orgRefresh.getDefaultGroupStore()
        assertNull(defaultGroupStore)
        defaultAccountStore = orgRefresh.getDefaultAccountStore()
        assertNull(defaultAccountStore)
    }

    @Test
    void testAccountStoreMappingUpdate() {
        5.times { i ->
            Directory dir = createDirectory()
            def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
            orgAccountStoreMapping.setAccountStore(dir)
            orgAccountStoreMapping.setOrganization(org)
            orgAccountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            orgAccountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        }

        def accStrMaps = org.getOrganizationAccountStoreMappings(OrganizationAccountStoreMappings.where(OrganizationAccountStoreMappings.listIndex().eq(1)))
        OrganizationAccountStoreMapping orgAccountStoreMapping = accStrMaps.first()
        AccountStore accountStore = orgAccountStoreMapping.accountStore
        assertNotEquals(accountStore, org.getDefaultAccountStore())
        assertNotEquals(accountStore, org.getDefaultGroupStore())
        orgAccountStoreMapping.setDefaultAccountStore(true)
        orgAccountStoreMapping.setDefaultGroupStore(true)
        orgAccountStoreMapping.setListIndex(0)
        orgAccountStoreMapping.save()

        OrganizationAccountStoreMappingList accountStoreMappings = org.getOrganizationAccountStoreMappings()
        for (OrganizationAccountStoreMapping mapping : accountStoreMappings) {
            if (mapping.listIndex == 0) {
                assertTrue(mapping.isDefaultAccountStore())
                assertTrue(mapping.isDefaultGroupStore())
            } else {
                assertFalse(mapping.isDefaultAccountStore())
                assertFalse(mapping.isDefaultGroupStore())
            }
        }
    }

    @Test
    void testAccountStoreMappingsUpdateNegativeListIndex() {
        Directory dir = createDirectory()
        def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
        orgAccountStoreMapping.setAccountStore(dir)
        orgAccountStoreMapping.setOrganization(org)
        orgAccountStoreMapping.setDefaultAccountStore(true)
        orgAccountStoreMapping.setDefaultGroupStore(true)
        orgAccountStoreMapping.setListIndex(-43)
        try {
            org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        } catch (com.stormpath.sdk.resource.ResourceException re) {
            assertTrue(re.message.contains("listIndex minimum value"))
        }
    }

    @Test
    void testSettingNewDefaultAccountStore() {
        Directory newDefaultAccountStore;

        5.times { i ->
            Directory dir = createDirectory()
            if (i == 2) {
                newDefaultAccountStore=dir
            }
            def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
            orgAccountStoreMapping.setAccountStore(dir)
            orgAccountStoreMapping.setOrganization(org)
            orgAccountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            orgAccountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        }

        assertNotEquals(newDefaultAccountStore, org.getDefaultAccountStore())
        assertNotEquals(newDefaultAccountStore, org.getDefaultGroupStore())

        org.setDefaultAccountStore(newDefaultAccountStore)
        org.setDefaultGroupStore(newDefaultAccountStore)

        assertEquals(newDefaultAccountStore.href, org.getDefaultAccountStore().getHref())
        assertEquals(newDefaultAccountStore.href, org.getDefaultGroupStore().getHref())
    }

    @Test(expectedExceptions = com.stormpath.sdk.resource.ResourceException)
    void testAccountStoreMappingDuplicateAccountStore() {

        List<Directory> dirs = new ArrayList<Directory>()

        5.times {
            Directory dir = createDirectory()
            dirs.add(dir)
            def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
            orgAccountStoreMapping.setAccountStore(dir)
            orgAccountStoreMapping.setOrganization(org)
            org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        }

        def dir = dirs.get(3)
        def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
        orgAccountStoreMapping.setAccountStore(dir)
        orgAccountStoreMapping.setOrganization(org)
        org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
    }

    @Test
    void testCreateAccountWithoutDefaultAccountStore() {

        Account acct = client.instantiate(Account)
        acct.username = uniquify('JSDK_testAccountForOrg')
        acct.password = 'Changeme1!'
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'

        try {
            org.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
            fail("Should have thrown due to missing accountStore")
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No account store assigned to this organization has been configured as the default storage location for newly created accounts.")
        }
    }

    @Test
    void testOrganizationAccountStoreMappingCriteria() {
        2.times { i ->
            Directory dir = createDirectory()
            def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
            orgAccountStoreMapping.setAccountStore(dir)
            orgAccountStoreMapping.setOrganization(org)
            orgAccountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            orgAccountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        }
        OrganizationAccountStoreMappingList mappings = org.getOrganizationAccountStoreMappings(OrganizationAccountStoreMappings.criteria().withAccountStore())
        String mappingsString = mappings.toString()
        assertTrue(mappingsString.contains("Directory"))
        assertTrue(mappingsString.contains("name"))
        assertTrue(mappingsString.contains("description"))
    }

    @Test
    void testOrganizationCriteria() {
        Directory dir = createDirectory()
        org.addAccountStore(dir)

        def organization = tenant.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).withOrganizationAccountStoreMappings())
        String orgString = organization.toString()
        assertTrue(orgString.contains("listIndex=0"))
        assertTrue(orgString.contains("isDefaultAccountStore=false"))
        assertTrue(orgString.contains("isDefaultGroupStore=false"))
        assertTrue(orgString.contains("name"))
        assertTrue(orgString.contains("description"))

        org.setDefaultAccountStore(dir)
        org.setDefaultGroupStore(dir)

        organization = tenant.getOrganizations(
                Organizations.where(Organizations.name().eqIgnoreCase(org.name)).and(Organizations.description().eqIgnoreCase(org.description))
                        .withOrganizationAccountStoreMappings())
        orgString = organization.toString()
        assertTrue(orgString.contains("listIndex=0"))
        assertTrue(orgString.contains("isDefaultAccountStore=true"))
        assertTrue(orgString.contains("isDefaultGroupStore=true"))
        assertTrue(orgString.contains("name"))
        assertTrue(orgString.contains("description"))
    }

    @Test
    void testOrganizationCriteriaLimit() {
        6.times{
            org.addAccountStore(createDirectory())
        }

        def organization = tenant.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).withOrganizationAccountStoreMappings(2))
        String orgString = organization.toString()
        assertTrue(orgString.contains("listIndex=0"))
        assertTrue(orgString.contains("listIndex=1"))
        assertTrue(orgString.contains("isDefaultAccountStore=false"))
        assertTrue(orgString.contains("isDefaultGroupStore=false"))
        assertTrue(orgString.contains("name"))
        assertTrue(orgString.contains("description"))
    }

    @Test
    void testOrganizationCriteriaLimitOffset() {
        6.times{
            org.addAccountStore(createDirectory())
        }

        def organization = tenant.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).withOrganizationAccountStoreMappings(2,3))
        String orgString = organization.toString()
        assertFalse(orgString.contains("listIndex=0"))
        assertFalse(orgString.contains("listIndex=1"))
        assertTrue(orgString.contains("listIndex=3"))
        assertTrue(orgString.contains("listIndex=4"))
        assertFalse(orgString.contains("isDefaultAccountStore=true"))
        assertFalse(orgString.contains("isDefaultGroupStore=true"))
        assertTrue(orgString.contains("isDefaultAccountStore=false"))
        assertTrue(orgString.contains("isDefaultGroupStore=false"))
        assertTrue(orgString.contains("name"))
        assertTrue(orgString.contains("description"))
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testDefaultOrganizationGaps() {

        Group group = client.instantiate(Group)
        group.name = uniquify("Java SDK IT Group")
        group.status = GroupStatus.DISABLED

        Directory dir = createDirectory()
        org.setDefaultAccountStore(dir)

        dir.createGroup(group)
        deleteOnTeardown(group)

        assertNotEquals(org.getDefaultAccountStore().getHref(), group.getHref())
        org.setDefaultAccountStore(group)
        assertEquals(org.getDefaultAccountStore().getHref(), group.getHref())

        OrganizationAccountStoreMappingList accountStoreMappingList = org.getOrganizationAccountStoreMappings()
        assertEquals(accountStoreMappingList.size, 2)

        for (OrganizationAccountStoreMapping orgAccountStoreMapping : accountStoreMappingList) {
            if (orgAccountStoreMapping.getAccountStore().getHref().equals(group.getHref())) {
                if(!orgAccountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is not marked as default in the OrganizationAccountStoreMappingList")
                }
            } else if (orgAccountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(orgAccountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is wrongly marked as default in the OrganizationAccountStoreMappingList")
                }
            }
        }

        // Create a new account to see if it gets created in the new DefaultAccountStore
        group.setStatus(GroupStatus.ENABLED)
        group.save()
        Account acct = client.instantiate(Account)
        acct.username = uniquify('Stormpath-SDK-Test-AccountStore')
        acct.password = 'Changeme1!'
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = org.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)
        assertEquals(acct.getDirectory().getHref(), group.getDirectory().getHref())

        // Set the DefaultAccountStore to be an already existing account store mapping
        org.setDefaultAccountStore(dir)
        assertEquals(org.getDefaultAccountStore().getHref(), dir.getHref())
        accountStoreMappingList = org.getOrganizationAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 2)
        for (OrganizationAccountStoreMapping orgAccountStoreMapping : accountStoreMappingList) {
            if (orgAccountStoreMapping.getAccountStore().getHref().equals(group.getHref())) {
                if(orgAccountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is wrongly marked as default in the OrganizationAccountStoreMappingList")
                }
            } else if (orgAccountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(!orgAccountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is not marked as default in the OrganizationAccountStoreMappingList")
                }
            }
        }

        def newDir = createDirectory()
        org.setDefaultGroupStore(newDir)

        assertEquals(org.getDefaultGroupStore().getHref(), newDir.getHref())
        accountStoreMappingList = org.getOrganizationAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 3)
        for (OrganizationAccountStoreMapping orgAccountStoreMapping : accountStoreMappingList) {
            if (orgAccountStoreMapping.getAccountStore().getHref().equals(newDir.getHref())) {
                if(!orgAccountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is not marked as default in the OrganizationAccountStoreMappingList")
                }
            } else if (orgAccountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(orgAccountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is wrongly marked as default in the OrganizationAccountStoreMappingList")
                }

            }
        }

        // Create a new group to see if it gets created in the new DefaultGroupStore
        def newGroup = client.instantiate(Group)
        newGroup.name = uniquify('Java SDK IT Group')
        newGroup = org.createGroup(newGroup)
        deleteOnTeardown(newGroup)
        assertEquals(newGroup.getDirectory().getHref(), newDir.getHref())

        // Set the DefaultGroupStore to be an already existing account store mapping
        org.setDefaultGroupStore(dir)
        assertEquals(org.getDefaultGroupStore().getHref(), dir.getHref())
        accountStoreMappingList = org.getOrganizationAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 3)
        for (OrganizationAccountStoreMapping orgAccountStoreMapping : accountStoreMappingList) {
            if (orgAccountStoreMapping.getAccountStore().getHref().equals(newDir.getHref())) {
                if(orgAccountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is wronlgy marked as default in the OrganizationAccountStoreMappingList")
                }
            } else if (orgAccountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(!orgAccountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is not marked as default in the OrganizationAccountStoreMappingList")
                }
            }
        }

    }

    private Directory createDirectory() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("JSDK: Directory")
        dir = client.createDirectory(dir);
        directories.add(dir)
        return dir;
    }
    @Test
    void testCreate() {

        def tenant = client.currentTenant

        def org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate"))
                .setDescription("Organization Description")
                .setNameKey(uniquify("test").substring(2, 8))
                .setStatus(OrganizationStatus.ENABLED)

        org = tenant.createOrganization(org)
        assertNotNull org.href
        deleteOnTeardown(org)

        // test create with wrong account store
        def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
        orgAccountStoreMapping.setOrganization(org)
        orgAccountStoreMapping.setAccountStore(org)
        try {
            def retrieved = org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
            fail("Should have thrown due to organization cannot be account store error.");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "HTTP 400, Stormpath 4614 (http://docs.stormpath.com/errors/4614): An organization can not an account store for another organization.")
        }

        // create a directory
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate_dir")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)

        // test using directory as store
        orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
        orgAccountStoreMapping.setOrganization(org)
        orgAccountStoreMapping.setAccountStore(dir)
        def retrieved = org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        deleteOnTeardown(retrieved)

        assertNotNull retrieved
        assertEquals orgAccountStoreMapping.href, retrieved.href
        assertEquals orgAccountStoreMapping.accountStore.href, dir.href

        // test using group as account store
        org.setDefaultGroupStore(dir)
        Group group = client.instantiate(Group)
        group.name = uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate_group")
        group = org.createGroup(group)
        deleteOnTeardown(group)

        orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
        orgAccountStoreMapping.setOrganization(org)
        orgAccountStoreMapping.setAccountStore(group)
        retrieved = org.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
        deleteOnTeardown(retrieved)

        assertNotNull retrieved
        assertEquals orgAccountStoreMapping.href, retrieved.href
        assertEquals orgAccountStoreMapping.accountStore.href, group.href
    }
}