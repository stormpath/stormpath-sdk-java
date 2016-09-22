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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountLinkingPolicy
import com.stormpath.sdk.account.AccountLinkingStatus
import com.stormpath.sdk.account.AutomaticProvisioningStatus
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.EnumProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*
/**
 * @since 1.1.0
 */
public class DefaultAccountLinkingPolicyTest {

    @Test
    void testGetPropertyDescriptors() {
        AccountLinkingPolicy accountLinkingPolicy = new DefaultAccountLinkingPolicy(createStrictMock(InternalDataStore))

        def propertyDescriptors = accountLinkingPolicy.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 4)

        assertTrue(propertyDescriptors.get("status") instanceof EnumProperty)
        assertTrue(propertyDescriptors.get("automaticProvisioning") instanceof EnumProperty)
        assertTrue(propertyDescriptors.get("matchingProperty") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/accountLinkingPolicies/35YM3OwioW9PVtfLOh6q1e",
                          status: "DISABLED",
                          automaticProvisioning: "DISABLED",
                          matchingProperty: "email", //case-sensitive
                          tenant: [href: "https://api.stormpath.com/v1/tenants/3Tj2L7gxX6NkXtiiLkh1WF"]
        ]

        AccountLinkingPolicy accountLinkingPolicy = new DefaultAccountLinkingPolicy(internalDataStore, properties)

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).
                andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        replay internalDataStore

        assertEquals(accountLinkingPolicy.getStatus().name(), properties.status)
        assertEquals(accountLinkingPolicy.getAutomaticProvisioning().name(), properties.automaticProvisioning)
        assertEquals(accountLinkingPolicy.getMatchingProperty(), properties.matchingProperty)

        accountLinkingPolicy = accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED).setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED).setMatchingProperty("email")

        assertEquals(accountLinkingPolicy.getStatus().name(), "ENABLED")
        assertEquals(accountLinkingPolicy.getAutomaticProvisioning().name(), "ENABLED")
        assertEquals(accountLinkingPolicy.getMatchingProperty(), "email")
        assertEquals(accountLinkingPolicy.getHref(), properties.href)

        def tenant = accountLinkingPolicy.getTenant()
        assertTrue(tenant instanceof Tenant && tenant.getHref().equals(properties.tenant.href))

        verify internalDataStore
    }

    @Test
    void testErrors() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/accountLinkingPolicies/35YM3OwioW9PVtfLOh6q1e",
                          status: "DISABLED",
                          automaticProvisioning: "DISABLED",
                          matchingProperty: "EMAIL",
                          tenant: [href: "https://api.stormpath.com/v1/tenants/3Tj2L7gxX6NkXtiiLkh1WF"]
        ]

        AccountLinkingPolicy accountLinkingPolicy = new DefaultAccountLinkingPolicy(internalDataStore, properties)

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).
                andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        replay internalDataStore

        accountLinkingPolicy = accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED).
                                setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED).
                                setMatchingProperty("email")

        try {
            accountLinkingPolicy.setStatus(null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "status cannot be null.")
        }

        try {
            accountLinkingPolicy.setAutomaticProvisioning(null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "automaticProvisioning cannot be null.")
        }

        accountLinkingPolicy.setMatchingProperty(null) // matching property is nullable.
    }
}
