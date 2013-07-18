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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue


/**
 * @author ecrisostomo
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
        assertEquals(2, resourceWithProps.getPropertyDescriptors().size())
        assertTrue(resourceWithProps.getPropertyDescriptors().get("email") instanceof StringProperty && resourceWithProps.getPropertyDescriptors().get("account") instanceof ResourceReference)
        assertEquals(Account, resourceWithProps.getPropertyDescriptors().get("account").getType())

        resourceWithDS.setEmail("some@email.com")
        assertEquals("some@email.com", resourceWithDS.getEmail())

        def innerProperties = [href: "https://api.stormpath.com/v1/accounts/nfoweurj9824urnou"]
        expect(internalDataStore.instantiate(Account, innerProperties)).andReturn(new DefaultAccount(internalDataStore, innerProperties))

        replay internalDataStore

        assertTrue(resourceWithProps.getAccount() instanceof Account)

        verify internalDataStore

    }
}
