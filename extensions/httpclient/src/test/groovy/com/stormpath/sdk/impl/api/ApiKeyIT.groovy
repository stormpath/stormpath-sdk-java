/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.api

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.client.ClientIT
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.1.beta
 */
class ApiKeyIT extends ClientIT {

    @Test
    void testUpdateStatus() {

        def apiKey = createTestApiKey()

        assertEquals apiKey.status, ApiKeyStatus.ENABLED

        apiKey.status = ApiKeyStatus.DISABLED
        apiKey.save()
        assertEquals apiKey.status, ApiKeyStatus.DISABLED
    }

    ApiKey createTestApiKey() {

        def application = createTempApp()

        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = application.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        def apiKey = acct.createApiKey()
        deleteOnTeardown(apiKey)

        return apiKey
    }
}
