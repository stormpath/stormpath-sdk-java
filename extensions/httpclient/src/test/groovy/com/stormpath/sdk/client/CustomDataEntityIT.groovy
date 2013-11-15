package com.stormpath.sdk.client

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.directory.CustomData
import org.testng.annotations.Test

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

        customData = client.getResource(customDataHref, CustomData)

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

}
