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
package com.stormpath.sdk.impl.application

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse


class ApplicationIT extends ClientIT {

    @Test
    void testCreateAppAccount() {

        def tenant = client.currentTenant

        def app = client.instantiate(Application)

        app.name = uniquify("DELETEME")

        def dirName = uniquify("DELETEME")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectoryNamed(dirName).build())
        def dir = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()

        deleteOnTeardown(dir)
        deleteOnTeardown(app)

        def email = 'deleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email =  email
        account.password = 'Changeme1!'

        def created = app.createAccount(account)

        //verify it was created:

        def found = app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email))).iterator().next()
        assertEquals(created.href, found.href)

        //test delete:
        found.delete()

        def list = app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email)))
        assertFalse list.iterator().hasNext() //no results
    }

    @Test
    void testCreateAppGroup() {

        def tenant = client.currentTenant

        def app = client.instantiate(Application)

        app.name = uniquify("DELETEME")

        def dirName = uniquify("DELETEME")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectoryNamed(dirName).build())
        def dir = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()

        deleteOnTeardown(dir)
        deleteOnTeardown(app)

        Group group = client.instantiate(Group)
        group.name = uniquify('DELETEME')

        def created = app.createGroup(group)

        //verify it was created:

        def found = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name))).iterator().next()

        assertEquals(created.href, found.href)

        //test delete:
        found.delete()

        def list = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name)))
        assertFalse list.iterator().hasNext() //no results
    }

}
