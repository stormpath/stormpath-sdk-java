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
package com.stormpath.sdk.client

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.mail.ModeledEmailTemplate
import com.stormpath.sdk.mail.UnmodeledEmailTemplate
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 1.0.RC4.5
 */
class AccountCreationPolicyIT extends ClientIT {

    @Test
    void testAccountVerificationEmailTemplateList() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testAccountVerificationEmailTemplate")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        assertNotNull dir.href

        def templates = dir.getAccountCreationPolicy().getAccountVerificationEmailTemplates()

        assertNotNull templates

        assertNotNull templates.getHref()
        assertEquals templates.getLimit(), 25
        assertEquals templates.getOffset(), 0
        assertTrue templates.getHref().endsWith("verificationEmailTemplates") //the href could have wrongly changed after materialization

        def emailTemplate = templates.iterator().next()
        assertNotNull emailTemplate.href
        assertTrue(emailTemplate.getLinkBaseUrl().endsWith("/emailVerificationTokens"))
        assertEquals(emailTemplate.getDefaultModel(), [linkBaseUrl: emailTemplate.getLinkBaseUrl()])
        assertEquals(emailTemplate.getSubject(), "Verify your account")
        assertEquals(emailTemplate.getName(), "Default Verification Email Template")

        emailTemplate.setSubject("New Verify your account Subject")
        emailTemplate.setName("New Default Verification Email Template Name")
        emailTemplate.setLinkBaseUrl("https://api.stormpath.com/newEmailVerificationTokens")

        emailTemplate.save()

        dir = client.getResource(dir.href, Directory.class)
        def emailTemplateFromDir = dir.getAccountCreationPolicy().getAccountVerificationEmailTemplates().iterator().next()
        emailTemplate = client.getResource(emailTemplate.href, ModeledEmailTemplate.class)
        assertEquals(emailTemplate.href, emailTemplateFromDir.href)
        assertEquals(emailTemplate.getSubject(), "New Verify your account Subject")
        assertEquals(emailTemplate.getName(), "New Default Verification Email Template Name")
        assertEquals(emailTemplate.getLinkBaseUrl(), "https://api.stormpath.com/newEmailVerificationTokens")
    }
    
    
    @Test
    void testAccountVerificationSuccessEmailTemplateList() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testAccountVerificationSuccessEmailTemplate")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        assertNotNull dir.href

        def templates = dir.getAccountCreationPolicy().getAccountVerificationSuccessEmailTemplates()

        assertNotNull templates

        assertNotNull templates.getHref()
        assertEquals templates.getLimit(), 25
        assertEquals templates.getOffset(), 0
        assertTrue templates.getHref().endsWith("verificationSuccessEmailTemplates") //the href could have wrongly changed after materialization

        def emailTemplate = templates.iterator().next()
        assertNotNull emailTemplate.href
        assertEquals(emailTemplate.getSubject(), "Your account has been confirmed")
        assertEquals(emailTemplate.getName(), "Default Verification Success Email Template")

        emailTemplate.setSubject("New Your account has been confirmed Subject")
        emailTemplate.setName("New Default Verification Success Email Template Name")

        emailTemplate.save()

        dir = client.getResource(dir.href, Directory.class)
        def emailTemplateFromDir = dir.getAccountCreationPolicy().getAccountVerificationSuccessEmailTemplates().iterator().next()
        emailTemplate = client.getResource(emailTemplate.href, UnmodeledEmailTemplate.class)
        assertEquals(emailTemplate.href, emailTemplateFromDir.href)
        assertEquals(emailTemplate.getSubject(), "New Your account has been confirmed Subject")
        assertEquals(emailTemplate.getName(), "New Default Verification Success Email Template Name")
    }

    @Test
    void testWelcomeEmailTemplateList() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testWelcomeEmailTemplate")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        def templates = dir.getAccountCreationPolicy().getWelcomeEmailTemplates()
        assertNotNull templates.getHref()
        assertEquals templates.getLimit(), 25
        assertEquals templates.getOffset(), 0
        assertTrue templates.getHref().endsWith("welcomeEmailTemplates") //the href could have wrongly changed after materialization

        def emailTemplate = templates.iterator().next()
        assertNotNull emailTemplate.href
        assertEquals(emailTemplate.getSubject(), "Your registration was successful")
        assertEquals(emailTemplate.getName(), "Default Welcome Email Template")

        emailTemplate.setSubject("New Your registration was successful Subject")
        emailTemplate.setName("New Default Welcome Email Template Name")

        emailTemplate.save()

        dir = client.getResource(dir.href, Directory.class)
        def emailTemplateFromDir = dir.getAccountCreationPolicy().getWelcomeEmailTemplates().iterator().next()
        emailTemplate = client.getResource(emailTemplate.href, UnmodeledEmailTemplate.class)
        assertEquals(emailTemplate.href, emailTemplateFromDir.href)
        assertEquals(emailTemplate.getSubject(), "New Your registration was successful Subject")
        assertEquals(emailTemplate.getName(), "New Default Welcome Email Template Name")
    }

}