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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.impl.resource.AbstractResource
import java.lang.reflect.Field
import com.stormpath.sdk.impl.api.ApiKeyParameter
import com.stormpath.sdk.impl.ds.api.ApiKeyCacheParameter
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import static com.stormpath.sdk.api.ApiKeys.criteria
import static com.stormpath.sdk.api.ApiKeys.options
import static org.testng.Assert.*
import org.testng.annotations.Test

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

    /**
     * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/47
     * @since 1.0.0
     */
    @Test
    public void testResourceReferencesStayInSync() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        account = client.getResource(account.href, Account.class);

        def directory = client.getResource(account.getDirectory().getHref(), Directory.class);

        directory.setName(app.getName() + "XXXXX");
        directory.save();

        account.getDirectory().getName();
        assertEquals(directory.getName(), account.getDirectory().getName());

        directory.setName(app.getName() + "YYYYY");
        directory.save();

        //Before fixing issue #47, this assertion failed
        assertEquals(directory.getName(), account.getDirectory().getName());
    }

    // @since 1.0.0
    @Test
    public void testCustomDataStayInSync() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        account = client.getResource(account.getHref(), Account.class);

        def customData = client.getResource(account.getHref() + "/customData", CustomData.class);

        customData.put("aKey", "aValue01");
        customData.save();

        //CustomData content obtained from Account is in sync with the content obtained from the CustomData Resource
        assertEquals("aValue01", customData.get("aKey"));
        assertEquals("aValue01", account.getCustomData().get("aKey"));

        customData.put("aKey", "aValue02");
        customData.save();

        //Before fixing issue #47, this assertion failed
        assertEquals(customData.get("aKey"), account.getCustomData().get("aKey")); // assertion fails
    }

    //@since 1.0.0
    @Test
    public void testCustomDataSize() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        def customData = client.getResource(account.getHref() + "/customData", CustomData.class);

        customData.put("aKey", "aValue02");
        assertEquals(customData.size(), account.getCustomData().size() + 3)
        account.getCustomData().get("anything")
        assertEquals(customData.size(), account.getCustomData().size() + 1)
        customData.save();
        assertEquals(customData.size(), account.getCustomData().size())
    }

    /**
     * @since 1.0.0
     */
    @Test
    void testInternalProperties() {

        def app = createTempApp()
        def account01 = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account01.setEmail(account01.getUsername() + "@nowhere.com")
        account01 = app.createAccount(Accounts.newCreateRequestFor(account01).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account01)

        def account02 = client.getResource(account01.href, Account)

        Map properties01 = getValue(AbstractResource, account01, "properties")
        Map dirtyProperties01 = getValue(AbstractResource, account01, "dirtyProperties")
        Map properties02 = getValue(AbstractResource, account02, "properties")
        Map dirtyProperties02 = getValue(AbstractResource, account02, "dirtyProperties")

        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)
        assertSame(properties01, properties02)

        account01.setEmail("new@email.com")
        assertEquals(account01.getEmail(), "new@email.com")
        assertEquals(account02.getEmail(), account01.getUsername() + "@nowhere.com")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

        account01.save()

        assertSame(properties01, properties02)
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

        assertEquals(account01.getEmail(), account02.getEmail())

        account02.setMiddleName("New Middle Name for Account02")
        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)

        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)

        account01.setMiddleName("New Middle Name for Account01")
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)
        account02.save()

        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)
        account01.save()
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account01")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

    }

    //@since 1.0.0
    @Test
    void testCustomData() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        def customData01 = client.getResource(account.href + "/customData", CustomData)
        def customData02 = account.getCustomData()

        assertFalse(customData01.containsValue("aValue"))
        assertFalse(customData02.containsValue("aValue"))
        assertEquals(customData01.size(), 3)
        assertEquals(customData02.size(), 3)

        assertEquals(customData01.getPropertyNames().size(), 3)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.put("aKey","aValue")
        assertEquals(customData01.size(), 4)
        assertTrue(customData01.containsKey("aKey"))
        assertTrue(customData01.containsValue("aValue"))
        assertEquals(customData02.size(), 3)
        assertFalse(customData02.containsKey("aKey"))
        assertFalse(customData02.containsValue("aValue"))

        customData01.remove("aKey")
        assertEquals(customData01.size(), 3)
        assertFalse(customData01.containsKey("aKey"))
        assertFalse(customData01.containsValue("aValue"))
        assertEquals(customData02.size(), 3)
        assertFalse(customData02.containsKey("aKey"))
        assertFalse(customData02.containsValue("aValue"))
        customData01.put("anotherKey","aValue")
        assertEquals(customData01.size(), 4)
        assertTrue(customData01.containsValue("aValue"))
        assertFalse(customData01.containsKey("aKey"))
        assertTrue(customData01.containsKey("anotherKey"))
        assertEquals(customData01.entrySet().size(), 4)
        assertEquals(customData01.keySet().size(), 4)
        assertEquals(customData01.values().size(), 4)
        assertEquals(customData02.size(), 3)
        assertFalse(customData02.containsValue("aValue"))
        assertFalse(customData02.containsKey("aKey"))
        assertFalse(customData02.containsKey("anotherKey"))
        assertEquals(customData02.entrySet().size(), 3)
        assertEquals(customData02.keySet().size(), 3)
        assertEquals(customData02.values().size(), 3)
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 1)
        assertEquals(customData01.getDeletedPropertyNames().size(), 1)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData02.put("anotherKey","aValue02")
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 1)
        assertEquals(customData01.getDeletedPropertyNames().size(), 1)
        assertEquals(customData02.getPropertyNames().size(), 4)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 1)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.save()
        assertEquals(customData01.size(), 4)
        assertTrue(customData01.containsValue("aValue"))
        assertFalse(customData01.containsKey("aKey"))
        assertTrue(customData01.containsKey("anotherKey"))
        assertEquals(customData01.entrySet().size(), 4)
        assertEquals(customData01.keySet().size(), 4)
        assertEquals(customData01.values().size(), 4)
        assertEquals(customData02.size(), 4)
        assertFalse(customData02.containsValue("aValue"))
        assertTrue(customData02.containsValue("aValue02"))
        assertFalse(customData02.containsKey("aKey"))
        assertTrue(customData02.containsKey("anotherKey"))
        assertEquals(customData02.entrySet().size(), 4)
        assertEquals(customData02.keySet().size(), 4)
        assertEquals(customData02.values().size(), 4)
        assertEquals(customData01.get("anotherKey"), "aValue")
        assertEquals(customData02.get("anotherKey"), "aValue02")
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 4)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 1)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData02.save()
        assertEquals(customData01.get("anotherKey"), "aValue02")
        assertEquals(customData02.get("anotherKey"), "aValue02")
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 4)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData02.clear()
        assertEquals(customData01.size(), 4)
        assertEquals(customData02.size(), 3)
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 1)

        customData02.save()
        assertEquals(customData01.size(), 3)
        assertEquals(customData02.size(), 3)
        assertEquals(customData01.getPropertyNames().size(), 3)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.put("aKey","aValue")
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 1)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.remove("aKey")
        assertEquals(customData01.getPropertyNames().size(), 3)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 1)
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.put("aKey","newValue")
        customData01.save()
        assertEquals(customData01.getPropertyNames().size(), 4)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.getPropertyNames().size(), 4)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)

        customData01.remove("aKey")
        assertEquals(customData01.getPropertyNames().size(), 3)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 1)
        assertNull(customData01.get("aKey"))
        assertEquals(customData02.getPropertyNames().size(), 4)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)
        assertEquals(customData02.get("aKey"), "newValue")

        customData01.save()
        assertEquals(customData01.getPropertyNames().size(), 3)
        assertEquals(customData01.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData01.getDeletedPropertyNames().size(), 0)
        assertNull(customData01.get("aKey"))
        assertEquals(customData02.getPropertyNames().size(), 3)
        assertEquals(customData02.getUpdatedPropertyNames().size(), 0)
        assertEquals(customData02.getDeletedPropertyNames().size(), 0)
        assertNull(customData02.get("aKey"))

    }

    /**
     * Test for https://github.com/stormpath/stormpath-sdk-java/issues/112
     * @since 1.0.0
     */
    @Test
    void testGetCroupsWithLimitAndOffset() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        def group01 = client.instantiate(Group)
        group01.name = uniquify("My Group01")
        group01 = app.createGroup(group01)
        deleteOnTeardown(group01)

        def group02 = client.instantiate(Group)
        group02.name = uniquify("My Group02")
        group02 = app.createGroup(group02)
        deleteOnTeardown(group02)

        account.addGroup(group01)
        account.addGroup(group02)

        GroupList groups = account.getGroups(Groups.criteria().limitTo(1).orderByName().ascending());
        assertEquals(1, groups.getLimit());
        assertEquals(0, groups.getOffset());

        Group firstGroupWithOffset0 = groups.iterator().next();

        assertNotNull(firstGroupWithOffset0);
        assertEquals(firstGroupWithOffset0.getHref(), group01.getHref());

        groups = account.getGroups(Groups.criteria().offsetBy(1).orderByName().ascending());
        assertEquals(25, groups.getLimit());
        assertEquals(1, groups.getOffset());

        Group firstGroupWithOffset1 = groups.iterator().next();

        assertNotNull(firstGroupWithOffset1);
        assertEquals(firstGroupWithOffset1.getHref(), group02.getHref());

        assertTrue(firstGroupWithOffset0.getHref() != firstGroupWithOffset1.getHref());
    }


    //@since 1.0.0
    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
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
