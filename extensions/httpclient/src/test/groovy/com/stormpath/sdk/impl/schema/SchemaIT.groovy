/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.schema

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.schema.Field
import com.stormpath.sdk.schema.FieldList
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * @since 1.2.0
 */
class SchemaIT extends ClientIT {

    Application application
    Directory directory
    AccountStoreMapping accountStoreMapping

    @BeforeMethod
    void setup() {
        application = createTempApp()

        directory = client.instantiate(Directory)
        directory.name = uniquify('JSDK.SchemaIT.test')
        directory = client.createDirectory(directory)
        deleteOnTeardown(directory)
        AccountStoreMapping accountStoreMapping = application.addAccountStore(directory)
        accountStoreMapping.setDefaultAccountStore(true)
        accountStoreMapping.save()
    }

    @Test
    void testDirectoryDefaultSchema() {
        def accountSchema = directory.getAccountSchema()
        FieldList fields = accountSchema.getFields()

        assertEquals(2, fields.size)

        def defaultFields = [givenName: false, surname: false]

        for (Field field : fields) {
            assertEquals defaultFields.get(field.name), field.required
        }
    }

    @Test
    void testUpdateGivenNameFieldToRequiredAndBack() {
        def accountSchema = directory.getAccountSchema();

        String fieldHref = ""

        accountSchema.fields.each { field ->
            if ("givenName".equals(field.name)) {
                fieldHref = field.href
                field.required = true
                field.save()
            }
        }

        Field givenNameField = client.getResource(fieldHref, Field)
        assertTrue(givenNameField.required)

        givenNameField.required = false
        givenNameField.save()

        assertFalse(client.getResource(fieldHref, Field).required)
    }

    @Test
    void testUpdateSurnameFieldToRequired() {
        def accountSchema = directory.getAccountSchema();

        String fieldHref = ""

        accountSchema.fields.each { field ->
            if ("surname".equals(field.name)) {
                fieldHref = field.href
                field.required = true
                field.save()
            }
        }

        Field surenameField = client.getResource(fieldHref, Field)
        assertTrue(surenameField.required)

        surenameField.required = false
        surenameField.save()

        assertFalse(client.getResource(fieldHref, Field).required)
    }
}
