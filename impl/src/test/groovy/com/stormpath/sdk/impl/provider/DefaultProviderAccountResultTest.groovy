/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.BooleanProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultProviderAccountResultTest {

    @Test
    void testGetPropertyDescriptors() {

        def providerAccountResult = new DefaultProviderAccountResult(createStrictMock(InternalDataStore))

        def propertyDescriptors = providerAccountResult.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 2)

        assertTrue(propertyDescriptors.get("isNewAccount") instanceof BooleanProperty)
        assertTrue(propertyDescriptors.get("account") instanceof ResourceReference && propertyDescriptors.get("account").getType().equals(Account))

    }

    @Test
    void testInstantiation() {

        def properties = [isNewAccount: true,
                          href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                          fullName: "Mel Ben Smuk",
                          emailVerificationToken: [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"],
                          directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                          groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                          groupMemberships: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"],
                          providerData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def account = createStrictMock(Account)

        Map<String, Object> propertiesWithoutIsNewAccount = new HashMap<String, Object>()
        propertiesWithoutIsNewAccount.putAll(properties)
        propertiesWithoutIsNewAccount.remove("isNewAccount")

        expect(internalDataStore.instantiate(Account, propertiesWithoutIsNewAccount)).andReturn(account)

        replay(internalDataStore, account)

        def providerAccountResult = new DefaultProviderAccountResult(internalDataStore, properties)
        assertTrue(providerAccountResult.isNewAccount())
        assertEquals(providerAccountResult.getAccount(), account)

        verify(internalDataStore, account)
    }

    @Test
    void testInstantiationNullProperties() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def providerAccountResult = new DefaultProviderAccountResult(internalDataStore, null)
        assertFalse(providerAccountResult.isNewAccount())
        assertNull(providerAccountResult.getAccount())
    }

}
