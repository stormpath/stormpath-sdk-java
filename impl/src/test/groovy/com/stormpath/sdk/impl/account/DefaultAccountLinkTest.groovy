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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountLink
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.1.0
 */
public class DefaultAccountLinkTest {

    @Test
    void testGetPropertyDescriptors() {

        AccountLink accountLink = new DefaultAccountLink(createStrictMock(InternalDataStore))

        def propertyDescriptors = accountLink.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 3)

        assertTrue(propertyDescriptors.get("leftAccount") instanceof ResourceReference && propertyDescriptors.get("leftAccount").getType().equals(Account))
        assertTrue(propertyDescriptors.get("rightAccount") instanceof ResourceReference && propertyDescriptors.get("leftAccount").getType().equals(Account))
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/accountLinks/iouertnw48ufsjnsDFSf",
                          leftAccount: [href: "https://api.stormpath.com/v1/accounts/fwerh23948ru2euweouh"],
                          rightAccount: [href: "https://api.stormpath.com/v1/accounts/jdhrgojeorigjj09etiij"],
                          groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                          createdAt: "2015-01-01T00:00:00Z"]

        AccountLink accountLink = new DefaultAccountLink(internalDataStore, properties)

        expect(internalDataStore.instantiate(Account, properties.leftAccount)).
                andReturn(new DefaultAccount(internalDataStore, properties.leftAccount))

        expect(internalDataStore.instantiate(Account, properties.rightAccount)).
                andReturn(new DefaultAccount(internalDataStore, properties.rightAccount))

        replay internalDataStore

        def leftAccount = accountLink.getLeftAccount()
        assertTrue(leftAccount instanceof Account && leftAccount.getHref().equals(properties.leftAccount.href))
        def rightAccount = accountLink.getRightAccount()
        assertTrue(rightAccount instanceof Account && rightAccount.getHref().equals(properties.rightAccount.href))

        DateFormat dateFormatter = new ISO8601DateFormat()
        assertEquals(accountLink.getCreatedAt(), dateFormatter.parse(properties.createdAt))
        assertEquals(accountLink.getHref(), properties.href)

        verify internalDataStore
    }
}
