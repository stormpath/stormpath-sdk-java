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

import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * @since 0.8.2
 */
class DefaultCreateAccountRequestBuilderTest {

    @Test
    void testBuilder() {
        def account = new DefaultAccount(createStrictMock(InternalDataStore))

        def request = new DefaultCreateAccountRequestBuilder(account)
        assertFalse(request.registrationWorkflowSet)
        assertTrue(request.build() instanceof DefaultCreateAccountRequest)
        assertFalse(request.build() instanceof CreateAccountWithWorkflowValueRequest)

        request = new DefaultCreateAccountRequestBuilder(account).setRegistrationWorkflowEnabled(true)
        assertTrue(request.registrationWorkflowSet)
        assertTrue(request.registrationWorkflowEnabled)
        assertTrue(request.build() instanceof CreateAccountWithWorkflowValueRequest)

        request = new DefaultCreateAccountRequestBuilder(account).setRegistrationWorkflowEnabled(false)
        assertTrue(request.registrationWorkflowSet)
        assertFalse(request.registrationWorkflowEnabled)
        assertTrue(request.build() instanceof CreateAccountWithWorkflowValueRequest)
    }

}
