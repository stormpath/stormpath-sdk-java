/*
 * Copyright 2013 Stormpath, Inc.
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
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.testng.Assert.assertFalse

/**
 * @since 0.8
 */
@Test
class HammerManualIT extends ClientIT {

    static Random random = new Random() //doesn't need to be secure - used for generating names

    static List<String> FIRST_NAMES = firstNameList()
    static List<String> LAST_NAMES = lastNameList()

    /**
     * Disabled by default (on purpose), as it hammers ones Stormpath tenant with many requests, and typically
     * should not be run during normal CI IT builds.  Enable it if you want to run the test manually, but DO NOT COMMIT
     * the change to version control!
     */
    @Test
    public void dropTheHammer() {

        Tenant tenant = client.getCurrentTenant();

        //create a bunch of apps to test pagination:

        26.times { i ->
            def app = client.instantiate(Application)
            app.name = uniquify("Test Application")
            app.status = (i % 2 == 0 ? ApplicationStatus.ENABLED : ApplicationStatus.DISABLED)
            app.description = uniquify("Test Application Description")

            app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            //get the auto-created Directory and add some accounts and groups:
            def dirName = app.name + ' Directory'
            def list = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName)))
            def iterator = list.iterator()
            Directory dir = iterator.next()
            assertFalse iterator.hasNext() //should only be the one matching dir

            //add some groups:
            def admin = client.instantiate(Group)
            admin.name = uniquify("Test Group Admin")
            dir.createGroup(admin)
            def users = client.instantiate(Group)
            users.name = uniquify("Test Group Users")
            dir.createGroup(users)
            def disabled = client.instantiate(Group)
            disabled.name = uniquify("Test Group Disabled")
            disabled.status = GroupStatus.DISABLED
            dir.createGroup(disabled)

            //add some users:

            25.times{ j ->

                def account;

                account = client.instantiate(Account)
                account.givenName = randomFirstName().trim()
                account.middleName = "IT Test"
                account.surname = randomLastName().trim()
                account.username = account.givenName.toLowerCase() + '-' + account.surname.toLowerCase() + '-' + UUID.randomUUID()
                account.email = account.username + '@mailinator.com'
                account.password = "changeMe1!"

                if (j % 2 == 0) {
                    account.status = AccountStatus.DISABLED
                }

                dir.createAccount(account)

                if (j % 3 == 0) {
                    admin.addAccount(account)
                } else if (j % 2 == 0) {
                    disabled.addAccount(account)
                } else {
                    users.addAccount(account)
                }
            }
        }

        ApplicationList applications = tenant.getApplications();

        for (Application application : applications) {
            println "Application $application"
        }

        DirectoryList directories = tenant.getDirectories();

        for (Directory directory : directories) {
            directory.getName();
            println "Directory $directory";

            GroupList groupList = directory.getGroups();
            for (Group group : groupList) {
                group.getName()
                println("- Group $group");
            }

            AccountList accountList = directory.getAccounts()
            for (Account account : accountList) {
                println("-- Account $account");
            }
        }
    }

    @Test
    void testStuff() {
        Tenant tenant = client.getCurrentTenant()

        def apps = tenant.getApplications()
        def app = null
        for( def anApp : apps) {
            if (!anApp.name.equals('Stormpath')) {
                app = anApp
                break;
            }
        }

        def accts = app.getAccounts(Accounts.where(Accounts.status().eq(AccountStatus.ENABLED)).orderByEmail().descending().orderBySurname())
        accts.each { println(it) }
    }

    @Test
    void deleteEmAll() {

        Tenant tenant = client.getCurrentTenant();

        def apps = tenant.getApplications()
        apps.each { app ->
            if (!(app.name.equals('Stormpath'))) {
                app.delete()
            }
        }

        def dirs = tenant.getDirectories()
        dirs.each { dir ->
            if (!(dir.name.equals('Stormpath Administrators'))) {
                dir.delete()
            }
        }
    }
}
