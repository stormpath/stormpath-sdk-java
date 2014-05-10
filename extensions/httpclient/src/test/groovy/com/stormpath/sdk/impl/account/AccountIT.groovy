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
import com.stormpath.sdk.impl.api.ApiKeyParameter
import com.stormpath.sdk.impl.ds.api.ApiKeyCacheParameter
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import org.testng.annotations.Test

import static com.stormpath.sdk.api.ApiKeys.*
import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class AccountIT extends ClientIT {

    def encryptionServiceBuilder = new ApiKeySecretEncryptionService.Builder()

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
     * @since 1.0.RC
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
     * @since 1.0.RC
     */
    @Test
    void testCreateApiKeyWithOptions() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        def apiKey = acct.createApiKey(options().withAccount().withTenant())


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

    @Test
    void testGetApiKeys() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        int apiKeysCreated = 10
        for (int i = 0; i < apiKeysCreated; i++) {
            acct.createApiKey()
        }

        def apiKeys = acct.getApiKeys()

        int apiKeysCount = 0
        for (ApiKey apiKey : apiKeys) {
            apiKeysCount++
        }

        assertEquals apiKeysCount, apiKeysCreated
    }

    @Test
    void testGetApiKeysDecryptFromCache() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        int apiKeysCreated = 10
        for (int i = 0; i < apiKeysCreated; i++) {
            acct.createApiKey()
        }

        def client = buildClient(true)
        acct = client.dataStore.getResource(acct.href, Account) // need to request the account from the new client to cache the api keys
        def apiKeys = acct.getApiKeys()
        def apiKeyCache = client.dataStore.cacheManager.getCache(ApiKey.name)
        assertNotNull apiKeyCache
        int apiKeysCount = 0
        for (ApiKey apiKey : apiKeys) {
            apiKeysCount++

            def apiKeyCacheValue = apiKeyCache.get(apiKey.href)
            assertNotNull apiKeyCacheValue
            assertNotEquals apiKeyCacheValue['secret'], apiKey.secret
            assertEquals decryptSecretFromCacheMap(apiKeyCacheValue), apiKey.secret
        }

        assertEquals apiKeysCount, apiKeysCreated
    }

    @Test
    void testGetApiKeysWithCriteria() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        int apiKeysCreated = 10
        for (int i = 0; i < apiKeysCreated; i++) {
            acct.createApiKey()
        }

        int limit = 5
        int offset = 2
        def apiKeys = acct.getApiKeys(criteria().offsetBy(offset).limitTo(limit).withTenant())

        assertEquals apiKeys.limit, limit
        assertEquals apiKeys.offset, offset

        int apiKeysCount = 0
        for (ApiKey apiKey : apiKeys) {
            assertTrue(apiKey.tenant.getPropertyNames().size() > 1) // testing expansion
            apiKeysCount++
        }

        assertEquals(apiKeysCount, apiKeysCreated - offset)

    }

    @Test
    void testGetApiKeysWithMap() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        int apiKeysCreated = 10
        for (int i = 0; i < apiKeysCreated; i++) {
            acct.createApiKey()
        }

        int limit = 5
        int offset = 2
        def apiKeys = acct.getApiKeys(['offset' : offset, 'limit' : limit, 'expand' : 'tenant'])

        assertEquals apiKeys.limit, limit
        assertEquals apiKeys.offset, offset

        int apiKeysCount = 0
        for (ApiKey apiKey : apiKeys) {
            assertTrue(apiKey.tenant.getPropertyNames().size() > 1) // testing expansion
            apiKeysCount++
        }

        assertEquals(apiKeysCount, apiKeysCreated - offset)

    }

    /**
     * @since 1.0.RC
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

    //@since 1.0.beta
    @Test
    void testGetProviderData() {

        def app = createTempApp()

        //create a test account:
        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        def providerData = acct.getProviderData()

        assertEquals providerData.getHref(), acct.getHref() + "/providerData"
        assertEquals providerData.getProviderId(), "stormpath"
        assertNotNull providerData.getCreatedAt()
        assertNotNull providerData.getModifiedAt()
    }

    String decryptSecretFromCacheMap(Map cacheMap) {

        if (cacheMap == null || cacheMap.isEmpty() || !cacheMap.containsKey(ApiKeyCacheParameter.API_KEY_META_DATA.toString())) {
            return null
        }

        def apiKeyMetaData = cacheMap[ApiKeyCacheParameter.API_KEY_META_DATA.toString()]

        def salt = apiKeyMetaData[ApiKeyParameter.ENCRYPTION_KEY_SALT.getName()]
        def keySize = apiKeyMetaData[ApiKeyParameter.ENCRYPTION_KEY_SIZE.getName()]
        def iterations = apiKeyMetaData[ApiKeyParameter.ENCRYPTION_KEY_ITERATIONS.getName()]

        def encryptionService = encryptionServiceBuilder
                .setBase64Salt(salt.getBytes())
                .setKeySize(keySize)
                .setIterations(iterations)
                .setPassword(client.dataStore.apiKey.secret.toCharArray()).build()

        def secret = encryptionService.decryptBase64String(cacheMap['secret'])

        return secret

    }

}
