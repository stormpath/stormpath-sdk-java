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
package com.stormpath.sdk.impl.api

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.impl.ds.api.ApiKeyCacheParameter
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import org.testng.annotations.Test

import static com.stormpath.sdk.api.ApiKeys.options
import static org.testng.Assert.*

/**
 * @since 1.0.RC
 */
class ApiKeyIT extends ClientIT {

    ApiKey apiKey;
    def encryptionServiceBuilder = new ApiKeySecretEncryptionService.Builder()

    @Test
    void testUpdateStatus() {

        def apiKey = getTestApiKey()

        apiKey.status = ApiKeyStatus.DISABLED
        apiKey.save()
        assertEquals apiKey.status, ApiKeyStatus.DISABLED

        apiKey.status = ApiKeyStatus.ENABLED
        apiKey.save()
    }

    @Test
    void testSaveWithOptions() {

        def apiKey = getTestApiKey()

        apiKey.status = ApiKeyStatus.DISABLED
        apiKey.save(options().withAccount().withTenant())

        assertEquals apiKey.status, ApiKeyStatus.DISABLED

        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)
        assertEquals apiKey, retrievedApiKey

        //TODO test expansion for this scenario when it gets fixed in the DefaultDataStore
    }

    @Test
    void testSaveWithOptionsWithNoCaching() {

        def apiKey = getTestApiKey()

        apiKey.status = ApiKeyStatus.ENABLED
        apiKey.save(options().withAccount().withTenant())

        assertEquals apiKey.status, ApiKeyStatus.ENABLED

        def client = buildClient(false)
        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)
        assertEquals apiKey.href, retrievedApiKey.href
        assertEquals apiKey.id, retrievedApiKey.id

        //TODO test expansion for this scenario when it gets fixed in the DefaultDataStore
    }

    @Test
    void testGetByHref() {

        def apiKey = getTestApiKey()

        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)

        assertNotNull retrievedApiKey
        assertEquals retrievedApiKey, apiKey
    }

    /**
     * Found an issue where the collection resource (in this case account.apiKeys) once is accessed the reference
     * never looks at the server anymore, this means that creation or deletion of the items are ignored by the
     * collection resource at all times, and there is no way to ask for a new collection from the same entity.
     */
    @Test
    void testListApiKeysWithDeletion() {

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

        List<ApiKey> apiKeyArray = []

        (1..5).each {
            apiKeyArray.add(acct.createApiKey())
        }

        assertEquals acct.getApiKeys().iterator().size(), 5

        //Deleting all but last one.
        (1..4).each { i ->
            apiKeyArray[i].delete()
        }

        //Getting the account again from a client with no cache to receive a fresh account object from the server.
        //then get the apiKeys collection and assert there is only one left.

        Client client2 = buildClient(false)
        Account acct2 = client2.getResource(acct.href, Account)
        assertEquals acct2.getApiKeys().iterator().size(), 1
    }

    /**
     * Tests the fix of the issue found in above test (https://github.com/stormpath/stormpath-sdk-java/issues/62)
     * @since 1.0.RC3
     */
    @Test
    void testListApiKeysWithDeletion_NewCollectionInstance() {

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

        List<ApiKey> apiKeyArray = []

        (1..5).each {
            apiKeyArray.add(acct.createApiKey())
        }

        assertEquals acct.getApiKeys().iterator().size(), 5

        //Deleting all but last one.
        (1..4).each { i ->
            apiKeyArray[i].delete()
        }

        assertEquals acct.getApiKeys().iterator().size(), 1
    }

    /**
     * Tests the fix of issue 62 (https://github.com/stormpath/stormpath-sdk-java/issues/62)
     * @since 1.0.RC3
     */
    @Test
    void testListApiKeysWithDeletion_SameCollection_NewIterator() {

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

        List<ApiKey> apiKeyArray = []

        (1..5).each {
            apiKeyArray.add(acct.createApiKey())
        }

        def apiKeys = acct.getApiKeys()

        assertEquals apiKeys.iterator().size(), 5

        //Deleting all but last one.
        (1..4).each { i ->
            apiKeyArray[i].delete()
        }

        assertEquals apiKeys.iterator().size(), 1
    }

    @Test
    void testDecryptFromCache() {

        def apiKey = getTestApiKey()

        def retrievedApiKey = client.getResource(apiKey.href, ApiKey)

        assertNotNull retrievedApiKey
        assertEquals retrievedApiKey, apiKey

        def apiKeyCache = client.dataStore.cacheManager.getCache(ApiKey.name)
        assertNotNull apiKeyCache
        def apiKeyCacheValue = apiKeyCache.get(retrievedApiKey.href)
        assertNotNull apiKeyCacheValue
        assertNotEquals apiKeyCacheValue['secret'], retrievedApiKey.secret
        assertEquals decryptSecretFromCacheMap(apiKeyCacheValue), retrievedApiKey.secret
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
