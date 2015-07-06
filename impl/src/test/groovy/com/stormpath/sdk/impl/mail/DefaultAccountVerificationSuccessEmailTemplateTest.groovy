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
package com.stormpath.sdk.impl.mail

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.mail.EmailTemplate
import com.stormpath.sdk.mail.MimeType
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC4.5
 */
class DefaultAccountVerificationSuccessEmailTemplateTest {

    @Test
    void testGetPropertyDescriptors() {

        EmailTemplate emailTemplate = new DefaultUnmodeledEmailTemplate(createStrictMock(InternalDataStore))

        def propertyDescriptors = emailTemplate.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 8)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fromName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fromEmailAddress") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("subject") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("textBody") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("htmlBody") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("mimeType") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/emailTemplates/3PCjpMa5kihBOo1eO8L6z5",
                name: "Test Email",
                description: "Test Description",
                fromName: "John Doe",
                fromEmailAddress: "john@email.com",
                subject: "Your account has been confirmed",
                textBody: "Your account has been successfully verified",
                htmlBody: "Your account has been <b>successfully</b> verified",
                mimeType: "text/plain"
        ]

        EmailTemplate emailTemplate = new DefaultUnmodeledEmailTemplate(internalDataStore, properties)
        assertEquals(emailTemplate.getName(), "Test Email")
        assertEquals(emailTemplate.getDescription(), "Test Description")
        assertEquals(emailTemplate.getFromName(), "John Doe")
        assertEquals(emailTemplate.getFromEmailAddress(), "john@email.com")
        assertEquals(emailTemplate.getSubject(), "Your account has been confirmed")
        assertEquals(emailTemplate.getTextBody(), "Your account has been successfully verified")
        assertEquals(emailTemplate.getHtmlBody(), "Your account has been <b>successfully</b> verified")
        assertEquals(emailTemplate.getMimeType(), MimeType.PLAIN_TEXT)

        emailTemplate = emailTemplate.setName("New Email Name")
                .setDescription("My New Description")
                .setFromName("New John Doe")
                .setFromEmailAddress("john@newemail.com")
                .setSubject("New Your account has been confirmed")
                .setTextBody("New Your account has been successfully verified")
                .setHtmlBody("New Your account has been <b>successfully</b> verified")
                .setMimeType(MimeType.HTML)

        assertEquals(emailTemplate.getName(), "New Email Name")
        assertEquals(emailTemplate.getDescription(), "My New Description")
        assertEquals(emailTemplate.getFromName(), "New John Doe")
        assertEquals(emailTemplate.getFromEmailAddress(), "john@newemail.com")
        assertEquals(emailTemplate.getSubject(), "New Your account has been confirmed")
        assertEquals(emailTemplate.getTextBody(), "New Your account has been successfully verified")
        assertEquals(emailTemplate.getHtmlBody(), "New Your account has been <b>successfully</b> verified")
        assertEquals(emailTemplate.getMimeType(), MimeType.HTML)
    }
}
