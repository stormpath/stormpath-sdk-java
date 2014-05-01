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

import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.http.RequestExecutor
import org.junit.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class DefaultProviderAccountResultHelperTest {

    @Test
    void testGetPropertyDescriptors() {

        def providerAccountResultHelper = new DefaultProviderAccountResultHelper(createStrictMock(InternalDataStore))

        def propertyDescriptors = providerAccountResultHelper.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 0)
    }

    @Test
    void testInstantiation() {

        //def properties = [providerAccountResult: providerAccountResult]

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

        def requestExecutor = createStrictMock(RequestExecutor)

        Map<String, Object> propertiesWithoutIsNewAccount = new HashMap<String, Object>()
        propertiesWithoutIsNewAccount.putAll(properties)
        propertiesWithoutIsNewAccount.remove("isNewAccount")

        replay(requestExecutor)

        def internalDataStore = new DefaultDataStore(requestExecutor)
        def providerAccountResultHelper = new DefaultProviderAccountResultHelper(internalDataStore, properties)
        def providerAccountResult = providerAccountResultHelper.getProviderAccountResult()
        assertTrue(providerAccountResult.isNewAccount())
        assertEquals(providerAccountResult.getAccount().getHref(), properties.href)
        assertEquals(providerAccountResult.getAccount().getGroups().getHref(), properties.groups.href)
        assertEquals(providerAccountResult.getAccount().getDirectory().getHref(), properties.directory.href)
        assertEquals(providerAccountResult.getAccount().getFullName(), properties.fullName)

        verify(requestExecutor)
    }

    @Test
    void testInstantiationEmptyProperties() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = new HashMap<String, Object>()

        def providerAccountResultHelper = new DefaultProviderAccountResultHelper(internalDataStore, properties)
        assertNull(providerAccountResultHelper.getProviderAccountResult())
    }

}
