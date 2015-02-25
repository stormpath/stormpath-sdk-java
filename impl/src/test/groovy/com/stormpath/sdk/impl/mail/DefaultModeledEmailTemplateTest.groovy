/*
* Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.mail

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.MapProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.mail.EmailTemplate
import com.stormpath.sdk.mail.MimeType
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertSame
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.RC4
 */
class DefaultModeledEmailTemplateTest {

    @Test
    void testGetPropertyDescriptors() {

        EmailTemplate emailTemplate = new DefaultModeledEmailTemplate(createStrictMock(InternalDataStore))

        def propertyDescriptors = emailTemplate.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 9)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fromName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fromEmailAddress") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("subject") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("textBody") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("htmlBody") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("mimeType") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("defaultModel") instanceof MapProperty)
    }

    @Test
    void testMethods() {

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/emailTemplates/3PCjpMa5kihBOo1eO8L6z5",
                          name: "My Email",
                          description: "My Description",
                          fromName: "John Doe",
                          fromEmailAddress: "joe@email.com",
                          subject: "Your password has been changed",
                          textBody: "Your password has been successfully changed",
                          htmlBody: "Your password has been <b>successfully</b> changed",
                          mimeType: "text/plain",
                          defaultModel: [linkBaseUrl : "http://localhost:9191/passwordReset"]
        ]

        def emailTemplate = new DefaultModeledEmailTemplate(internalDataStore, properties)
        assertEquals(emailTemplate.getName(), "My Email")
        assertEquals(emailTemplate.getDescription(), "My Description")
        assertEquals(emailTemplate.getFromName(), "John Doe")
        assertEquals(emailTemplate.getFromEmailAddress(), "joe@email.com")
        assertEquals(emailTemplate.getSubject(), "Your password has been changed")
        assertEquals(emailTemplate.getTextBody(), "Your password has been successfully changed")
        assertEquals(emailTemplate.getHtmlBody(), "Your password has been <b>successfully</b> changed")
        assertEquals(emailTemplate.getMimeType(), MimeType.PLAIN_TEXT)
        assertEquals(emailTemplate.getDefaultModel(), properties.defaultModel)

        emailTemplate = emailTemplate.setName("New Email Name")
                .setDescription("My New Description")
                .setFromName("John Doe Jr.")
                .setFromEmailAddress("joejr@newemail.com")
                .setSubject("Your password has been reset.")
                .setTextBody("Your password has been successfully reset.")
                .setHtmlBody("Your password has been <b>successfully</b> reset.")
                .setMimeType(MimeType.HTML)
                .setLinkBaseUrl("http://localhost:8080/newPasswordReset")

        assertEquals(emailTemplate.getName(), "New Email Name")
        assertEquals(emailTemplate.getDescription(), "My New Description")
        assertEquals(emailTemplate.getFromName(), "John Doe Jr.")
        assertEquals(emailTemplate.getFromEmailAddress(), "joejr@newemail.com")
        assertEquals(emailTemplate.getSubject(), "Your password has been reset.")
        assertEquals(emailTemplate.getTextBody(), "Your password has been successfully reset.")
        assertEquals(emailTemplate.getHtmlBody(), "Your password has been <b>successfully</b> reset.")
        assertEquals(emailTemplate.getMimeType(), MimeType.HTML)
        assertEquals(emailTemplate.getLinkBaseUrl(), "http://localhost:8080/newPasswordReset")
        assertEquals(emailTemplate.getDefaultModel(), [linkBaseUrl : "http://localhost:8080/newPasswordReset"])

        Map defaultModel = emailTemplate.getDefaultModel()
        assertEquals(defaultModel.size(), 1)
        assertSame(emailTemplate.getLinkBaseUrl(), defaultModel.get("linkBaseUrl"))
        emailTemplate.setLinkBaseUrl("http://mycompany.com/resetEmail.html")
        assertEquals(defaultModel.get("linkBaseUrl"), "http://mycompany.com/resetEmail.html")
    }

    @Test
    void testModifiedDefaultModel() {

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/emailTemplates/3PCjpMa5kihBOo1eO8L6z5",
                          name: "My Email",
                          description: "My Description",
                          fromName: "John Doe",
                          fromEmailAddress: "joe@email.com",
                          subject: "Your password has been changed",
                          textBody: "Your password has been successfully changed",
                          htmlBody: "Your password has been <b>successfully</b> changed",
                          mimeType: "text/plain",
                          defaultModel: [linkBaseUrl : "http://localhost:9191/passwordReset"]
        ]

        def emailTemplate = new DefaultModeledEmailTemplate(internalDataStore, properties)

        Map defaultModel = emailTemplate.getDefaultModel()
        assertEquals(defaultModel.size(), 1)
        defaultModel.put("newKey", "newValue")
        assertEquals(defaultModel.size(), 2)
        defaultModel.put("linkBaseUrl", "http://mycompany.com/resetEmail.html")
        assertEquals(defaultModel.size(), 2)

        try {
            defaultModel.remove("linkBaseUrl")
            emailTemplate.save()
            fail("Should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "The defaultModel map must contain the 'linkBasedUrl' reserved property.")
        }
    }

    @Test
    void testMimeTypeNull() {

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/emailTemplates/3PCjpMa5kihBOo1eO8L6z5",
                          name: "My Email",
                          description: "My Description",
                          fromName: "John Doe",
                          fromEmailAddress: "joe@email.com",
                          subject: "Your password has been changed",
                          textBody: "Your password has been successfully changed",
                          htmlBody: "Your password has been <b>successfully</b> changed",
                          mimeType: "text/plain",
                          defaultModel: [linkBaseUrl : "http://localhost:9191/passwordReset"]
        ]

        def emailTemplate = new DefaultModeledEmailTemplate(internalDataStore, properties)

        try {
            emailTemplate.setMimeType(null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
        }

    }

}
