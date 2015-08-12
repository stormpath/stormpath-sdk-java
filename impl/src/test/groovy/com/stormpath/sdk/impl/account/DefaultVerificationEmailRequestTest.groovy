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

import com.stormpath.sdk.directory.AccountStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertSame

/**
 * @since 1.0.0
 */
class DefaultVerificationEmailRequestTest {

    @Test
    void testAll() {
        def accountStore = createStrictMock(AccountStore)
        def verificationEmailRequest = new DefaultVerificationEmailRequest(null)

        String email = "email@domain.com"
        verificationEmailRequest.setLogin(email)
        verificationEmailRequest.setAccountStore(accountStore)

        assertSame(verificationEmailRequest.getLogin(), email)
        assertSame(verificationEmailRequest.getAccountStore(), accountStore)
    }

    @Test(
        expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "accountStore cannot be null"
    )
    void testNullAccountStore() {
        new DefaultVerificationEmailRequest(null).setAccountStore(null)
    }
}
