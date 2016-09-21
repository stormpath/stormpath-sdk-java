/*
 * Copyright 2015 Stormpath, Inc.
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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.account.AccountLink
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.impl.api.ApiKeyParameter
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.text.DateFormat

import static com.stormpath.sdk.api.ApiKeys.criteria
import static com.stormpath.sdk.api.ApiKeys.options
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
     * @since 1.1.0
     */
    @Test
    void testIsLinkedToAccount() {

        def app = createTempApp()

        //create a test account:
        def acct = createTestAccount(app)

        //create a user group (to be the account store of the other account):
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDir")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        //create another account (in a different account store)
        def acct2 = createTempAccountInDir(dir)

        //link the accounts
        AccountLink accountLink = acct.link(acct2)
        deleteOnTeardown(accountLink)

        assertEquals acct.getLinkedAccounts().size, 1
        assertEquals acct.getAccountLinks().size, 1
        assertEquals accountLink.leftAccount.href, acct.href
        assertEquals accountLink.rightAccount.href, acct2.href

        assertTrue acct.isLinkedToAccount(acct2.href)
        assertTrue acct.isLinkedToAccount(acct2.href.toLowerCase())
        assertTrue acct.isLinkedToAccount(acct2.href.toUpperCase())
        assertFalse acct.isLinkedToAccount(acct2.href.substring(0, acct2.href.length() - 2) + "*")
        assertNotEquals acct.getDirectory().getHref(), acct2.getDirectory().getHref()

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

        limit = 10
        offset = 1
        apiKeys = acct.getApiKeys(criteria().offsetBy(offset).limitTo(limit).withTenant())
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
     * @since 1.0.RC3
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

    // @since 1.0.RC3
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

    //@since 1.0.RC3
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
     * @since 1.0.RC3
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

        //Changing to a dynamic size based on the first resource that is received from the server, because having
        //hardcoded makes it failed when a new property/resource is added in the server API.
        final int EXPECTED_PROPERTIES_SIZE = properties01.size();

        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 0)
        assertSame(properties01, properties02)

        account01.setEmail("new@email.com")
        assertEquals(account01.getEmail(), "new@email.com")
        assertEquals(account02.getEmail(), account01.getUsername() + "@nowhere.com")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 0)

        account01.save()

        assertSame(properties01, properties02)
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 0)

        assertEquals(account01.getEmail(), account02.getEmail())

        account02.setMiddleName("New Middle Name for Account02")
        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 1)

        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 1)

        account01.setMiddleName("New Middle Name for Account01")
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 1)
        account02.save()

        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 0)
        account01.save()
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account01")
        assertEquals(properties01.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), EXPECTED_PROPERTIES_SIZE)
        assertEquals(dirtyProperties02.size(), 0)

    }

    //@since 1.0.RC3
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
     * @since 1.0.RC4.6
     */
    @Test
    void testGetGroupsWithTimestampFilter() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('StormpathTimestampTest'))
                .setPassword("Changeme123")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@mail.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        DateFormat df = new ISO8601DateFormat();

        def group01 = client.instantiate(Group)
        group01.name = uniquify("StormpathTimestampTest Group01")
        group01 = app.createGroup(group01)
        account.addGroup(group01)
        deleteOnTeardown(group01)

        def groupList = account.getGroups(Groups.where(Groups.name().eqIgnoreCase(group01.name)).and(Groups.createdAt().equals(group01.getCreatedAt())))
        assertNotNull groupList
        def retrieved = groupList.iterator().next()
        assertEquals retrieved.href, group01.href

        def group02 = client.instantiate(Group)
        group02.name = uniquify("StormpathTimestampTest Group02")
        group02 = app.createGroup(group02)
        account.addGroup(group02)
        deleteOnTeardown(group02)

        groupList = account.getGroups(Groups.where(Groups.name().eqIgnoreCase(group02.name)).and(Groups.createdAt().equals(group02.getCreatedAt())))
        assertNotNull groupList
        retrieved = groupList.iterator().next()
        assertEquals retrieved.href, group02.href
    }

    /**
     * Test for https://github.com/stormpath/stormpath-sdk-java/issues/112
     * @since 1.0.RC3
     */
    @Test
    void testGetGroupsWithLimitAndOffset() {
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

        //Let's check with have 2 groups
        def groups = account.getGroups(Groups.criteria().orderByName().ascending());
        assertEquals(groups.getLimit(), 25);
        assertEquals(groups.getProperty("items").size, 2);

        //Let's retrieve 1 group per page
        groups = account.getGroups(Groups.criteria().limitTo(1).orderByName().ascending());
        assertEquals(groups.getLimit(), 1);
        assertEquals(groups.getProperty("items").size, 1);
        assertEquals(groups.getOffset(), 0);

        Group firstGroupWithOffset0 = groups.iterator().next();

        assertNotNull(firstGroupWithOffset0);
        assertEquals(firstGroupWithOffset0.getHref(), group01.getHref());

        //Since we have 2 groups and offset = 1 here, then this page should only have 1 group, the last one
        groups = account.getGroups(Groups.criteria().offsetBy(1).orderByName().ascending());
        assertEquals(groups.getLimit(), 25);
        assertEquals(groups.getProperty("items").size, 1);
        assertEquals(groups.getOffset(), 1);

        Group firstGroupWithOffset1 = groups.iterator().next();

        assertNotNull(firstGroupWithOffset1);
        assertEquals(firstGroupWithOffset1.getHref(), group02.getHref());

        assertTrue(firstGroupWithOffset0.getHref() != firstGroupWithOffset1.getHref());
    }

    /**
     * Test for https://github.com/stormpath/stormpath-sdk-java/issues/112
     * @since 1.0.RC3
     */
    @Test
    void testGetGroupswithAccountMembershipsLimitAndOffset() {
        def app = createTempApp()
        def account01 = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct01'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account01.setEmail(account01.getUsername() + "@nowhere.com")
        account01 = app.createAccount(Accounts.newCreateRequestFor(account01).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account01)

        def account02 = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct02'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account02.setEmail(account02.getUsername() + "@nowhere.com")
        account02 = app.createAccount(Accounts.newCreateRequestFor(account02).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account02)

        def group = client.instantiate(Group)
        group.name = uniquify("My Group01")
        group = app.createGroup(group)
        deleteOnTeardown(group)

        account01.addGroup(group)
        account02.addGroup(group)

        GroupList groups = account01.getGroups(Groups.criteria().withAccountMemberships().orderByName().ascending());

        Group groupFromCollecion = groups.iterator().next()

        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("limit"), 25)
        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("items").size, 2)

        groups = account01.getGroups(Groups.criteria().withAccountMemberships(1).orderByName().ascending());

        groupFromCollecion = groups.iterator().next()

        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("limit"), 1)
        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("items").size, 1)

        def accountHrefFromGroupCollectionWithLimit1 = groupFromCollecion.getProperty("accountMemberships").get("items").get(0).get("href")

        groups = account01.getGroups(Groups.criteria().withAccountMemberships(25, 1).orderByName().ascending());

        groupFromCollecion = groups.iterator().next()

        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("limit"), 25)
        assertEquals(groupFromCollecion.getProperty("accountMemberships").get("items").size, 1)

        def accountHrefFromGroupCollectionWithOffset1 = groupFromCollecion.getProperty("accountMemberships").get("items").get(0).get("href")

        //Since we set offset = 1 in the second collection, the account must be different than the one obtained in the first collection
        assertNotEquals(accountHrefFromGroupCollectionWithLimit1, accountHrefFromGroupCollectionWithOffset1)
    }

    /**
     * Test for https://github.com/stormpath/stormpath-sdk-java/issues/154
     * @since 1.0.RC4
     */
    @Test
    public void testGetApplications() {

        def app = createTempApp()

        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-GetApplications'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@stormpath.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        def count = 0
        for(Application application : account.getApplications()) {
            count++
        }
        assertEquals(count, 1)

        count = 0
        for (Application application : account.getApplications(Applications.where(Applications.name().eqIgnoreCase("this app does not exist")))) {
            count++
        }
        assertEquals(count, 0)

        //Let's create a second app
        def app1 = createApplication()
        Directory dirForApp = app.getDefaultAccountStore()
        app1.addAccountStore(dirForApp) //the directory where account resides is now also an account store of a different app,
                                        // thus the account belongs to 2 applications now

        count = 0
        for(Application application : account.getApplications()) {
            count++
        }
        assertEquals(count, 2)

        count = 0
        for (Application application : account.getApplications(Applications.where(Applications.name().eqIgnoreCase(app1.getName())))) {
            count++
        }
        assertEquals(count, 1)

        count = 0
        def queryParams = new HashMap<String, Object>()
        queryParams.put("name", app.getName())
        for (Application application : account.getApplications(queryParams)) {
            count++
        }
        assertEquals(count, 1)

        ((Directory)app1.getDefaultAccountStore()).delete() //deleting app1's account store should not have any effect

        count = 0
        for(Application application : account.getApplications()) {
            count++
        }
        assertEquals(count, 2)

        app1.delete() //now, account should belong to a single app

        count = 0
        for(Application application : account.getApplications()) {
            count++
        }
        assertEquals(count, 1)

        //create a group
        def group = client.instantiate(Group)
        group.name = uniquify('Java SDK: AccountIT.testGetApplications')
        deleteOnTeardown(group)

        group = dirForApp.createGroup(group) //a new group in the account's dir is created
        account.addGroup(group) //the account belongs to a new group now
        def app2 = createTempApp()
        app2.addAccountStore(group) //a new app (app2) is created and it has the account's group as an account store

        count = 0
        for(Application application : account.getApplications()) {
            count++
        }
        assertEquals(count, 2)

    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    public void testAddGroupError() {
        //create an App
        def app = createTempApp()
        Directory directory = app.getDefaultAccountStore() as Directory

        //create an account
        def account = client.instantiate(Account)
                .setUsername(uniquify('JSDK_testAddGroupErrors'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@stormpath.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        try{
            account.addGroup("SuperInvalid")
            fail("Should have failed due to group not found in this Account's directory.")
        } catch (Exception e){
            assertEquals "The specified group was not found in this Account's directory.", e.getMessage()
        }
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    public void testAddAndRemoveGroup() {

        //create an App
        def app = createTempApp()
        Directory directory = app.getDefaultAccountStore() as Directory

        //create a group
        def group = client.instantiate(Group)
        group.name = uniquify('JSDK: testAddGroup Group1')
        group = directory.createGroup(group)
        deleteOnTeardown(group)

        //create an account
        def account = client.instantiate(Account)
                .setUsername(uniquify('JSDK_testAddGroup_Account_1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@stormpath.com")
        account = directory.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        //add account to group
        GroupMembership membership = account.addGroup(group)
        assertNotNull membership
        assertEquals account.getGroups().size, 1
        assertEquals membership.account.href, account.href
        assertEquals membership.group.href, group.href

        //add account to group using the href

        //create a second group
        def group2 = client.instantiate(Group)
        group2.name = uniquify('JSDK: testAddGroup Group2')
        group2 = directory.createGroup(group2)
        deleteOnTeardown(group2)

        membership = account.addGroup(group2.href)
        assertNotNull membership
        assertEquals account.getGroups().size, 2
        assertEquals membership.account.href, account.href
        assertEquals membership.group.href, group2.href

        //create a third group
        def group3 = client.instantiate(Group)
        group3.name = uniquify('JSDK: testAddGroup Group3')
        group3 = directory.createGroup(group3)
        deleteOnTeardown(group3)

        membership = account.addGroup(group3.name)
        assertNotNull membership
        assertEquals account.getGroups().size, 3
        assertEquals membership.account.href, account.href
        assertEquals membership.group.href, group3.href

        // Test Remove
        account = account.removeGroup(group)
        assertEquals 2, account.getGroups().size

        account = account.removeGroup(group2.name)
        assertEquals 1, account.getGroups().size

        account = account.removeGroup(group3.href)
        assertEquals 0, account.getGroups().size

        // Test exceptions

        def group5 = client.instantiate(Group)
        group5.name = uniquify('JSDK: testAddGroup Group5')
        group5 = directory.createGroup(group5)
        deleteOnTeardown(group5)

        try {
            account.removeGroup(group5)
            fail ("Should have failed due to account not present in group")
        } catch (Exception e){
            assertTrue e instanceof IllegalStateException
            assertEquals "This account does not belong to the specified group.", e.getMessage()
        }

        try {
            account.removeGroup("Invalid group info")
            fail ("Should have failed due to account not present in group")
        } catch (Exception e){
            assertTrue e instanceof IllegalStateException
            assertEquals "This account does not belong to the specified group.", e.getMessage()
        }
    }

    /**
     * @since 1.1.0
     */
    @Test
    void testLinkAndUnlinkAccount() {

        def app = createTempApp()
        //create a test account:
        def acct = createTestAccount(app)
        deleteOnTeardown(acct)

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDir")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        //create another account (in a different account store)
        def acct2 = createTempAccountInDir(dir)

        //link the accounts acct and acct2
        AccountLink accountLink = acct.link(acct2)
        deleteOnTeardown(accountLink)

        assertEquals accountLink.leftAccount.href, acct.href
        assertEquals accountLink.rightAccount.href, acct2.href
        assertEquals acct.getLinkedAccounts().size, 1
        assertEquals acct.getAccountLinks().size, 1

        //create a second dir
        Directory dir2 = client.instantiate(Directory)
        dir2.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir2 = client.currentTenant.createDirectory(dir2);
        deleteOnTeardown(dir2)

        //create another account (in a different account store)
        def acct3 = createTempAccountInDir(dir2)

        //link the accounts acct and acct3
        AccountLink accountLink2 = acct.link(acct3)
        deleteOnTeardown(accountLink2)

        assertEquals accountLink2.leftAccount.href, acct.href
        assertEquals accountLink2.rightAccount.href, acct3.href
        assertEquals acct.getLinkedAccounts().size, 2
        assertEquals acct.getAccountLinks().size, 2

        // Test unlink
        acct.unlink(acct2)
        assertEquals 1, acct.getLinkedAccounts().size
        assertEquals 1, acct.getAccountLinks().size
        assertEquals false, acct.isLinkedToAccount(acct2.href)
        assertEquals true, acct.isLinkedToAccount(acct3.href)

        acct.unlink(acct3)
        assertEquals 0, acct.getLinkedAccounts().size
        assertEquals 0, acct.getAccountLinks().size
        assertEquals false, acct.isLinkedToAccount(acct2.href)
        assertEquals false, acct.isLinkedToAccount(acct3.href)


    }

    /**
     * @since 1.1.0
     */
    @Test
    void testLinkAccountErrors() {

        def app = createTempApp()
        //create a test account:
        def acct = createTestAccount(app)
        deleteOnTeardown(acct)

        //link same account - invalid
        try{
            acct.link(acct)
            fail ("Should fail because account cannot link to itself")
        } catch (Exception e){
            assertTrue e instanceof ResourceException
            ResourceException re = (ResourceException) e;
            assertEquals re.properties.status as String, '400'
            assertEquals re.properties.code as String , '7501'
        }

        assertEquals false, acct.isLinkedToAccount(acct.href)

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDir")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        //create another account (in a different account store)
        def acct2 = createTempAccountInDir(dir)


        //link the accounts acct and acct2 - vaild
        AccountLink accountLink = acct.link(acct2)
        deleteOnTeardown(accountLink)
        assertEquals true, acct.isLinkedToAccount(acct2.href)

        //link more than 1 account, in a given directory - invalid
        try{
            acct.link(acct2)
            fail ("Should fail because an these accounts are already linked")
        } catch (Exception e){
            assertTrue e instanceof ResourceException
            ResourceException re = (ResourceException) e;
            assertEquals re.properties.status as String, '409'
            assertEquals re.properties.code as String , '7500'
        }

        def acct3 = createTempAccountInDir(dir)

        //link more than 1 account, in a given directory - invalid
        try{
            acct.link(acct3)
            fail ("Should fail because an account cannot be linked to more than 1 account for a given directory. " +
                    "Here both acct2 and acct3 are in the same dir")
        } catch (Exception e){
            assertTrue e instanceof ResourceException
            ResourceException re = (ResourceException) e;
            assertEquals re.properties.status as String, '409'
            assertEquals re.properties.code as String , '7505'
        }

        assertEquals false, acct.isLinkedToAccount(acct3.href)

        //create a second dir
        Directory dir2 = client.instantiate(Directory)
        dir2.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir2 = client.currentTenant.createDirectory(dir2);
        deleteOnTeardown(dir2)

        def acct4 = createTempAccountInDir(dir2)
        def acct5 = createTempAccountInDir(dir2)

        //link with invalid href
        try{
            acct.link('invalidHref')
            fail ("Should fail because the provided href is not valid")
        } catch (Exception e) {
            assertTrue e instanceof ResourceException
            ResourceException re = (ResourceException) e;
            assertEquals re.properties.status as String, '404'
            assertEquals re.properties.code as String , '404'
        }

        //link accounts in the same dir - invalid
        try{
            acct4.link(acct5)
            fail ("Should fail because both accounts are in the same directory")
        } catch (Exception e) {
            assertTrue e instanceof ResourceException
            ResourceException re = (ResourceException) e;
            assertEquals re.properties.status as String, '409'
            assertEquals re.properties.code as String , '7504'
        }

        assertEquals false, acct4.isLinkedToAccount(acct5.href)

        acct.link(acct4.href) // valid
        assertEquals true, acct.isLinkedToAccount(acct4.href)
        acct4.unlink(acct) // valid
        assertEquals false, acct.isLinkedToAccount(acct4.href)

        acct.link(acct5) // valid (because acct4 is now unlinked)
        assertEquals true, acct5.isLinkedToAccount(acct.href)
        acct5.unlink(acct.href) // valid
        assertEquals false, acct5.isLinkedToAccount(acct.href)

    }

    /**
     * Asserts https://github.com/stormpath/stormpath-sdk-java/issues/520
     * @since 1.0.RC9
     */
    @Test
    public void testSaveWithResponseOptionsIsCachedProperly() {
        def app = createTempApp()
        Account account = createTestAccount(app)
        account = account.setGivenName("newFooGivenName");
        account = account.saveWithResponseOptions(Accounts.options().withCustomData())
        assertEquals account.getGivenName(), "newFooGivenName"
        account = client.getResource(account.getHref(), Account.class)
        assertEquals account.getGivenName(), "newFooGivenName"
    }

    //@since 1.0.RC3
    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

    String decryptSecretFromCacheMap(Map cacheMap) {

        if (cacheMap == null || cacheMap.isEmpty() || !cacheMap.containsKey(ApiKeyParameter.ENCRYPTION_METADATA.getName())) {
            return null
        }

        def apiKeyMetaData = cacheMap[ApiKeyParameter.ENCRYPTION_METADATA.getName()]

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
