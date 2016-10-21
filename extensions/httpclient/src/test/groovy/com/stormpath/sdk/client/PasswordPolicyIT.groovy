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
package com.stormpath.sdk.client

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.mail.ModeledEmailTemplate
import com.stormpath.sdk.mail.UnmodeledEmailTemplate
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC4
 */
class PasswordPolicyIT extends ClientIT {

    @Test
    void testStrength() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testStrength")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        def strength = dir.getPasswordPolicy().getStrength()
        assertNotNull strength.getHref()
        assertEquals strength.getMinSymbol(), 0
        assertEquals strength.getMinDiacritic(), 0
        assertEquals strength.getMinUpperCase(), 1
        assertEquals strength.getMinLength(), 8
        assertEquals strength.getMinLowerCase(), 1
        assertEquals strength.getMaxLength(), 100
        assertEquals strength.getMinNumeric(), 1
        assertTrue strength.getHref().endsWith("strength") //the href could have wrongly changed after materialization

        strength.setMinSymbol(1).setMinDiacritic(2).setMinUpperCase(3)
                .setMinLength(4).setMinLowerCase(5).setMaxLength(20).setMinNumeric(6)
        strength.save()
        assertEquals strength.getMinSymbol(), 1
        assertEquals strength.getMinDiacritic(), 2
        assertEquals strength.getMinUpperCase(), 3
        assertEquals strength.getMinLength(), 4
        assertEquals strength.getMinLowerCase(), 5
        assertEquals strength.getMaxLength(), 20
        assertEquals strength.getMinNumeric(), 6

    }

    @Test
    void testResetEmailTemplateList() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testResetEmailTemplateList")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        def templates = dir.getPasswordPolicy().getResetEmailTemplates()
        assertNotNull templates.getHref()
        assertEquals templates.getLimit(), 25
        assertEquals templates.getOffset(), 0
        assertTrue templates.getHref().endsWith("resetEmailTemplates") //the href could have wrongly changed after materialization

        def emailTemplate = templates.iterator().next()
        assertNotNull emailTemplate.href
        assertTrue(emailTemplate.getLinkBaseUrl().endsWith("/passwordReset"))
        assertEquals(emailTemplate.getDefaultModel(), [linkBaseUrl : emailTemplate.getLinkBaseUrl()])
        assertEquals(emailTemplate.getSubject(), "Reset your Password")
        assertEquals(emailTemplate.getName(), "Default Password Reset Email Template")

        emailTemplate.setSubject("New Reset your Password Subject")
        emailTemplate.setName("New Default Password Reset Email Template Name")
        emailTemplate.setLinkBaseUrl("https://api.stormpath.com/newPasswordReset")

        emailTemplate.save()

        dir = client.getResource(dir.href, Directory.class)
        def emailTemplateFromDir = dir.getPasswordPolicy().getResetEmailTemplates().iterator().next()
        emailTemplate = client.getResource(emailTemplate.href, ModeledEmailTemplate.class)
        assertEquals(emailTemplate.href, emailTemplateFromDir.href)
        assertEquals(emailTemplate.getSubject(), "New Reset your Password Subject")
        assertEquals(emailTemplate.getName(), "New Default Password Reset Email Template Name")
        assertEquals(emailTemplate.getLinkBaseUrl(), "https://api.stormpath.com/newPasswordReset")
    }


    @Test
    void testResetSuccessEmailTemplateList() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testResetSuccessEmailTemplateList")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        def templates = dir.getPasswordPolicy().getResetSuccessEmailTemplates()
        assertNotNull templates.getHref()
        assertEquals templates.getLimit(), 25
        assertEquals templates.getOffset(), 0
        assertTrue templates.getHref().endsWith("resetSuccessEmailTemplates") //the href could have wrongly changed after materialization

        def emailTemplate = templates.iterator().next()
        assertNotNull emailTemplate.href
        assertEquals(emailTemplate.getSubject(), "Your password has been changed")
        assertEquals(emailTemplate.getName(), "Default Password Reset Success Email Template")

        emailTemplate.setSubject("New Your password has been changed Subject")
        emailTemplate.setName("New Default Password Reset Success Email Template Name")

        emailTemplate.save()

        dir = client.getResource(dir.href, Directory.class)
        def emailTemplateFromDir = dir.getPasswordPolicy().getResetSuccessEmailTemplates().iterator().next()
        emailTemplate = client.getResource(emailTemplate.href, UnmodeledEmailTemplate.class)
        assertEquals(emailTemplate.href, emailTemplateFromDir.href)
        assertEquals(emailTemplate.getSubject(), "New Your password has been changed Subject")
        assertEquals(emailTemplate.getName(), "New Default Password Reset Success Email Template Name")
    }

}
