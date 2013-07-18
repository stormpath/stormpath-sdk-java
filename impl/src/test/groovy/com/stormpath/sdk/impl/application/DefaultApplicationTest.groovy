/*
 * Copyright 2013 Stormpath, Inc. and contributors.
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
package com.stormpath.sdk.impl.application

import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @author ecrisostomo
 * @since 0.8
 */
class DefaultApplicationTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultApplication = new DefaultApplication(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultApplication.getPropertyDescriptors()

        assertEquals(7, propertyDescriptors.size())

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(ApplicationStatus))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("passwordResetTokens") instanceof CollectionReference && propertyDescriptors.get("passwordResetTokens").getType().equals(PasswordResetTokenList))
    }

    @Test
    void testMethods() {

        def properties = [tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                          accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                          passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        assertNull(defaultApplication.getStatus())

        defaultApplication.setStatus(ApplicationStatus.DISABLED)
        defaultApplication.setName("App Name")
        defaultApplication.setDescription("App Description")

        assertEquals(ApplicationStatus.DISABLED, defaultApplication.getStatus())
        assertEquals("App Name", defaultApplication.getName())
        assertEquals("App Description", defaultApplication.getDescription())
    }
}