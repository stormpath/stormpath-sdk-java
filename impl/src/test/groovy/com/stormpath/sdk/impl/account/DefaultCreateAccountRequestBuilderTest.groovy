/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.account.PasswordFormat
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.9
 */
class DefaultCreateAccountRequestBuilderTest {

    @Test
    void testBuilder() {
        def account = new DefaultAccount(createStrictMock(InternalDataStore))

        def request = new DefaultCreateAccountRequestBuilder(account).build()
        assertSame account, request.account
        assertFalse request.isRegistrationWorkflowOptionSpecified()

        request = new DefaultCreateAccountRequestBuilder(account).setRegistrationWorkflowEnabled(true).build()
        assertSame account, request.account
        assertTrue request.isRegistrationWorkflowOptionSpecified()
        assertTrue request.isRegistrationWorkflowEnabled()

        request = new DefaultCreateAccountRequestBuilder(account).setRegistrationWorkflowEnabled(false).build()
        assertSame account, request.account
        assertTrue request.isRegistrationWorkflowOptionSpecified()
        assertFalse request.isRegistrationWorkflowEnabled()

        request = new DefaultCreateAccountRequestBuilder(account)
                .setPasswordFormat(PasswordFormat.MCF)
                .setRegistrationWorkflowEnabled(false)
                .build()
        assertSame account, request.account
        assertTrue request.isPasswordFormatSpecified()
        assertEquals request.getPasswordFormat(), PasswordFormat.MCF

        request = new DefaultCreateAccountRequestBuilder(account).setRegistrationWorkflowEnabled(false).setPasswordFormat(PasswordFormat.MCF).build()
        assertSame account, request.account
        assertEquals request.isRegistrationWorkflowEnabled(), false
        assertEquals request.getPasswordFormat(), PasswordFormat.MCF
    }

    @Test(expectedExceptions = IllegalStateException)
    void testWorkflowUnspecifiedButAccessed() {
        def account = new DefaultAccount(createStrictMock(InternalDataStore))

        def request = new DefaultCreateAccountRequestBuilder(account).build()
        assertSame account, request.account
        request.isRegistrationWorkflowEnabled()
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test(expectedExceptions = IllegalStateException)
    void testPasswordFormatNotSpecified() {
        def account = new DefaultAccount(createStrictMock(InternalDataStore))

        def request = new DefaultCreateAccountRequestBuilder(account).build()
        assertSame account, request.account
        request.getPasswordFormat()
    }
}
