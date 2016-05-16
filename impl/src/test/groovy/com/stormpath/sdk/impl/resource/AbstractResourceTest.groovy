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
package com.stormpath.sdk.impl.resource

import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import java.lang.reflect.Method

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.fail

/**
 * @since 0.4.1
 */
class AbstractResourceTest {

    /**
     * Asserts that the InternalDataStore is not invoked if the dirty properties can satisfy a getProperty request.
     */
    @Test
    void testNonMaterializedResourceGetDirtyPropertyDoesNotMaterialize() {

        def props = ['href': 'http://foo.com/test/123']
        InternalDataStore ds = createStrictMock(InternalDataStore)

        replay ds

        TestResource resource = new TestResource(ds, props)

        resource.setName('New Value')

        assertEquals 'New Value', resource.getName()

        verify ds
    }

    @Test
    void testDirtyPropertiesRetainedAfterMaterialization() {

        def props = ['href': 'http://foo.com/test/123']
        InternalDataStore ds = createStrictMock(InternalDataStore)

        def serverResource = new TestResource(ds, [
                'href': props.href,
                'name': 'Old Name',
                'description': 'Old Description'
        ])

        expect(ds.getResource(props.href, TestResource)).andReturn serverResource

        replay ds

        TestResource resource = new TestResource(ds, props)
        resource.setName('New Name')

        //get the description, which hasn't been set locally - this will force materialization:
        String description = resource.getDescription()

        assertEquals 'Old Description', description //obtained during materialization
        assertEquals 'New Name', resource.getName() //dirty property retained even after materialization

        verify ds
    }

    /**
     * @since 1.0.RC4
     */
    @Test
    void testMapProperty() {

        def props =[
                href: "https://api.stormpath.com/v1/emailTemplates/3HEMybJjMO2za0YmfTubDI",
                name: "Default Password Reset Email Template",
                description: "This is the password reset email template that is associated with the directory",
                fromName: "Test Name",
                fromEmailAddress: "test@stormpath.com",
                subject: "Reset your Password",
                textBody: "Forgot your password?\n\nWe've received a request to reset the password for this email address.",
                htmlBody: "<p>Forgot your password?</p><br/><br/><p>We've received a request to reset the password for this email address.",
                mimeType: "text/plain",
                defaultModel: [
                    linkBaseUrl: "https://api.stormpath.com/passwordReset"
                ]
        ]
        InternalDataStore ds = createStrictMock(InternalDataStore)

        def testResource = new TestResource(ds, props)


        Method method = AbstractResource.getDeclaredMethod("getMapProperty", String)
        method.setAccessible(true)

        Object[] parameters = new Object[1]
        parameters[0] = "defaultModel"

        assertEquals(((Map) method.invoke(testResource, parameters)).get('linkBaseUrl'), "https://api.stormpath.com/passwordReset")

        parameters[0] = "nonExistentMapProperty"
        assertNull(method.invoke(testResource, parameters))

        parameters[0] = "name"
        try {
            method.invoke(testResource, parameters)
            fail("Should have thrown")
        } catch (Exception e) {
            assertEquals(e.getCause().toString(), "java.lang.IllegalArgumentException: 'name' property value type does not match the specified type. Specified type: Map. Existing type: java.lang.String.  Value: Default Password Reset Email Template")
        }
    }

}
