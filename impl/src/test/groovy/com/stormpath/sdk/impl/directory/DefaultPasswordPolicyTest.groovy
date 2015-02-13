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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.PasswordPolicy
import com.stormpath.sdk.directory.Strength
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.mail.DefaultResetEmailTemplateList
import com.stormpath.sdk.impl.mail.DefaultResetSuccessEmailTemplateList
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.IntegerProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.mail.EmailStatus
import com.stormpath.sdk.mail.ResetEmailTemplateList
import com.stormpath.sdk.mail.ResetSuccessEmailTemplateList
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.0
 */
class DefaultPasswordPolicyTest {

    @Test
    void testGetPropertyDescriptors() {

        PasswordPolicy passwordPolicy = new DefaultPasswordPolicy(createStrictMock(InternalDataStore))

        def propertyDescriptors = passwordPolicy.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 6)

        assertTrue(propertyDescriptors.get("resetTokenTtl") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("resetEmailStatus") instanceof StatusProperty<EmailStatus>)
        assertTrue(propertyDescriptors.get("resetSuccessEmailStatus") instanceof StatusProperty<EmailStatus>)
        assertTrue(propertyDescriptors.get("strength") instanceof ResourceReference)
        assertTrue(propertyDescriptors.get("resetEmailTemplates") instanceof CollectionReference)
        assertTrue(propertyDescriptors.get("resetSuccessEmailTemplates") instanceof CollectionReference)
    }


    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                resetTokenTtl: 24,
                resetEmailStatus: "ENABLED",
                resetSuccessEmailStatus: "ENABLED",
                strength: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/strength"],
                resetEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetEmailTemplates"],
                resetSuccessEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetSuccessEmailTemplates"]
        ]

        expect(internalDataStore.instantiate(Strength, properties.strength)).
            andReturn(new DefaultStrength(internalDataStore, properties.strength))

        expect(internalDataStore.instantiate(ResetEmailTemplateList, properties.resetEmailTemplates)).
                andReturn(new DefaultResetEmailTemplateList(internalDataStore, properties.resetEmailTemplates))

        expect(internalDataStore.instantiate(ResetSuccessEmailTemplateList, properties.resetSuccessEmailTemplates)).
                andReturn(new DefaultResetSuccessEmailTemplateList(internalDataStore, properties.resetSuccessEmailTemplates))

        replay internalDataStore

        PasswordPolicy passwordPolicy = new DefaultPasswordPolicy(internalDataStore, properties)
        assertEquals(passwordPolicy.getResetTokenTtl(), 24)

        passwordPolicy = passwordPolicy.setResetTokenTtl(100)
                .setResetEmailStatus(EmailStatus.DISABLED)
                .setResetSuccessEmailStatus(EmailStatus.DISABLED)

        assertEquals(passwordPolicy.getResetTokenTtl(), 100)
        assertEquals(passwordPolicy.getResetEmailStatus(), EmailStatus.DISABLED)
        assertEquals(passwordPolicy.getResetSuccessEmailStatus(), EmailStatus.DISABLED)
        assertEquals(passwordPolicy.getHref(), properties.href)

        def resource = passwordPolicy.getStrength()
        assertTrue(resource instanceof DefaultStrength && resource.getHref().equals(properties.strength.href))

        resource = passwordPolicy.getResetEmailTemplates()
        assertTrue(resource instanceof DefaultResetEmailTemplateList && resource.getHref().equals(properties.resetEmailTemplates.href))

        resource = passwordPolicy.getResetSuccessEmailTemplates()
        assertTrue(resource instanceof DefaultResetSuccessEmailTemplateList && resource.getHref().equals(properties.resetSuccessEmailTemplates.href))

        verify internalDataStore

    }


    @Test
    void testSetNegativeTTL() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                          resetTokenTtl: 24,
                          resetEmailStatus: "ENABLED",
                          resetSuccessEmailStatus: "ENABLED",
                          strength: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/strength"],
                          resetEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetEmailTemplates"],
                          resetSuccessEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetSuccessEmailTemplates"]
        ]

        PasswordPolicy passwordPolicy = new DefaultPasswordPolicy(internalDataStore, properties)
        passwordPolicy.setResetTokenTtl(0)    //must be ok

        try {
            passwordPolicy.setResetTokenTtl(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "resetTokenTtl cannot be a negative number.")
        }
    }

    @Test
    void testSetStatusNull() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                          resetTokenTtl: 24,
                          resetEmailStatus: "ENABLED",
                          resetSuccessEmailStatus: "ENABLED",
                          strength: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/strength"],
                          resetEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetEmailTemplates"],
                          resetSuccessEmailTemplates: [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e/resetSuccessEmailTemplates"]
        ]

        PasswordPolicy passwordPolicy = new DefaultPasswordPolicy(internalDataStore, properties)

        try {
            passwordPolicy.setResetEmailStatus(null); //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "status cannot be null.")
        }

        try {
            passwordPolicy.setResetSuccessEmailStatus(null); //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "status cannot be null.")
        }
    }


}
