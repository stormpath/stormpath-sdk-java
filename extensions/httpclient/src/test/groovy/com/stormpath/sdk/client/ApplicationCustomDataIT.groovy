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
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import org.testng.annotations.Test

/**
 * @since 0.9
 */
class ApplicationCustomDataIT extends AbstractCustomDataIT {

    Application application

    @Test
    void testApplicationCustomData() {

        application = createTempApp()

        //TEST Accounts
        def postedCustomData = createComplexData()
        def account1 = createAccount(postedCustomData, false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), true)

        postedCustomData = createComplexData()
        def account2 = createAccount(postedCustomData, true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account3 = createAccount(postedCustomData, false)
        updateAccount(account3, postedCustomData, [:], false)
        updateAccount(account3, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account4 = createAccount(postedCustomData, true)
        updateAccount(account4, postedCustomData, [:], true)
        updateAccount(account4, postedCustomData, createDataForUpdate(), true)

        //TEST Groups
        postedCustomData = createComplexData()
        def group1 = createGroup(postedCustomData, false)
        updateGroup(group1, postedCustomData, createDataForUpdate(), false)
        updateGroup(group1, postedCustomData, createDataForUpdate(), true)
        updateGroup(group1, postedCustomData, [:], false)

        postedCustomData = createComplexData()
        def group2 = createGroup(postedCustomData, true)
        updateGroup(group2, postedCustomData, createDataForUpdate(), true)
        updateGroup(group2, postedCustomData, createDataForUpdate(), false)
        updateGroup(group2, postedCustomData, [:], true)

        postedCustomData = [:]
        def group3 = createGroup(postedCustomData, false)
        updateGroup(group3, postedCustomData, [:], false)
        updateGroup(group3, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def group4 = createGroup(postedCustomData, true)
        updateGroup(group4, postedCustomData, [:], true)
        updateGroup(group4, postedCustomData, createDataForUpdate(), true)

        //TEST Directories
        postedCustomData = createComplexData()
        def dir1 = createDirectory(postedCustomData)
        updateDirectory(dir1, postedCustomData, createDataForUpdate())
        updateDirectory(dir1, postedCustomData, [:])

        postedCustomData = [:]
        def dir3 = createDirectory(postedCustomData)
        updateDirectory(dir3, postedCustomData, [:])
        updateDirectory(dir3, postedCustomData, createDataForUpdate())

        //TEST Applications
        postedCustomData = createComplexData()
        def app1 = createApplication(postedCustomData)
        updateApplication(app1, postedCustomData, createDataForUpdate())
        updateApplication(app1, postedCustomData, [:])

        postedCustomData = [:]
        def app3 = createApplication(postedCustomData)
        updateApplication(app3, postedCustomData, [:])
        updateApplication(app3, postedCustomData, createDataForUpdate())

        //TEST Tenant
        postedCustomData = createComplexData()
        initializeTenantCustomData(postedCustomData)
        def tenant = client.currentTenant
        updateTenant(tenant, postedCustomData, createDataForUpdate())
        updateTenant(tenant, postedCustomData, [:])

        postedCustomData = [:]
        initializeTenantCustomData(postedCustomData)
        updateTenant(tenant, postedCustomData, [:])
        updateTenant(tenant, postedCustomData, createDataForUpdate())

        cleanTenantCustomData()

        application = null
    }

    def Account createAccount(Map postedCustomData, boolean expand) {

        def account = newAccountData()

        account.customData.putAll(postedCustomData)

        def builder = Accounts.newCreateRequestFor(account)

        builder = expand ? builder.withResponseOptions(Accounts.options().withCustomData()) : builder

        application.createAccount(builder.build());

        assertValidCustomData(account.href + "/customData", postedCustomData, account.customData, expand)

        return account
    }

    def Group createGroup(Map postedCustomData, boolean expand) {
        def group = newGroupData()

        group.customData.putAll(postedCustomData)

        def builder = Groups.newCreateRequestFor(group)

        builder = expand ? builder.withResponseOptions(Groups.options().withCustomData()) : builder

        application.createGroup(builder.build())

        deleteOnTeardown(group)

        assertValidCustomData(group.href + "/customData", postedCustomData, group.customData, expand)

        return group
    }

    def Directory createDirectory(Map postedCustomData) {
        def dir = newDirectoryData()

        dir.customData.putAll(postedCustomData)

        client.createDirectory(dir)

        deleteOnTeardown(dir)

        assertValidCustomData(dir.href + "/customData", postedCustomData, dir.customData, false)

        return dir
    }

    def Application createApplication(Map postedCustomData) {
        def app = newApplicationData()

        app.customData.putAll(postedCustomData)

        def builder = Applications.newCreateRequestFor(app)

        client.createApplication(builder.build())

        deleteOnTeardown(app)

        assertValidCustomData(app.href + "/customData", postedCustomData, app.customData, false)

        return app
    }

    def void initializeTenantCustomData(Map postedCustomData) {
        cleanTenantCustomData()

        def tenant = client.currentTenant

        tenant.customData.putAll(postedCustomData)

        tenant.save()

        assertValidCustomData(tenant.href + "/customData", postedCustomData, tenant.customData, false)
    }

    def cleanTenantCustomData() {
        client.currentTenant.customData.delete()
    }

}
