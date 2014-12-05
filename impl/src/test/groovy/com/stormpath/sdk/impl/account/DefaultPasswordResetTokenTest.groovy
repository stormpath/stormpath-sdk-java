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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultPasswordResetTokenTest {

    @Test
    void testAll() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def resourceWithDS = new DefaultPasswordResetToken(internalDataStore)
        def resourceWithProps = new DefaultPasswordResetToken(internalDataStore,
                [href: "https://api.stormpath.com/v1/applications/WpM9nyZ2Tb67hbRvLk9KA/passwordResetTokens/j6HqguWPo98YXM2xmcOUShw",
                 account: [href: "https://api.stormpath.com/v1/accounts/nfoweurj9824urnou"]])

        assertTrue(resourceWithDS instanceof DefaultPasswordResetToken && resourceWithProps instanceof DefaultPasswordResetToken)
        def pd = resourceWithProps.getPropertyDescriptors()
        assertEquals(pd.size(), 4)
        assertTrue(pd.email instanceof StringProperty)
        assertTrue(pd.account instanceof ResourceReference)
        assertEquals(pd.account.type, Account)
        assertTrue(pd.accountStore instanceof ResourceReference)
        assertEquals(pd.accountStore.type, AccountStore)
        assertTrue(pd.password instanceof StringProperty)

        resourceWithDS.setEmail("some@email.com")
        assertEquals(resourceWithDS.getEmail(), "some@email.com")

        def innerProperties = [href: "https://api.stormpath.com/v1/accounts/nfoweurj9824urnou"]
        def account = new DefaultAccount(internalDataStore, innerProperties)
        expect(internalDataStore.instantiate(Account, innerProperties)).andReturn(account)

        def accountStore = createStrictMock(Directory)
        def accountStoreHref = 'https://api.stormpath.com/v1/directories/dir123'
        expect(accountStore.href).andReturn(accountStoreHref)

        replay internalDataStore, accountStore

        resourceWithDS.setPassword("fooPassword")
        assertEquals(resourceWithDS.getProperty("password"), "fooPassword")

        resourceWithDS.setAccountStore(accountStore)
        assertEquals(resourceWithDS.dirtyProperties.accountStore.href, accountStoreHref)

        assertSame(resourceWithProps.account, account)

        verify internalDataStore, accountStore
    }
}
