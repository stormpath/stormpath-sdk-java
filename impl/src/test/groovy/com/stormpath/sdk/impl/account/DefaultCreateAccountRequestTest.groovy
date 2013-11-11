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

import com.stormpath.sdk.account.Account
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.9.0
 */
class DefaultCreateAccountRequestTest {

    @Test
    void testDefault() {
        def account = createStrictMock(Account)
        def request = new DefaultCreateAccountRequest(account, null)

        assertSame(request.account, account)
        assertFalse request.isRegistrationWorkflowOptionSpecified()
    }

    @Test
    void testWorkflowEnabled() {
        def account = createStrictMock(Account)
        def request = new DefaultCreateAccountRequest(account, true)

        assertSame(request.account, account)
        assertTrue request.isRegistrationWorkflowOptionSpecified()
        assertTrue request.isRegistrationWorkflowEnabled()
    }

    @Test
    void testWorkflowDisabled() {
        def account = createStrictMock(Account)
        def request = new DefaultCreateAccountRequest(account, false)

        assertSame(request.account, account)
        assertTrue request.isRegistrationWorkflowOptionSpecified()
        assertFalse request.isRegistrationWorkflowEnabled()
    }

    @Test(expectedExceptions = IllegalStateException)
    void testWorkflowNotSpecifiedButAccessed() {

        def account = createStrictMock(Account)
        def request = new DefaultCreateAccountRequest(account, null)
        request.isRegistrationWorkflowEnabled()
    }
}
