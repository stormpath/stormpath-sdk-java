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
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.client.ClientIT
import org.testng.annotations.Test

import static com.stormpath.sdk.api.ApiKeys.options
import static org.testng.Assert.*

/**
 * @since 1.1.beta
 */
class ApiKeyIT extends ClientIT {

    ApiKey apiKey;

    @Test
    void testUpdateStatus() {

        def apiKey = getTestApiKey()

        assertEquals apiKey.status, ApiKeyStatus.ENABLED

        apiKey.status = ApiKeyStatus.DISABLED
        apiKey.save()
        assertEquals apiKey.status, ApiKeyStatus.DISABLED

        apiKey.status = ApiKeyStatus.ENABLED
        apiKey.save()
    }

    @Test
    void testSaveWithRequest() {

        def apiKey = getTestApiKey()

        assertEquals apiKey.status, ApiKeyStatus.ENABLED

        apiKey.status = ApiKeyStatus.DISABLED
        apiKey.save(ApiKeys.newSaveRequest()
                    .setEncryptSecret(true)
                    .setEncryptionKeySize(128)
                    .setEncryptionKeyIterations(10)
                    .setEncryptionKeySalt("YAKpZIKRAUJ0-1pf57Bs_A")
                    .withResponseOptions(options().withAccount().withTenant())
                    .build())

        assertEquals apiKey.status, ApiKeyStatus.DISABLED

        // need to create another client to call the server because the api key is cached with the encrypted values
        // TODO define cache keys that include the query parameters
        def client = buildClient()
        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)
        assertNotEquals apiKey.secret, retrievedApiKey.secret

        //TODO test expansion for this scenario when it gets fixed in the DefaultDataStore
    }

    @Test
    void testGetByHref() {

        def apiKey = getTestApiKey()

        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)

        assertNotNull retrievedApiKey
        assertEquals apiKey, retrievedApiKey
    }

    ApiKey getTestApiKey() {

        if (apiKey != null) {
            return apiKey
        }

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

        apiKey = acct.createApiKey()
        deleteOnTeardown(apiKey)

        return apiKey
    }
}
