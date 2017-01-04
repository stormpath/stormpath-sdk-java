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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.AccountCreationPolicy
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.mail.DefaultModeledEmailTemplateList
import com.stormpath.sdk.impl.mail.DefaultUnmodeledEmailTemplateList
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.EnumProperty
import com.stormpath.sdk.mail.EmailStatus
import com.stormpath.sdk.mail.ModeledEmailTemplateList
import com.stormpath.sdk.mail.UnmodeledEmailTemplateList
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC4.5
 */
class DefaultAccountCreationPolicyTest {

    @Test
    void testGetPropertyDescriptors() {

        DefaultAccountCreationPolicy accountCreationPolicy = new DefaultAccountCreationPolicy(createStrictMock(InternalDataStore))
        def propertyDescriptors = accountCreationPolicy.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 6)
        assertTrue(propertyDescriptors.get("verificationEmailStatus") instanceof EnumProperty<EmailStatus>)
        assertTrue(propertyDescriptors.get("verificationSuccessEmailStatus") instanceof EnumProperty<EmailStatus>)
        assertTrue(propertyDescriptors.get("welcomeEmailStatus") instanceof EnumProperty<EmailStatus>)
        assertTrue(propertyDescriptors.get("verificationEmailTemplates") instanceof CollectionReference)
        assertTrue(propertyDescriptors.get("verificationSuccessEmailTemplates") instanceof CollectionReference)
        assertTrue(propertyDescriptors.get("welcomeEmailTemplates") instanceof CollectionReference)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e",
                verificationEmailStatus: "ENABLED",
                verificationSuccessEmailStatus: "ENABLED",
                welcomeEmailStatus: "Enabled",
                verificationEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/verificationEmailTemplates"],
                verificationSuccessEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/verificationSuccessEmailTemplates"],
                welcomeEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/welcomeEmailTemplates"]
        ]

        expect(internalDataStore.instantiate(ModeledEmailTemplateList, properties.verificationEmailTemplates)).
                andReturn(new DefaultModeledEmailTemplateList(internalDataStore, properties.verificationEmailTemplates))

        expect(internalDataStore.instantiate(UnmodeledEmailTemplateList, properties.verificationSuccessEmailTemplates)).
                andReturn(new DefaultUnmodeledEmailTemplateList(internalDataStore, properties.verificationSuccessEmailTemplates))

        expect(internalDataStore.instantiate(UnmodeledEmailTemplateList, properties.welcomeEmailTemplates)).
                andReturn(new DefaultUnmodeledEmailTemplateList(internalDataStore, properties.welcomeEmailTemplates))

        replay internalDataStore

        AccountCreationPolicy accountCreationPolicy = new DefaultAccountCreationPolicy(internalDataStore, properties)

        accountCreationPolicy = accountCreationPolicy.setVerificationEmailStatus(EmailStatus.DISABLED)
                .setVerificationSuccessEmailStatus(EmailStatus.DISABLED)
                .setWelcomeEmailStatus(EmailStatus.DISABLED)

        assertEquals(accountCreationPolicy.getVerificationEmailStatus(), EmailStatus.DISABLED)
        assertEquals(accountCreationPolicy.getVerificationSuccessEmailStatus(), EmailStatus.DISABLED)
        assertEquals(accountCreationPolicy.getWelcomeEmailStatus(), EmailStatus.DISABLED)
        assertEquals(accountCreationPolicy.getHref(), properties.href)

        def verificationTemplates = accountCreationPolicy.getAccountVerificationEmailTemplates()
        assertTrue(verificationTemplates instanceof DefaultModeledEmailTemplateList && verificationTemplates.getHref().equals(properties.verificationEmailTemplates.href))

        def verificationSucessTemplates = accountCreationPolicy.getAccountVerificationSuccessEmailTemplates()
        assertTrue(verificationSucessTemplates instanceof UnmodeledEmailTemplateList && verificationSucessTemplates.getHref().equals(properties.verificationSuccessEmailTemplates.href))

        def welcomeTemplates = accountCreationPolicy.getWelcomeEmailTemplates()
        assertTrue(welcomeTemplates instanceof UnmodeledEmailTemplateList && welcomeTemplates.getHref().equals(properties.welcomeEmailTemplates.href))

        verify internalDataStore

    }

    @Test
    void testSetNullStatus() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e",
                verificationEmailStatus: "ENABLED",
                verificationSuccessEmailStatus: "ENABLED",
                welcomeEmailStatus: "Enabled",
                verificationEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/verificationEmailTemplates"],
                verificationSuccessEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/verificationSuccessEmailTemplates"],
                welcomeEmailTemplates: [href: "https://api.stormpath.com/v1/accountCreationPolicies/35YM3OwioW9PVtfLOh6q1e/welcomeEmailTemplates"]
        ]

        AccountCreationPolicy accountCreationPolicy = new DefaultAccountCreationPolicy(internalDataStore, properties)

        try {
            accountCreationPolicy.setVerificationEmailStatus(null); //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "accountVerificationEmailStatus cannot be null.")
        }

        try {
            accountCreationPolicy.setVerificationSuccessEmailStatus(null); //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "accountVerificationSuccessEmailStatus cannot be null.")
        }

        try {
            accountCreationPolicy.setWelcomeEmailStatus(null); //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "welcomeEmailStatus cannot be null.")
        }
    }



}
