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
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.directory.CustomData
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 0.9
 */
class CustomDataEntityIT extends AbstractCustomDataIT {

    Application application

    @Test
    void testCustomDataOperations() {

        application = createApplication()

        def account = application.createAccount(newAccountData())

        CustomData customData = account.customData

        testCRUD(customData)

        def group = application.createGroup(newGroupData())

        customData = group.customData

        testCRUD(customData)

        deleteOnTeardown(account.directory)

        deleteOnTeardown(account)

        deleteOnTeardown(group)

        deleteOnTeardown(application)

    }

    private testCRUD(CustomData customData) {

        //TEST CREATE
        Map postedProperties = createComplexData()

        customData.putAll(postedProperties)

        customData.save()

        assertValidCustomData(customData.getHref(), postedProperties, customData)

        //TEST READ
        def customDataHref = customData.getHref()

        customData = client.getResource(customDataHref, CustomData)

        assertValidCustomData(customData.getHref(), postedProperties, customData)

        //TEST UPDATE
        def updateCustomDataProperties = createDataForUpdate()

        for (Map.Entry objectEntry : updateCustomDataProperties.entrySet()) {

            Object object = updateCustomDataProperties.get(objectEntry.key)

            customData.put(objectEntry.key, object)
        }

        customData.save()

        postedProperties.putAll(updateCustomDataProperties)

        assertValidCustomData(customDataHref, postedProperties, customData)

        //TEST DELETE SINGLE PROPERTIES
        def propertiesToDelete = updateCustomDataProperties.keySet()

        for (String propertyName : propertiesToDelete) {

            customData.remove(propertyName)

            postedProperties.remove(propertyName)

        }

        customData.save()

        //TEST DELETE SINGLE PROPERTIES & UPDATE OTHERS

        customData = client.getResource(customDataHref, CustomData)

        propertiesToDelete = createSetOfPropertiesToDelete();

        for (String propertyName : propertiesToDelete) {

            customData.remove(propertyName)

            postedProperties.remove(propertyName)

        }

        assertValidCustomData(customData.getHref(), postedProperties, customData)

        updateCustomDataProperties = createDataForUpdate()

        for (Map.Entry objectEntry : updateCustomDataProperties.entrySet()) {

            Object object = updateCustomDataProperties.get(objectEntry.key)

            customData.put(objectEntry.key, object)
        }

        postedProperties.putAll(updateCustomDataProperties)

        customData.save()

        assertValidCustomData(customData.getHref(), postedProperties, customData)

        //TEST DELETE ALL PROPERTIES

        customData.clear()

        postedProperties = ["uniqueString" : uniquify("my unique string")]

        customData.putAll(postedProperties)

        customData.save()

        customData = client.getResource(customDataHref, CustomData)

        assertValidCustomData(customData.getHref(), postedProperties, customData)

        //TEST DELETE OBJECT.
        customData.delete()

        customData = client.getResource(customDataHref, CustomData)

        assertValidCustomData(customData.getHref(), [:], customData)
    }

    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/30">Issue #30</a>
     */
    @Test
    void testCustomDataCacheUpdated() {

        //create temp account:
        def app = createTempApp()
        def account = app.createAccount(newAccountData())
        def href = account.href

        //for the purposes of this test, we want a new client, using caching, completely separate from anything else:
        Client client2 = buildClient(true);

        //1. Get the account's custom data:
        account = client2.getResource(href, Account)
        def customData = account.getCustomData()

        assertNull customData.hobby //no data yet

        //2. Update the custom data value:
        customData.hobby = 'Reading'
        customData.save()

        //3. Get the custom data again and assert that the value is there:
        customData = client2.getResource(customData.getHref(), CustomData)
        assertEquals customData.hobby, 'Reading'

        //4. Get the account again and assert that the value is there:
        account = client2.getResource(href, Account)
        customData = account.customData
        assertEquals customData.hobby, 'Reading'

        //5. Change the value:
        customData.hobby = 'Kendo'
        customData.save()

        //6. Get the custom data again and assert that the value is there:
        customData = client2.getResource(customData.getHref(), CustomData)
        assertEquals customData.hobby, 'Kendo'

        //7. Get the account again and assert that the value is there:
        account = client2.getResource(account.getHref(), Account)
        assertEquals account.customData.hobby, 'Kendo'

        deleteOnTeardown(account)
    }

    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/30">Issue #30</a>
     */
    @Test
    void testCustomDataCacheUpdatedViaParentSave() {

        //create temp account:
        def app = createTempApp()
        def account = app.createAccount(newAccountData())
        def href = account.href

        //for the purposes of this test, we want a new client, using caching, completely separate from anything else:
        Client client2 = buildClient(true);

        //1. Get the account
        account = client2.getResource(href, Account)
        assertNull account.getCustomData().hobby //no data yet

        //2. Update the custom data value:
        account.customData.hobby = 'Reading'
        account.save()

        //3. Get the account again and assert that the value is there:
        account = client2.getResource(href, Account)
        assertEquals account.customData.hobby, 'Reading'

        //4. Change the value:
        account.customData.hobby = 'Kendo'
        account.save()

        //5. Get the account again and assert that the value is there:
        account = client2.getResource(href, Account)
        assertEquals account.customData.hobby, 'Kendo'
    }

}
