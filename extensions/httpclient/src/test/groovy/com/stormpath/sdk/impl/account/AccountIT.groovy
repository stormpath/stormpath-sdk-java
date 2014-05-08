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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.impl.security.DefaultSaltGenerator
import org.testng.annotations.Test

import static com.stormpath.sdk.api.ApiKeys.*
import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class AccountIT extends ClientIT {

    @Test
    void testIsMemberOf() {

        def app = createTempApp()

        //create a user group:
        def group = client.instantiate(Group)
        group.name = uniquify('Users')
        group.description = "Description for " + group.name
        group = app.createGroup(group)
        deleteOnTeardown(group)

        //create a test account:
        def acct = createTestAccount(app)

        //add the account to the group:
        GroupMembership membership = group.addAccount(acct);
        deleteOnTeardown(membership)

        assertTrue acct.isMemberOfGroup(group.name)
        assertTrue acct.isMemberOfGroup(group.name.toUpperCase())
        assertTrue acct.isMemberOfGroup(group.href)
        assertTrue acct.isMemberOfGroup(group.href.toLowerCase())
        assertFalse acct.isMemberOfGroup(group.name.substring(0, group.name.length() - 2) + "*")

    }

    /**
     * @since 1.1.beta
     */
    @Test
    void testCreateAndDeleteApiKey() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        def apiKey = acct.createApiKey()

        assertNotNull apiKey
        assertFalse apiKey.href.isEmpty()
        assertEquals apiKey.status, ApiKeyStatus.ENABLED
        assertNotNull apiKey.account
        assertEquals apiKey.account.href, acct.href
        assertNotNull apiKey.tenant
        assertEquals apiKey.tenant.href, acct.tenant.href

        apiKey.delete() //passes if no error is returned

    }

    /**
     * @since 1.1.beta
     */
    @Test
    void testCreateApiKeyWithCriteria() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        def base64Salt = new DefaultSaltGenerator().generate()

        def apiKey = acct.createApiKey(newCreateRequest()
                                        .setEncryptSecret(true)
                                        .setEncryptionKeySize(128)
                                        .setEncryptionKeyIterations(1024)
                                        .setEncryptionKeySalt(base64Salt)
                                        .withResponseOptions(options().withAccount().withTenant())
                                        .build())


        assertNotNull apiKey
        assertFalse apiKey.href.isEmpty()
        assertEquals apiKey.status, ApiKeyStatus.ENABLED
        assertNotNull apiKey.account
        assertEquals apiKey.account.href, acct.href
        assertNotNull apiKey.tenant
        assertEquals apiKey.tenant.href, acct.tenant.href

        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)
        assertEquals retrievedApiKey.secret, apiKey.secret

        //TODO test expansion for this scenario when it gets fixed in the DefaultDataStore
    }

    /**
     * @since 1.1.beta
     */
    Account createTestAccount(def application) {

        //create a test account:
        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = application.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        return acct
    }

}
