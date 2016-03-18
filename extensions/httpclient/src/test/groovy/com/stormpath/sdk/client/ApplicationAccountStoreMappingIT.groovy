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
package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappings
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.AccountStoreVisitor
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.9
 */
class ApplicationAccountStoreMappingIT extends ClientIT {

    List<Application> applications
    List<Directory> directories
    Tenant tenant
    Application app

    @BeforeClass
    void setUpClass() {
        applications = new ArrayList<Application>()
        directories = new ArrayList<Directory>()
        tenant = client.getCurrentTenant();
    }

    @BeforeMethod
    void setUpApp() {
        app = client.instantiate(Application)
        app.name = uniquify("Java SDK IT")
        app.status = ApplicationStatus.ENABLED
        app.description = uniquify("Test Application Description")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())
        applications.add(app)

        //get the auto-created Directory
        def dirName = app.name + ' Directory'
        def list = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName)))
        def iterator = list.iterator()
        Directory dir = iterator.next()
        directories.add(dir)
        assertFalse iterator.hasNext() //should only be the one matching dir
    }

    @AfterClass
    void tearDown() {
        applications.each { app ->
            if (!(app.name.equals('Stormpath'))) {
                app.delete()
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

        ApplicationAccountStoreMappingList accountStoreMappings = app.getAccountStoreMappings()

        6.times{
            app.addAccountStore(createDirectory())  // testing create
        }
        ApplicationAccountStoreMappingList mappings = app.getAccountStoreMappings()

        int counter = 0;
        for(ApplicationAccountStoreMapping mapping : mappings) {
            counter++;
            mapping.delete()      //testing ApplicationAccountStoreMapping Delete
        }
        assertEquals(counter, 7) //counter will equal seven because one dir is associated when app is created, and then we add 6 more in the loop.

        Application appRefresh = client.getResource(app.href, Application)

        ApplicationAccountStoreMappingList mappings2 = appRefresh.getAccountStoreMappings()
        counter = 0;
        for(ApplicationAccountStoreMapping mapping : mappings2) {
            counter++;
            //println(mapping)
        }
        assertEquals(counter, 0) //making sure the previous mappings were deleted.

        5.times {
            Directory dir = createDirectory()
            def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
            accountStoreMapping.setAccountStore(dir)
            accountStoreMapping.setApplication(appRefresh)
            accountStoreMapping.setDefaultAccountStore(true)
            accountStoreMapping.setDefaultGroupStore(true)
            accountStoreMapping.setListIndex(Integer.MAX_VALUE)
            appRefresh.createAccountStoreMapping(accountStoreMapping) //testing ApplicationAccountStoreMapping Create.
        }

        //I think cache was screwing things up, so I reload the application via a different route.
        ApplicationList appRefreshList = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)))
        appRefresh = appRefreshList.first()
        assertNotNull(appRefresh.getDefaultAccountStore())
        assertNotNull(appRefresh.getDefaultGroupStore())

        //check and make sure the newly created account store from above is the default account
        // store instead of the original app directory
        AccountStore accountStore = appRefresh.getDefaultAccountStore()
        accountStore.accept(new AccountStoreVisitor() {
            @Override
            void visit(Group group) {
                //don't really care about this route since we set the default account store to a directory.
                fail("We should not have a group returned when we set a directory as the default account store.")
            }

            @Override
            void visit(Directory directory) {
                assertFalse(directory.name.contains(appRefresh.name))
            }

            @Override
            void visit(Organization organization) {
                fail("We should not have an organization returned when we set a directory as the default account store.")
            }
        })

        AccountStore defaultAccountStore = appRefresh.getDefaultGroupStore()
        assertNotNull(defaultAccountStore)

        mappings2 = appRefresh.getAccountStoreMappings()

        counter = 0;
        for(ApplicationAccountStoreMapping mapping : mappings2) {
            counter++;
            mapping.delete()      //testing ApplicationAccountStoreMapping Delete
        }
        assertEquals(counter, 5)

        //Need to refresh app
        appRefreshList = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)))
        appRefresh = appRefreshList.first()

        mappings2 = appRefresh.getAccountStoreMappings()
        counter = 0;
        for(ApplicationAccountStoreMapping mapping : mappings2) {
            counter++;
            //println(mapping)
        }
        assertEquals(counter, 0) //should be zero because they all should have been deleted.
        AccountStore defaultGroupStore = appRefresh.getDefaultGroupStore()
        assertNull(defaultGroupStore)
        defaultAccountStore = appRefresh.getDefaultAccountStore()
        assertNull(defaultAccountStore)
    }

    @Test
    void testAccountStoreMappingUpdate() {
        5.times { i ->
            Directory dir = createDirectory()
            def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
            accountStoreMapping.setAccountStore(dir)
            accountStoreMapping.setApplication(app)
            accountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            accountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            app.createAccountStoreMapping(accountStoreMapping)
        }

        def accStrMaps = app.getApplicationAccountStoreMappings(
                ApplicationAccountStoreMappings.where(ApplicationAccountStoreMappings.listIndex().eq(1))
        )
        ApplicationAccountStoreMapping accountStoreMapping = accStrMaps.first()
        AccountStore accountStore = accountStoreMapping.accountStore
        assertNotEquals(accountStore, app.getDefaultAccountStore())
        assertNotEquals(accountStore, app.getDefaultGroupStore())
        accountStoreMapping.setDefaultAccountStore(true)
        accountStoreMapping.setDefaultGroupStore(true)
        accountStoreMapping.setListIndex(0)
        accountStoreMapping.save()

        ApplicationAccountStoreMappingList accountStoreMappings = app.getAccountStoreMappings()
        for (ApplicationAccountStoreMapping mapping : accountStoreMappings) {
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
        def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping.setAccountStore(dir)
        accountStoreMapping.setApplication(app)
        accountStoreMapping.setDefaultAccountStore(true)
        accountStoreMapping.setDefaultGroupStore(true)
        accountStoreMapping.setListIndex(-43)
        try {
            app.createAccountStoreMapping(accountStoreMapping)
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
            def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
            accountStoreMapping.setAccountStore(dir)
            accountStoreMapping.setApplication(app)
            accountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            accountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            app.createAccountStoreMapping(accountStoreMapping)
        }

        assertNotEquals(newDefaultAccountStore, app.getDefaultAccountStore())
        assertNotEquals(newDefaultAccountStore, app.getDefaultGroupStore())

        app.setDefaultAccountStore(newDefaultAccountStore)
        app.setDefaultGroupStore(newDefaultAccountStore)

        assertEquals(newDefaultAccountStore.href, app.getDefaultAccountStore().getHref())
        assertEquals(newDefaultAccountStore.href, app.getDefaultGroupStore().getHref())
    }

    @Test(expectedExceptions = com.stormpath.sdk.resource.ResourceException)
    void testAccountStoreMappingDuplicateAccountStore() {

        List<Directory> dirs = new ArrayList<Directory>()

        5.times {
            Directory dir = createDirectory()
            dirs.add(dir)
            def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
            accountStoreMapping.setAccountStore(dir)
            accountStoreMapping.setApplication(app)
            app.createAccountStoreMapping(accountStoreMapping)
        }

        def dir = dirs.get(3)
        def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping.setAccountStore(dir)
        accountStoreMapping.setApplication(app)
        app.createAccountStoreMapping(accountStoreMapping)
    }

    @Test
    void testApplicationAccountStoreMappingCriteria() {

        def dirHref
        3.times { i ->
            Directory dir = createDirectory()
            // dirHref will end up being the last directory name created
            dirHref = dir.href
            def accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
            accountStoreMapping.setAccountStore(dir)
            accountStoreMapping.setApplication(app)
            accountStoreMapping.setDefaultAccountStore(true)   //Should make last one in loop the defaultAccountStore
            accountStoreMapping.setDefaultGroupStore(true)     //Should make last one in loop the defaultGroupStore
            // reverse order so that the last mapping created will be first out when used with orderByListIndex
            accountStoreMapping.setListIndex(2-i)

            app.createAccountStoreMapping(accountStoreMapping)
        }
        ApplicationAccountStoreMappingList mappings = app.getApplicationAccountStoreMappings(
                ApplicationAccountStoreMappings.criteria().withAccountStore().withApplication().orderByListIndex()
        )
        String mappingsString = mappings.toString()

        assertTrue mappingsString.contains("Directory")
        assertTrue mappingsString.contains("name")
        assertTrue mappingsString.contains("description")
        assertTrue mappingsString.contains("loginAttempts")
        assertTrue mappingsString.contains("passwordResetTokens")

        assertEquals mappings.iterator().next().accountStore.href, dirHref
    }

    @Test
    void testApplicationCriteria() {
        def application = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)).withAccountStoreMappings())
        String applicationString = application.toString()
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
            app.addAccountStore(createDirectory())
        }

        def application = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)).withAccountStoreMappings(2))
        String applicationString = application.toString()
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
            app.addAccountStore(createDirectory())
        }

        def application = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)).withAccountStoreMappings(2,3))
        String applicationString = application.toString()
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

        def dir = (Directory) app.getDefaultAccountStore()
        dir.createGroup(group)
        deleteOnTeardown(group)

        assertNotEquals(app.getDefaultAccountStore().getHref(), group.getHref())
        app.setDefaultAccountStore(group)

        //let's check the changes are visible even without saving
        assertEquals(app.getDefaultAccountStore().getHref(), group.getHref())

        ApplicationAccountStoreMappingList accountStoreMappingList = app.getAccountStoreMappings()
        assertEquals(accountStoreMappingList.size, 2)

        for (ApplicationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(group.getHref())) {
                if(!accountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is not marked as default in the ApplicationAccountStoreMappingList")
                }
            } else if (accountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(accountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is wrongly marked as default in the ApplicationAccountStoreMappingList")
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
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)
        assertEquals(acct.getDirectory().getHref(), group.getDirectory().getHref())

        //Now let's set the DefaultAccountStore to be an already existing account store mapping (this runs different code in DefaultApplication)
        app.setDefaultAccountStore(dir)
        assertEquals(app.getDefaultAccountStore().getHref(), dir.getHref())
        accountStoreMappingList = app.getAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 2)
        for (ApplicationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(group.getHref())) {
                if(accountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is wrongly marked as default in the ApplicationAccountStoreMappingList")
                }
            } else if (accountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(!accountStoreMapping.isDefaultAccountStore()) {
                    fail("The DefaultAccountStoreMapping is not marked as default in the ApplicationAccountStoreMappingList")
                }
            }
        }

        ////////// setDefaultGroupStore time //////////

        def newDir = createDirectory()
        app.setDefaultGroupStore(newDir)

        //let's check the changes are visible even without saving
        assertEquals(app.getDefaultGroupStore().getHref(), newDir.getHref())
        accountStoreMappingList = app.getAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 3)
        for (ApplicationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(newDir.getHref())) {
                if(!accountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is not marked as default in the ApplicationAccountStoreMappingList")
                }
            } else if (accountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(accountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is wrongly marked as default in the ApplicationAccountStoreMappingList")
                }

            }
        }

        //// Let's create a new group to see if it gets created in the DefaultGroupStore we just set
        def newGroup = client.instantiate(Group)
        newGroup.name = uniquify('Java SDK IT Group')
        newGroup = app.createGroup(newGroup)
        deleteOnTeardown(newGroup)
        assertEquals(newGroup.getDirectory().getHref(), newDir.getHref())

        //Now let's set the DefaultGroupStore to be an already existing account store mapping (this runs different code in DefaultApplication)
        app.setDefaultGroupStore(dir)
        assertEquals(app.getDefaultGroupStore().getHref(), dir.getHref())
        accountStoreMappingList = app.getAccountStoreMappings()
        assertEquals(accountStoreMappingList.iterator().size(), 3)
        for (ApplicationAccountStoreMapping accountStoreMapping : accountStoreMappingList) {
            if (accountStoreMapping.getAccountStore().getHref().equals(newDir.getHref())) {
                if(accountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is wronlgy marked as default in the ApplicationAccountStoreMappingList")
                }
            } else if (accountStoreMapping.getAccountStore().getHref().equals(dir.getHref())) {
                if(!accountStoreMapping.isDefaultGroupStore()) {
                    fail("The DefaultGroupStoreMapping is not marked as default in the ApplicationAccountStoreMappingList")
                }
            }
        }

    }

    private Directory createDirectory() {
        def app = client.instantiate(Application)
        app.name = uniquify("Java SDK IT")
        app.status = ApplicationStatus.DISABLED
        app.description = uniquify("Test Application Description")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())
        applications.add(app)

        //get the auto-created Directory
        def dirName = app.name + ' Directory'
        def list = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName)))
        def iterator = list.iterator()
        Directory dir = iterator.next()
        directories.add(dir)
        assertFalse iterator.hasNext() //should only be the one matching dir
        return dir;
    }
}
