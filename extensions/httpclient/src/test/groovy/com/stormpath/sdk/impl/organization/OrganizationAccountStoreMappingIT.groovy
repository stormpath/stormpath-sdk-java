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

import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.AccountStoreVisitor
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
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
 * @since 1.0.RC4.6
 */
class OrganizationAccountStoreMappingIT extends ClientIT {


    List<Organization> organizations
    List<Directory> directories
    Tenant tenant
    Organization org

    @BeforeClass
    void setUpClass() {
        organizations = new ArrayList<Organization>()
        directories = new ArrayList<Directory>()
        tenant = client.getCurrentTenant();
    }

    @BeforeMethod
    void setUpOrg() {
        org = client.instantiate(Organization)
            .name = uniquify("JSDK_OrgIT")
            .setNameKey(uniquify("test").substring(2, 8))
            .status = OrganizationStatus.ENABLED
            .description = uniquify("Test Organization Description")

        org = tenant.createOrganization(Organizations.newCreateRequestFor(org).build())
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
    }

    @Test
    void testAccountStoreMappings() {

        OrganizationAccountStoreMappingList accountStoreMappings = org.getOrganizationAccountStoreMappings()

        6.times{
            org.addAccountStore(createDirectory())  //testing AccountStoreMapping Create
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

        assertEquals(newDefaultAccountStore, org.getDefaultAccountStore())
        assertEquals(newDefaultAccountStore, org.getDefaultGroupStore())
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
        OrganizationAccountStoreMappingList mappings = org.getOrganizationAccountStoreMappings(OrganizationAccountStoreMappings.criteria().withAccountStore().withApplication())
        String mappingsString = mappings.toString()
        assertTrue(mappingsString.contains("Directory"))
        assertTrue(mappingsString.contains("name"))
        assertTrue(mappingsString.contains("description"))
        assertTrue(mappingsString.contains("loginAttempts"))
        assertTrue(mappingsString.contains("passwordResetTokens"))
    }

    @Test
    void testApplicationCriteria() {
        def organization = tenant.getApplications(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).with())
        String applicationString = organization.toString()
        assertTrue(applicationString.contains("listIndex=0"))
        assertTrue(applicationString.contains("isDefaultAccountStore=true"))
        assertTrue(applicationString.contains("isDefaultGroupStore=true"))
        assertTrue(applicationString.contains("name"))
        assertTrue(applicationString.contains("description"))
        assertTrue(applicationString.contains("loginAttempts"))
        assertTrue(applicationString.contains("passwordResetTokens"))
    }

    @Test
    void testApplicationCriteriaLimit() {
        6.times{
            org.addAccountStore(createDirectory())
        }

        def organization = tenant.getApplications(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).withAccountStoreMappings(2))
        String applicationString = organization.toString()
        assertTrue(applicationString.contains("listIndex=0"))
        assertTrue(applicationString.contains("listIndex=1"))
        assertTrue(applicationString.contains("isDefaultAccountStore=true"))
        assertTrue(applicationString.contains("isDefaultGroupStore=true"))
        assertTrue(applicationString.contains("isDefaultAccountStore=false"))
        assertTrue(applicationString.contains("isDefaultGroupStore=false"))
        assertTrue(applicationString.contains("name"))
        assertTrue(applicationString.contains("description"))
        assertTrue(applicationString.contains("loginAttempts"))
        assertTrue(applicationString.contains("passwordResetTokens"))
    }

    @Test
    void testApplicationCriteriaLimitOffset() {
        6.times{
            org.addAccountStore(createDirectory())
        }

        def organization = tenant.getApplications(Organizations.where(Organizations.name().eqIgnoreCase(org.name)).withAccountStoreMappings(2,3))
        String applicationString = organization.toString()
        assertFalse(applicationString.contains("listIndex=0"))
        assertFalse(applicationString.contains("listIndex=1"))
        assertTrue(applicationString.contains("listIndex=3"))
        assertTrue(applicationString.contains("listIndex=4"))
        assertFalse(applicationString.contains("isDefaultAccountStore=true"))
        assertFalse(applicationString.contains("isDefaultGroupStore=true"))
        assertTrue(applicationString.contains("isDefaultAccountStore=false"))
        assertTrue(applicationString.contains("isDefaultGroupStore=false"))
        assertTrue(applicationString.contains("name"))
        assertTrue(applicationString.contains("description"))
        assertTrue(applicationString.contains("loginAttempts"))
        assertTrue(applicationString.contains("passwordResetTokens"))
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testDefaultApplicationGaps() {

        Group group = client.instantiate(Group)
        group.name = uniquify("Java SDK IT Group")
        group.status = GroupStatus.DISABLED

        def dir = (Directory) org.getDefaultAccountStore()
        dir.createGroup(group)
        deleteOnTeardown(group)

        assertNotEquals(org.getDefaultAccountStore().getHref(), group.getHref())
        org.setDefaultAccountStore(group)

        //let's check the changes are visible even without saving
        assertEquals(org.getDefaultAccountStore().getHref(), group.getHref())

        OrganizationAccountStoreMappingList accountStoreMappingList = org.getAccountStoreMappings()
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

        //// Let's create a new account to see if it gets created in the DefaultAccountStore we just set
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

        //Now let's set the DefaultAccountStore to be an already existing account store mapping (this runs different code in DefaultApplication)
        org.setDefaultAccountStore(dir)
        assertEquals(org.getDefaultAccountStore().getHref(), dir.getHref())
        accountStoreMappingList = org.getAccountStoreMappings()
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

        ////////// setDefaultGroupStore time //////////

        def newDir = createDirectory()
        org.setDefaultGroupStore(newDir)

        //let's check the changes are visible even without saving
        assertEquals(org.getDefaultGroupStore().getHref(), newDir.getHref())
        accountStoreMappingList = org.getAccountStoreMappings()
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

        //// Let's create a new group to see if it gets created in the DefaultGroupStore we just set
        def newGroup = client.instantiate(Group)
        newGroup.name = uniquify('Java SDK IT Group')
        newGroup = org.createGroup(newGroup)
        deleteOnTeardown(newGroup)
        assertEquals(newGroup.getDirectory().getHref(), newDir.getHref())

        //Now let's set the DefaultGroupStore to be an already existing account store mapping (this runs different code in DefaultApplication)
        org.setDefaultGroupStore(dir)
        assertEquals(org.getDefaultGroupStore().getHref(), dir.getHref())
        accountStoreMappingList = org.getAccountStoreMappings()
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
        dir.description = dir.name + "-Description"
        dir = tenant.createDirectory(dir);
        directories.add(dir)
        return dir;
    }
//    @Test
//    void testCreate() {
//
//        def tenant = client.currentTenant
//
//        def org = client.instantiate(Organization)
//        org.setName(uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate"))
//                .setDescription("Organization Description")
//                .setNameKey(uniquify("test").substring(2, 8))
//                .setStatus(OrganizationStatus.ENABLED)
//
//        org = tenant.createOrganization(org)
//        assertNotNull org.href
//        deleteOnTeardown(org)
//
//        // test create with wrong account store
//        def orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
//        orgAccountStoreMapping.setOrganization(org)
//        orgAccountStoreMapping.setAccountStore(org)
//        try {
//            def retrieved = tenant.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
//            fail("Should have thrown due to organization cannot be account store error.");
//        } catch (Exception e) {
//            assertEquals(e.getMessage(), "HTTP 400, Stormpath 4614 (http://docs.stormpath.com/errors/4614): An organization can not an account store for another organization.")
//        }
//
//        // create a directory
//        Directory dir = client.instantiate(Directory)
//        dir.name = uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate_dir")
//        dir = tenant.createDirectory(dir);
//        deleteOnTeardown(dir)
//
//        // test using directory as account store
//        orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
//        orgAccountStoreMapping.setOrganization(org)
//        orgAccountStoreMapping.setAccountStore(dir)
//        def retrieved = tenant.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
//        deleteOnTeardown(retrieved)
//
//        assertNotNull retrieved
//        assertEquals orgAccountStoreMapping.href, retrieved.href
//
//        // test using group as account store
//        def org = createTempApp()
//        Group group = client.instantiate(Group)
//        group.name = uniquify("JSDK_OrganizationAccountStoreMappingIT_testCreate_group")
//        group = org.createGroup(group)
//        deleteOnTeardown(group)
//
//        orgAccountStoreMapping = client.instantiate(OrganizationAccountStoreMapping)
//        orgAccountStoreMapping.setOrganization(org)
//        orgAccountStoreMapping.setAccountStore(group)
//        retrieved = tenant.createOrganizationAccountStoreMapping(orgAccountStoreMapping)
//        deleteOnTeardown(retrieved)
//
//        assertNotNull retrieved
//        assertEquals orgAccountStoreMapping.href, retrieved.href
//    }
}