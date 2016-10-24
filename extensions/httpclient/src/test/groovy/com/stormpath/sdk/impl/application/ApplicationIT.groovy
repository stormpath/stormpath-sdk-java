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
package com.stormpath.sdk.impl.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountLinkingPolicy
import com.stormpath.sdk.account.AccountLinkingStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.account.AutomaticProvisioningStatus
import com.stormpath.sdk.account.PasswordFormat
import com.stormpath.sdk.account.PasswordResetToken
import com.stormpath.sdk.account.VerificationEmailRequest
import com.stormpath.sdk.account.VerificationEmailRequestBuilder
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.authc.UsernamePasswordRequests
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.api.ApiKeyParameter
import com.stormpath.sdk.impl.client.RequestCountingClient
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator
import com.stormpath.sdk.impl.idsite.IdSiteClaims
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.saml.SamlResultStatus
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import com.stormpath.sdk.lang.Strings
import com.stormpath.sdk.mail.EmailStatus
import com.stormpath.sdk.oauth.AccessToken
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthPolicy
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationStatus
import com.stormpath.sdk.organization.Organizations
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.resource.ResourceException
import com.stormpath.sdk.saml.SamlPolicy
import com.stormpath.sdk.saml.SamlServiceProvider
import com.stormpath.sdk.tenant.Tenant
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.codec.binary.Base64
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Field

import static com.stormpath.sdk.application.Applications.newCreateRequestFor
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

class ApplicationIT extends ClientIT {

    def encryptionServiceBuilder = new ApiKeySecretEncryptionService.Builder()

    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>
     */
    @Test
    void testLoginWithCachingEnabled() {

        def username = uniquify('lonestarr')
        def password = 'Changeme1!'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = buildClient(true)

        def app = createTempApp()

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        def result = app.authenticateAccount(request)

        def cachedAccount = result.getAccount()

        assertEquals cachedAccount.username, acct.username
    }

    /**
     * @since 1.2.0
     */
    @Test
    void testLoginWithOrgNameKey() {
        def username = uniquify('thisisme')
        def password = 'Changeme1!'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = buildClient(true)

        def app = createTempApp()

        Organization org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_testLoginWithOrgNameKey"))
                .setDescription("Organization Description")
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)
        org = client.createOrganization(org)
        deleteOnTeardown(org)

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testLoginWithOrgNameKey")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)

        //create account store
        def orgAccountStoreMapping = org.addAccountStore(dir)
        deleteOnTeardown(orgAccountStoreMapping)

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        dir.createAccount(acct)

        ApplicationAccountStoreMapping accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping.setAccountStore(org)
        accountStoreMapping.setApplication(app)
        accountStoreMapping = app.createAccountStoreMapping(accountStoreMapping)
        deleteOnTeardown(accountStoreMapping)

        //Account belongs to org, therefore login must succeed
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).setOrganizationNameKey(org.nameKey).build()
        def result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)

        //No account store has been defined, therefore login must succeed
        request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)

        Organization org2 = client.instantiate(Organization)
        org2.setName(uniquify("JSDK_testLoginWithOrgNameKey_org2"))
                .setDescription("Organization Description 2")
                .setNameKey(uniquify("test").substring(2, 8))
                .setStatus(OrganizationStatus.ENABLED)
        org2 = client.createOrganization(org2)
        deleteOnTeardown(org2)

        //Account does not belong to org2, therefore login must fail
        try {
            request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).setOrganizationNameKey(org2.nameKey).build()
            app.authenticateAccount(request)
            fail("Should have thrown due to invalid username/password");
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 5114)
            assertTrue(e.getDeveloperMessage().contains("The specified Account Store is not one of the Application's assigned Account Stores."))
        }
    }

    @Test
    void testCreateAppAccount() {

        def app = createTempApp()

        def email = uniquify('deleteme') + '@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Changeme1!'

        def created = app.createAccount(account)

        //verify it was created:

        def found = app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email))).iterator().next()
        assertEquals(created.href, found.href)

        //test delete:
        found.delete()

        def list = app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email)))
        assertFalse list.iterator().hasNext() //no results
    }

    @Test
    void testApplicationPagination() {

        def applicationBaseName = UUID.randomUUID().toString()

        def app1 = client.instantiate(Application)
        app1.setName(applicationBaseName + "-JavaSDK1")
        app1 = client.createApplication(Applications.newCreateRequestFor(app1).build())

        deleteOnTeardown(app1)

        def app2 = client.instantiate(Application)
        app2.setName(applicationBaseName + "-JavaSDK2")
        app2 = client.createApplication(Applications.newCreateRequestFor(app2).build())

        deleteOnTeardown(app2)

        def expected = [app1.href, app2.href] as Set

        def apps = client.getApplications(Applications.where(Applications.name().startsWithIgnoreCase(applicationBaseName)).limitTo(1))

        assertEquals apps.limit, 1
        assertEquals apps.offset, 0
        assertEquals apps.size, 2

        def iterator = apps.iterator()
        while (iterator.hasNext()) {
            def app = iterator.next();
            assertTrue expected.remove(app.href)
        }

        assertEquals expected.size(), 0

        try {
            iterator.next()
            fail("should have thrown")
        } catch (NoSuchElementException e) {
            //ignore, exception was expected.
        }

        def newIterator = apps.iterator()

        assertTrue iterator != newIterator

        expected = [app1.href, app2.href] as Set

        while (newIterator.hasNext()) {
            def app = newIterator.next();
            assertTrue expected.remove(app.href)
        }

        assertEquals expected.size(), 0
    }

    @Test
    void testCreateAppGroup() {

        def tenant = client.currentTenant

        def app = client.instantiate(Application)

        def authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        //When no authenticationScheme is explicitly defined, SAuthc1RequestAuthenticator is used by default
        assertTrue authenticationScheme instanceof SAuthc1RequestAuthenticator

        app.name = uniquify("Java SDK IT App")

        def dirName = uniquify("Java SDK IT Dir")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectoryNamed(dirName).build())
        def dir = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()

        deleteOnTeardown(dir)
        deleteOnTeardown(app)

        Group group = client.instantiate(Group)
        group.name = uniquify('Java SDK IT Group')

        def created = app.createGroup(group)

        //verify it was created:

        def found = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name))).iterator().next()

        assertEquals(created.href, found.href)

        //test delete:
        found.delete()

        def list = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name)))
        assertFalse list.iterator().hasNext() //no results
    }

    /**
     * @since 1.2.0
     */
    @Test
    void testFilterApp() {

        def tenant = client.currentTenant

        def app1 = client.instantiate(Application)
        def app2 = client.instantiate(Application)

        app1.name = uniquify("Java SDK Filter IT App")
        app1.description = 'Java SDK IT App 01'

        app2.name = uniquify("Java SDK Filter IT App II")
        app2.description = 'Java SDK IT App 02'

        def dirName = uniquify("Java SDK Filter IT Dir")
        def dirName2 = uniquify("Java SDK IT Dir II")

        app1  = tenant.createApplication(Applications.newCreateRequestFor(app1).createDirectoryNamed(dirName).build())
        app2 = tenant.createApplication(Applications.newCreateRequestFor(app2).createDirectoryNamed(dirName2).build())

        deleteOnTeardown(app1)
        deleteOnTeardown(app2)

        //verify that the filter search works with a combination of criteria
        def foundApps2 = tenant.getApplications(Applications.where(Applications.filter('Java SDK Filter IT App')).and(Applications.description().endsWithIgnoreCase('02')))
        def foundApp2 = foundApps2.iterator().next()
        assertEquals(foundApp2.href, app2.href)

        //verify that the filter search works
        def allApps = tenant.getApplications(Applications.where(Applications.filter('Java SDK Filter IT App')))
        assertEquals(allApps.size(), 2)

        //verify that the filter search returns an empty collection if there is no match
        def emptyCollection = tenant.getApplications(Applications.where(Applications.filter('not_found')))
        assertTrue(emptyCollection.size() == 0)

        //verify that a non matching criteria added to a matching criteria is working as a final non matching criteria
        //ie. there are no properties matching 'not_found' but there are 1 account matching 'description=02'
        def emptyCollection2 = tenant.getApplications(Applications.where(Applications.filter('not_found')).and(Applications.description().endsWithIgnoreCase('02')))
        assertTrue(emptyCollection2.size() == 0)

        //verify that the filter search match with substrings
        def allApps2 = tenant.getApplications(Applications.where(Applications.filter("Java SDK Filter")))
        assertEquals(allApps2.size(), 2)

        //test delete:
        for (def app : allApps){
            app.delete()
        }
    }

    @Test
    void testCreateAppGroupWithSauthc1RequestAuthenticator() {

        //We are creating a new client with Digest Authentication
        def client = buildClient(AuthenticationScheme.SAUTHC1)

        def tenant = client.currentTenant

        def app = client.instantiate(Application)

        def authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue authenticationScheme instanceof SAuthc1RequestAuthenticator

        app.name = uniquify("Java SDK IT App")

        def dirName = uniquify("Java SDK IT Dir")

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectoryNamed(dirName).build())
        def dir = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()

        deleteOnTeardown(dir)
        deleteOnTeardown(app)

        Group group = client.instantiate(Group)
        group.name = uniquify('Java SDK Group')

        def created = app.createGroup(group)

        //verify it was created:

        def found = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name))).iterator().next()

        assertEquals(created.href, found.href)

        //test delete:
        found.delete()

        def list = app.getGroups(Groups.where(Groups.name().eqIgnoreCase(group.name)))
        assertFalse list.iterator().hasNext() //no results
    }

    @Test
    void testLoginWithAccountStore() {

        def username = uniquify('lonestarr')
        def password = 'Changeme1!'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = buildClient(true)

        def app = createTempApp()

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'

        Directory dir1 = client.instantiate(Directory)
        dir1.name = uniquify("Java SDK: ApplicationIT.testLoginWithAccountStore")
        dir1 = client.currentTenant.createDirectory(dir1);
        deleteOnTeardown(dir1)

        Directory dir2 = client.instantiate(Directory)
        dir2.name = uniquify("Java SDK: ApplicationIT.testLoginWithAccountStore")
        dir2 = client.currentTenant.createDirectory(dir2);
        deleteOnTeardown(dir2)

        ApplicationAccountStoreMapping accountStoreMapping1 = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping1.setAccountStore(dir1)
        accountStoreMapping1.setApplication(app)
        accountStoreMapping1 = app.createAccountStoreMapping(accountStoreMapping1)

        ApplicationAccountStoreMapping accountStoreMapping2 = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping2.setAccountStore(dir2)
        accountStoreMapping2.setApplication(app)
        accountStoreMapping2 = app.createAccountStoreMapping(accountStoreMapping2)

        dir1.createAccount(acct)
        deleteOnTeardown(acct)

        //Account belongs to dir1, therefore login must succeed
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password)
                .inAccountStore(accountStoreMapping1.getAccountStore()).build()
        def result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)

        try {
            //Account does not belong to dir2, therefore login must fail
            request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password)
                    .inAccountStore(accountStoreMapping2.getAccountStore()).build()
            app.authenticateAccount(request)
            fail("Should have thrown due to invalid username/password");
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 7104)
            assertTrue(e.getDeveloperMessage().contains("Login attempt failed because there is no Account in the Application's associated Account Stores with the specified username or email."))
        }

        //No account store has been defined, therefore login must succeed
        request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)
    }

    /**
     * @since 1.0.RC5
     */
    @Test
    void testLoginWithOrganizationAccountStore() {

        def username = uniquify('thisisme')
        def password = 'Changeme1!'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = buildClient(true)

        def app = createTempApp()

        Organization org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_testLoginWithOrganizationAccountStore"))
                .setDescription("Organization Description")
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)
        org = client.createOrganization(org)
        deleteOnTeardown(org)

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testLoginWithOrganizationAccountStore")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)

        //create account store
        def orgAccountStoreMapping = org.addAccountStore(dir)
        deleteOnTeardown(orgAccountStoreMapping)

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        dir.createAccount(acct)

        ApplicationAccountStoreMapping accountStoreMapping = client.instantiate(ApplicationAccountStoreMapping)
        accountStoreMapping.setAccountStore(org)
        accountStoreMapping.setApplication(app)
        accountStoreMapping = app.createAccountStoreMapping(accountStoreMapping)
        deleteOnTeardown(accountStoreMapping)

        //Account belongs to org, therefore login must succeed
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).inAccountStore(org).build()
        def result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)

        //No account store has been defined, therefore login must succeed
        request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        result = app.authenticateAccount(request)
        assertEquals(result.getAccount().getUsername(), acct.username)

        Organization org2 = client.instantiate(Organization)
        org2.setName(uniquify("JSDK_testLoginWithOrganizationAccountStore_org2"))
                .setDescription("Organization Description 2")
                .setNameKey(uniquify("test").substring(2, 8))
                .setStatus(OrganizationStatus.ENABLED)
        org2 = client.createOrganization(org2)
        deleteOnTeardown(org2)

        //Account does not belong to org2, therefore login must fail
        try {
            request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).inAccountStore(org2).build()
            result = app.authenticateAccount(request)
            fail("Should have thrown due to invalid username/password");
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 5114)
            assertTrue(e.getDeveloperMessage().contains("The specified Account Store is not one of the Application's assigned Account Stores."))
        }
    }

    /**
     * @since 1.0.RC5
     */
    @Test
    void testAddOrganizationAccountStoreWithCriteria() {
        def app2 = createTempApp()
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 1)

        Organization org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_testAddOrganizationAccountStoreMapping"))
                .setDescription("Organization")
                .setNameKey(uniquify("test").substring(2, 8))
                .setStatus(OrganizationStatus.ENABLED)
        org = client.currentTenant.createOrganization(org)
        deleteOnTeardown(org)

        def accountStoreMapping = app2.addAccountStore(Organizations.where(Organizations.name().eqIgnoreCase(org.name)))
        assertNotNull accountStoreMapping
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 2)
        deleteOnTeardown(accountStoreMapping)
    }

    /**
     * @since 1.0.RC8.2
     */
    @Test
    void testAddOrganizationAccountStoreAndRetrieveIt() {
        def app2 = createTempApp()
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 1)

        def name = uniquify("testAddOrganizationAccountStoresAndIterateOnIt");
        Organization org = client.instantiate(Organization)
        org.setName(name)
                .setDescription("Organization")
                .setNameKey(uniquify("test"))
                .setStatus(OrganizationStatus.ENABLED)
        org = client.currentTenant.createOrganization(org)
        deleteOnTeardown(org)

        def accountStoreMapping = app2.addAccountStore(Organizations.where(Organizations.name().eqIgnoreCase(org.name)))
        assertNotNull accountStoreMapping

        ApplicationAccountStoreMappingList accountStoreMappings = app2.getAccountStoreMappings();
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 2)

        for (ApplicationAccountStoreMapping mapping : accountStoreMappings) {
            AccountStore accountStore = mapping.getAccountStore();
            if (!(accountStore instanceof Organization)) {
                continue;
            }
            Organization organization = (Organization) accountStore;
            assertNotNull organization.href
            assertEquals organization.name, name
        }
    }

    /**
     * @since 1.0.RC7.7
     */
    @Test
    void testAddOrganizationAccountStore_Href() {
        def app2 = createTempApp()
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 1)

        Organization org = client.instantiate(Organization)
        org.setName(uniquify("JSDK_testAddOrganizationAccountStoreMapping_Href"))
                .setDescription("Organization")
                .setNameKey(uniquify("test").substring(2, 8))
                .setStatus(OrganizationStatus.ENABLED)
        org = client.currentTenant.createOrganization(org)
        deleteOnTeardown(org)

        def accountStoreMapping = app2.addAccountStore(org.href)
        assertNotNull accountStoreMapping
        assertAccountStoreMappingListSize(app2.getAccountStoreMappings(), 2)
        deleteOnTeardown(accountStoreMapping)
    }

    //@since 1.0.beta
    @Test
    void testGetNonexistentGoogleAccount() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testGetNonexistentGoogleAccount")
        GoogleProvider provider = client.instantiate(GoogleProvider.class);
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
        provider.setClientId(clientId).setClientSecret(clientSecret).setRedirectUri("https://www.myAppURL:8090/index.jsp");

        def createDirRequest = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectUri("https://www.myAppURL:8090/index.jsp")
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(createDirRequest)
        deleteOnTeardown(dir)

        def app = createTempApp()
        app.addAccountStore(dir)

        ProviderAccountRequest request = Providers.GOOGLE.account().setCode("4/MZ-Z4Xr-V6K61-Y0CE-ifJlyIVwY.EqwqoikzZTUSaDn_5y0ZQNiQIAI2iwI").build();

        try {
            app.getAccount(request)
            fail("should have thrown")
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 7200)
            assertTrue(e.getDeveloperMessage().contains("Stormpath was not able to complete the request to Google: this can be caused by either a bad Google Directory configuration, or the provided Account credentials are not valid."))
        }
    }

    @Test
    void testGetApiKeyById() {

        def app = createTempApp()

        def account = createTestAccount(app)

        def apiKey = account.createApiKey()

        //test invalid key
        def appApiKey = app.getApiKey("helloIamNotValid!");

        assertNull appApiKey

        appApiKey = app.getApiKey(apiKey.id)

        assertNotNull appApiKey
        assertEquals appApiKey, apiKey
    }

    @Test
    void testGetApiKeyByIdCacheDisabled() {

        def app = createTempApp()

        def account = createTestAccount(app)

        def apiKey = account.createApiKey()

        client = buildClient(false)

        app = client.dataStore.getResource(app.href, Application)

        def appApiKey = app.getApiKey(apiKey.id)

        assertNotNull appApiKey
        assertEquals appApiKey, apiKey

    }

    @Test
    void testGetApiKeyByIdCacheEnabled() {

        def client = buildClient()

        def app = client.instantiate(Application)

        app.setName(uniquify("Java SDK IT App"))

        client.currentTenant.createApplication(newCreateRequestFor(app).createDirectory().build())

        deleteOnTeardown(app.getDefaultAccountStore() as Directory)
        deleteOnTeardown(app)

        def account = createTestAccount(client, app)

        def apiKey = account.createApiKey()

        def apiKeyCache = client.dataStore.cacheManager.getCache(ApiKey.name)

        assertNotNull apiKeyCache

        def apiKeyCacheValue = apiKeyCache.get(apiKey.href)

        assertNotNull apiKeyCacheValue

        assertNotEquals apiKeyCacheValue['secret'], apiKey.secret

        assertEquals decryptSecretFromCacheMap(apiKeyCacheValue), apiKey.secret

        client = buildClient()

        def dataStore = (DefaultDataStore) client.dataStore

        app = dataStore.getResource(app.href, Application)

        def appApiKey = app.getApiKey(apiKey.id)

        assertNotNull appApiKey

        apiKeyCache = dataStore.cacheManager.getCache(ApiKey.name)

        assertNotNull apiKeyCache

        apiKeyCacheValue = apiKeyCache.get(apiKey.href)

        assertNotNull apiKeyCacheValue

        assertNotEquals apiKeyCacheValue['secret'], appApiKey.secret

        assertEquals decryptSecretFromCacheMap(apiKeyCacheValue), appApiKey.secret
    }

    /**
     * @see https://github.com/stormpath/stormpath-sdk-java/issues/164
     *
     * @since 1.0.RC9
     */
    @Test
    void testExpansionNotWorkingBecauseOfCache() {

        //Setup 1 application with 2 accounts
        def app = createTempApp()

        2.times {
            def account = createTestAccount(client, app)
            deleteOnTeardown(account)
        }

        //Get application without expanding the accounts attribute
        def appWithoutAccounts = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name))).first()
        assertNull getValue(AbstractResource, appWithoutAccounts, "properties").get("accounts").get("items"), "Application->Accounts shouldn't be expanded"

        //Get the application again twice expanding the accounts attribute
        2.times {
            def appWithAccounts = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.name)).withAccounts()).first()
            assertNotNull getValue(AbstractResource, appWithAccounts, "properties").get("accounts").get("items"), "Application->Accounts should be expanded"
        }
    }

    /**
     * @since 1.0.RC8
     */
    @Test
    void testSamlProperties() {
        def app = createTempApp()

        def samlPolicy = app.getSamlPolicy()

        assertNotNull samlPolicy.href
        assertNotNull samlPolicy.getSamlServiceProvider()
        assertNotNull samlPolicy.getSamlServiceProvider().getSsoInitiationEndpoint()

        //test invalid URI assignment attempt
        try {
            app.addAuthorizedCallbackUri("invalid")
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            //ignore, exception was expected.
        }

        def callbackUris = app.getAuthorizedCallbackUris()
        assertNotNull callbackUris
        assertFalse callbackUris.iterator().hasNext()

        String uri = "https://myapplication.com/whatever/callback"
        app.addAuthorizedCallbackUri(uri)

        callbackUris = app.getAuthorizedCallbackUris()
        assertNotNull callbackUris
        assertTrue callbackUris.iterator().hasNext()
        assertEquals callbackUris.iterator().next(), uri

        List<String> testUris = new ArrayList<String>()
        testUris.add("https://myapplication.com/callback1")
        testUris.add("https://myapplication.com/callback2")
        testUris.add("https://myapplication.com/callback3")
        testUris.add("https://myapplication.com/callback4")

        app.setAuthorizedCallbackUris(testUris)
        callbackUris = app.getAuthorizedCallbackUris()
        assertNotNull callbackUris
        assertEquals callbackUris.size(), 4
    }

    @Test
    void testGetApiKeyByIdWithOptions() {

        def app = createTempApp()

        def account = createTestAccount(app)

        def apiKey = account.createApiKey()

        def client = buildClient(false) // need to disable caching because the api key is cached without the options
        app = client.getResource(app.href, Application)
        def appApiKey = app.getApiKey(apiKey.id, ApiKeys.options().withAccount().withTenant())

        assertNotNull appApiKey
        assertEquals appApiKey.secret, apiKey.secret
        assertTrue(appApiKey.account.propertyNames.size() > 1) // testing expansion
        assertTrue(appApiKey.tenant.propertyNames.size() > 1) // testing expansion

    }

    @Test
    void testGetApiKeyByIdWithOptionsInCache() {

        def app = createTempApp()

        def account = createTestAccount(app)

        def apiKey = account.createApiKey()

        RequestCountingClient client = buildCountingClient();

        app = client.getResource(app.href, Application)
        def appApiKey = app.getApiKey(apiKey.id, ApiKeys.options().withAccount().withTenant())
        def appApiKey2 = app.getApiKey(apiKey.id, ApiKeys.options().withAccount().withTenant())

        assertNotNull appApiKey

        assertTrue(appApiKey.account.propertyNames.size() > 1)
        // testing expansion on the object retrieved from the server
        assertTrue(appApiKey.tenant.propertyNames.size() > 1)
        // testing expansion on the object retrieved from the server

        assertNotNull appApiKey2

        //Making sure that only two request were made to the server
        // 1) request to get the application
        // 2) request to get the apiKey (with options)
        assertEquals client.requestCount, 2

        // Compare apiKey get from the cache to the apiKey requested from cache
        assertEquals appApiKey.secret, appApiKey2.secret
        assertEquals appApiKey.account.email, appApiKey2.account.email
        assertEquals appApiKey.tenant.name, appApiKey2.tenant.name

        // Making sure that still only 2 requests were made to the server.
        assertEquals client.requestCount, 2

        def dataStore = (DefaultDataStore) client.dataStore

        // testing that the secret is encrypted in the cache
        def apiKeyCache = dataStore.cacheManager.getCache(ApiKey.name)
        assertNotNull apiKeyCache
        def apiKeyCacheValue = apiKeyCache.get(appApiKey2.href)
        assertNotNull apiKeyCacheValue
        assertNotEquals apiKeyCacheValue['secret'], appApiKey2.secret
        assertEquals decryptSecretFromCacheMap(apiKeyCacheValue), appApiKey2.secret

        // testing that the expansions made it to the cache
        def accountCache = dataStore.cacheManager.getCache(Account.name)
        assertNotNull accountCache
        def accountCacheValue = accountCache.get(appApiKey.account.href)
        assertNotNull accountCacheValue
        assertEquals accountCacheValue['username'], appApiKey.account.username

        def tenantCache = dataStore.cacheManager.getCache(Tenant.name)
        assertNotNull tenantCache
        def tenantCacheValue = tenantCache.get(appApiKey.tenant.href)
        assertNotNull tenantCacheValue
        assertEquals tenantCacheValue['key'], appApiKey.tenant.key
    }

    @Test
    void testCreateSsoRedirectUrl() {
        def app = createTempApp()

        def ssoRedirectUrlBuilder = app.newIdSiteUrlBuilder()

        String[] parts = com.stormpath.sdk.lang.Strings.split(app.getHref(), (char) "/")

        assertEquals ssoRedirectUrlBuilder.ssoEndpoint, parts[0] + "//" + parts[2] + "/sso"

        def ssoURL = ssoRedirectUrlBuilder.setCallbackUri("https://mycallbackuri.com/path").setPath("/mypath").setState("anyState").build()

        assertNotNull ssoURL

        String[] ssoUrlPath = ssoURL.split("jwtRequest=")

        assertEquals ssoUrlPath.length, 2
        assertTrue ssoURL.startsWith(app.getHref().substring(0, app.getHref().indexOf("/", 8)))

        StringTokenizer tokenizer = new StringTokenizer(ssoUrlPath[1], ".")

        //Expected JWT token 'base64Header'.'base64JsonPayload'.'base64Signature'
        assertEquals tokenizer.countTokens(), 3

        def base64Header = tokenizer.nextToken()
        def base64JsonPayload = tokenizer.nextToken()
        def base64Signature = tokenizer.nextToken()


        def objectMapper = new ObjectMapper()

        assertTrue Base64.isBase64(base64Header)
        assertTrue Base64.isBase64(base64JsonPayload)
        assertTrue Base64.isBase64(base64Signature)

        byte[] decodedJsonPayload = Base64.decodeBase64(base64JsonPayload)

        def jsonPayload = objectMapper.readValue(decodedJsonPayload, Map)

        assertEquals jsonPayload.cb_uri, "https://mycallbackuri.com/path"
        assertEquals jsonPayload.iss, client.dataStore.apiKey.id
        assertEquals jsonPayload.sub, app.href
        assertEquals jsonPayload.state, "anyState"
        assertEquals jsonPayload.path, "/mypath"
    }

    // @since 1.0.RC3
    @Test
    void testCreateSsoLogout() {
        def app = createTempApp()
        def ssoRedirectUrlBuilder = app.newIdSiteUrlBuilder()

        def ssoURL = ssoRedirectUrlBuilder.setCallbackUri("https://mycallbackuri.com/path").forLogout().build()

        assertNotNull ssoURL

        String[] ssoUrlPath = ssoURL.split("jwtRequest=")

        assertEquals ssoUrlPath.length, 2

        StringTokenizer tokenizer = new StringTokenizer(ssoUrlPath[1], ".")

        //Expected JWT token 'base64Header'.'base64JsonPayload'.'base64Signature'
        assertEquals tokenizer.countTokens(), 3

        def base64Header = tokenizer.nextToken()
        def base64JsonPayload = tokenizer.nextToken()
        def base64Signature = tokenizer.nextToken()


        def objectMapper = new ObjectMapper()

        assertTrue Base64.isBase64(base64Header)
        assertTrue Base64.isBase64(base64JsonPayload)
        assertTrue Base64.isBase64(base64Signature)

        byte[] decodedJsonPayload = Base64.decodeBase64(base64JsonPayload)

        def jsonPayload = objectMapper.readValue(decodedJsonPayload, Map)

        assertTrue ssoURL.startsWith(ssoRedirectUrlBuilder.ssoEndpoint + "/logout?jwtRequest=")
        assertEquals jsonPayload.cb_uri, "https://mycallbackuri.com/path"
        assertEquals jsonPayload.iss, client.dataStore.apiKey.id
        assertEquals jsonPayload.sub, app.href
    }

    def Account createTestAccount(Application app) {
        return createTestAccount(client, app)
    }

    def Account createTestAccount(Client client, Application app) {

        def email = uniquify('deleteme') + '@stormpath.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Changeme1!'

        app.createAccount(account)
        deleteOnTeardown(account)

        return account
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

    /**
     * Asserts <a href="https://github.com/stormpath/stormpath-sdk-java/issues/58">Issue 58</a>.
     * @since 1.0.RC
     */
    @Test
    void testCreateApplicationViaTenantActions() {
        Application app = client.instantiate(Application)
        app.name = uniquify("Java SDK: ApplicationIT.testCreateApplicationViaTenantActions")
        app = client.createApplication(app);
        deleteOnTeardown(app)
        assertNotNull app.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testCreateApplicationRequestViaTenantActions() {
        Application app = client.instantiate(Application)
        app.name = uniquify("Java SDK: ApplicationIT.testCreateApplicationRequestViaTenantActions")
        def request = Applications.newCreateRequestFor(app).build()
        app = client.createApplication(request)
        deleteOnTeardown(app)
        assertNotNull app.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testGetApplicationsViaTenantActions() {
        def appList = client.getApplications()
        assertNotNull appList.href
    }

    /**
     * @since 1.0.RC
     */
    @Test(enabled = false)
    //ignoring because of sporadic Travis failures
    void testGetApplicationsWithMapViaTenantActions() {
        def map = new HashMap<String, Object>()
        def appList = client.getApplications(map)
        assertNotNull appList.href

    }

    /**
     * @since 1.0.RC
     */
    @Test(enabled = false)
    //ignoring because of sporadic Travis failures
    void testGetApplicationsWithAppCriteriaViaTenantActions() {
        def appCriteria = Applications.criteria()
        def appList = client.getApplications(appCriteria)
        assertNotNull appList.href
    }

    /**
     * @since 1.0.RC3
     */
    @Test
    void testGetApplicationsWithCustomData() {

        def app = createTempApp()
        app.getCustomData().put("someKey", "someValue")
        app.save()

        def appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(app.getName())).withCustomData())

        def count = 0
        for (Application application : appList) {
            count++
            assertNotNull(application.getHref())
            assertEquals(application.getCustomData().size(), 4)
        }
        assertEquals(count, 1)
    }

    /**
     * @since 1.0.RC3
     */
    @Test(enabled = false)
    //ignoring because of sporadic Travis failures
    void testAddAccountStore_Dirs() {
        def count = 1
        while (count >= 0) { //re-trying once
            try {
                Directory dir = client.instantiate(Directory)
                dir.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_Dirs")
                dir.description = dir.name + "-Description"
                dir = client.currentTenant.createDirectory(dir);
                deleteOnTeardown(dir)

                def app = createTempApp()

                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                def retrievedAccountStoreMapping = app.addAccountStore(dir.name)
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                retrievedAccountStoreMapping = app.addAccountStore(dir.href)
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                retrievedAccountStoreMapping = app.addAccountStore(Directories.criteria().add(Directories.description().eqIgnoreCase(dir.description)))
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, dir.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                //Non-existent
                retrievedAccountStoreMapping = app.addAccountStore("non-existent Dir")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(dir.name.substring(0, 10))
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(dir.name + "XXX")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(dir.href + "XXX")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(Directories.criteria().add(Directories.description().eqIgnoreCase(dir.description + "XXX")))
                assertNull(retrievedAccountStoreMapping)
            } catch (Exception e) {
                if (!(e instanceof ResourceException)) {
                    //this is the known sporadic exception; we will fail if there is a different one
                    throw e;
                }
                count--
                if (count < 0) {
                    throw e
                }
                System.out.println("Test failed due to " + e.getMessage() + ".\nRetrying " + (count + 1) + " more time(s)")
                continue
            }
            break;  //no error, let's get out of the loop
        }
    }

    /**
     * @since 1.0.RC3
     */
    @Test(enabled = false)
    //ignoring because of sporadic Travis failures
    void testAddAccountStore_Groups() {
        def count = 1
        while (count >= 0) { //re-trying once
            try {
                Directory dir = client.instantiate(Directory)
                dir.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_Groups")
                dir.description = dir.name + "-Description"
                dir = client.currentTenant.createDirectory(dir);
                deleteOnTeardown(dir)

                Group group = client.instantiate(Group)
                group.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_Groups")
                group.description = group.name + "-Description"
                dir.createGroup(group)

                def app = createTempApp()

                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                def retrievedAccountStoreMapping = app.addAccountStore(group.name)
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, group.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                retrievedAccountStoreMapping = app.addAccountStore(group.href)
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, group.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                retrievedAccountStoreMapping = app.addAccountStore(Groups.criteria().add(Groups.description().eqIgnoreCase(group.description)))
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 2)
                assertEquals(retrievedAccountStoreMapping.accountStore.href, group.href)
                retrievedAccountStoreMapping.delete()
                assertAccountStoreMappingListSize(app.getAccountStoreMappings(), 1)

                //Non-existent
                retrievedAccountStoreMapping = app.addAccountStore("non-existent Group")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(group.name.substring(0, 10))
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(group.href + "XXX")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(group.name + "XXX")
                assertNull(retrievedAccountStoreMapping)

                retrievedAccountStoreMapping = app.addAccountStore(Groups.criteria().add(Groups.description().eqIgnoreCase(group.description + "XXX")))
                assertNull(retrievedAccountStoreMapping)
            } catch (Exception e) {
                if (!(e instanceof ResourceException)) {
                    //this is the known sporadic exception; we will fail if there is a different one
                    throw e;
                }
                count--
                if (count < 0) {
                    throw e
                }
                System.out.println("Test failed due to " + e.getMessage() + ".\nRetrying " + (count + 1) + " more time(s)")
                continue
            }
            break;  //no error, let's get out of the loop
        }
    }

    /**
     * @since 1.0.RC3
     */
    @Test(enabled = false, expectedExceptions = IllegalArgumentException)
    //ignoring because of sporadic Travis failures
    void testAddAccountStore_DirAndGroupMatch() {

        Directory dir01 = client.instantiate(Directory)
        dir01.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_DirAndGroupMatch")
        dir01.description = dir01.name + "-Description"
        dir01 = client.currentTenant.createDirectory(dir01);
        deleteOnTeardown(dir01)

        Directory dir02 = client.instantiate(Directory)
        dir02.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_DirAndGroupMatch")
        dir02.description = dir02.name + "-Description"
        dir02 = client.currentTenant.createDirectory(dir02);
        deleteOnTeardown(dir02)

        Group group = client.instantiate(Group)
        group.name = dir02.name
        group.description = group.name + "-Description"
        dir01.createGroup(group)

        def app = createTempApp()

        app.addAccountStore(group.name)
    }

    /**
     * @since 1.0.RC3
     */
    @Test(expectedExceptions = IllegalArgumentException)
    void testAddAccountStore_MultipleDirCriteria() {

        Directory dir01 = client.instantiate(Directory)
        dir01.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleDirCriteria")
        dir01.description = dir01.name + "-Description"
        dir01 = client.currentTenant.createDirectory(dir01);
        deleteOnTeardown(dir01)

        Directory dir02 = client.instantiate(Directory)
        dir02.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleDirCriteria")
        dir02.description = dir02.name + "-Description"
        dir02 = client.currentTenant.createDirectory(dir02);
        deleteOnTeardown(dir02)

        def app = createTempApp()

        app.addAccountStore(Directories.criteria().add(Directories.name().containsIgnoreCase("testAddAccountStore_MultipleDirCriteria")))
    }

    /**
     * @since 1.0.RC3
     */
    @Test(enabled = false, expectedExceptions = IllegalArgumentException)
    //ignoring because of sporadic Travis failures
    void testAddAccountStore_MultipleGroupCriteria() {

        Directory dir01 = client.instantiate(Directory)
        dir01.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleGroupCriteria")
        dir01.description = dir01.name + "-Description"
        dir01 = client.currentTenant.createDirectory(dir01);
        deleteOnTeardown(dir01)

        Directory dir02 = client.instantiate(Directory)
        dir02.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleGroupCriteria")
        dir02.description = dir02.name + "-Description"
        dir02 = client.currentTenant.createDirectory(dir02);
        deleteOnTeardown(dir02)

        Group group01 = client.instantiate(Group)
        group01.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleGroupCriteria")
        group01.description = group01.name + "-Description"
        dir01.createGroup(group01)

        Group group02 = client.instantiate(Group)
        group02.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_MultipleGroupCriteria")
        group02.description = group02.name + "-Description"
        dir02.createGroup(group02)

        def app = createTempApp()

        app.addAccountStore(Groups.criteria().add(Groups.name().containsIgnoreCase("testAddAccountStore_MultipleGroupCriteria")))
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByName_Null() {

        def app = createTempApp()

        assertNull app.addAccountStore("does not exist")
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByName_Exception() {

        def name = uniquify("Java SDK: ApplicationIT.testAddAccountStore")

        Directory dir = client.instantiate(Directory)
        dir.name = name
        dir.description = dir.name + "-Description"
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        Group group = client.instantiate(Group)
        group.name = name
        group.description = group.name + "-Description"
        dir.createGroup(group)

        def app = createTempApp()

        try {
            app.addAccountStore(name)
            fail("shouldn't get here")
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(),
                    "There are both a Directory and a Group matching the provided name in the current tenant. " +
                            "Please provide the href of the intended Resource instead of its name in order to univocally identify it."
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByName() {

        def dirName = uniquify("Java SDK: ApplicationIT.testAddAccountStore_dir")
        def groupName = uniquify("Java SDK: ApplicationIT.testAddAccountStore_group")

        Directory dir = client.instantiate(Directory)
        dir.name = dirName
        dir.description = dir.name + "-Description"
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        Group group = client.instantiate(Group)
        group.name = groupName
        group.description = group.name + "-Description"
        dir.createGroup(group)

        def app = createTempApp()
        def accountStoreMappting = app.addAccountStore(dirName)

        assertEquals accountStoreMappting.accountStore.href, dir.href

        app = createTempApp()
        accountStoreMappting = app.addAccountStore(groupName)

        assertEquals accountStoreMappting.accountStore.href, group.href
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByDirectoryCriteria() {

        def dirName = uniquify("Java SDK: ApplicationIT.testAddAccountStore_dir")

        Directory dir = client.instantiate(Directory)
        dir.name = dirName
        dir.description = dir.name + "-Description"
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def app = createTempApp()
        def accountStoreMapping = app.addAccountStore(Directories.where(Directories.name().eqIgnoreCase(dirName)))

        assertEquals accountStoreMapping.accountStore.href, dir.href
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByOrganizationCriteria() {

        def orgName = uniquify("Java SDK: ApplicationIT.testAddAccountStore_org")

        Organization org = client.instantiate(Organization)
        org.nameKey = uniquify("my-org")
        org.name = orgName
        org.description = org.name + "-Description"
        org = client.currentTenant.createOrganization(org)
        deleteOnTeardown(org)

        def app = createTempApp()
        def accountStoreMapping = app.addAccountStore(Organizations.where(Organizations.name().eqIgnoreCase(orgName)))

        assertEquals accountStoreMapping.accountStore.href, org.href
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAddAccountStoreByGroupCriteria() {

        def groupName = uniquify("Java SDK: ApplicationIT.testAddAccountStore_group")

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testAddAccountStore_dir")
        dir.description = dir.name + "-Description"
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        Group group = client.instantiate(Group)
        group.name = groupName
        group.description = group.name + "-Description"
        dir.createGroup(group)

        def app = createTempApp()
        def accountStoreMapping = app.addAccountStore(Groups.where(Groups.name().eqIgnoreCase(groupName)))

        assertEquals accountStoreMapping.accountStore.href, group.href
    }

    /**
     * @since 1.0.RC4.4
     */
    @Test
    void testGetSingleAccountFromCollection() {

        def app = createTempApp()
        def account01 = createTestAccount(app)

        assertEquals(app.getAccounts().single().toString(), account01.toString())

        def account02 = createTestAccount(app)

        try {
            app.getAccounts().single()
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Only a single resource was expected, but this list contains more than one item.")
        }

        assertEquals(app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(account02.getEmail()))).single().toString(), account02.toString())

        try {
            app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase("thisEmailDoesNotBelong@ToAnAccount.com"))).single()
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "This list is empty while it was expected to contain one (and only one) element.")
        }
    }

    /**
     * This test does not validate the the verification email has actually been received in the email address.
     * It only sends the verification email to check that no Exception is thrown.
     *
     * This test validates this issue has been solved: https://github.com/stormpath/stormpath-sdk-java/issues/218
     *
     * @since 1.0.RC4.5
     */
    @Test
    void testSendVerificationEmail() {

        def app = createTempApp()
        def dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testSendVerificationEmail")
        dir = client.currentTenant.createDirectory(dir);
        app.setDefaultAccountStore(dir)
        deleteOnTeardown(dir)
        def account = createTestAccount(app)
        dir.getAccountCreationPolicy().setVerificationEmailStatus(EmailStatus.ENABLED).save()

        VerificationEmailRequestBuilder verBuilder = Applications.verificationEmailBuilder();
        VerificationEmailRequest ver = verBuilder.setLogin(account.getEmail()).setAccountStore(dir).build();
        app.sendVerificationEmail(ver);

    }

    /**
     * @since 1.0.RC5
     */
    @Test
    void testLoginWithExpansion() {

        def username = 'lonestarr'
        def password = 'Changeme1!'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = buildClient(true)

        def app = createTempApp()

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = uniquify(username) + '@stormpath.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())

        def options = UsernamePasswordRequests.options().withAccount()
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).withResponseOptions(options).build()

        def result = app.authenticateAccount(request)

        //Let's check expansion worked by looking at the internal properties
        def properties = getValue(AbstractResource, result, "properties")
        assertTrue properties.get("account").size() > 15
        assertEquals properties.get("account").get("email"), acct.email

        //Let's re-authenticate without expansion
        request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        result = app.authenticateAccount(request)

        //Let's check that there is no expansion
        properties = getValue(AbstractResource, result, "properties")
        assertTrue properties.get("account").size() == 1
    }

    private
    static assertAccountStoreMappingListSize(ApplicationAccountStoreMappingList accountStoreMappings, int expectedSize) {
        int qty = 0;
        for (ApplicationAccountStoreMapping accountStoreMapping : accountStoreMappings) {
            qty++;
        }
        assertEquals(qty, expectedSize)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testSaveWithResponseOptions() {

        def app = createTempApp()
        def href = app.getHref()

        Account account = client.instantiate(Account)
        account.givenName = 'Jonathan'
        account.surname = 'Doe'
        account.email = uniquify('deleteme') + '@nowhere.com'
        account.password = 'Changeme1!'
        app.createAccount(account)
        deleteOnTeardown(account)

        app.getCustomData().put("testKey", "testValue")

        def retrieved = app.saveWithResponseOptions(Applications.options().withAccounts().withCustomData())

        assertEquals href, retrieved.getHref()

        Map properties = getValue(AbstractResource, retrieved, "properties")
        assertTrue properties.get("customData").size() > 1
        assertEquals properties.get("customData").get("testKey"), "testValue"
        assertTrue properties.get("accounts").size() > 1
        assertEquals properties.get("accounts").get("items")[0].get("email"), account.email
    }

    /** @since 1.0.RC4.6 */
    @Test
    void testResetPassword() {

        def app = createTempApp()

        def username = uniquify('lonestarr')
        def password = 'Changeme1!'
        def email = username + '@stormpath.com'

        def acct = client.instantiate(Account)
        acct.username = username
        acct.password = password
        acct.email = email
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
        def account = app.authenticateAccount(request).getAccount()
        assertEquals account.getEmail(), email

        PasswordResetToken token = app.sendPasswordResetEmail(email)
        assertEquals email, token.account.email
        assertEquals email, token.email

        account = app.resetPassword(token.getValue(), "newPassword123!")

        try {
            def upreq = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword(password).build()
            account = app.authenticateAccount(upreq).getAccount()
            fail("should have thrown due to wrong password")
        } catch (ResourceException e) {
            assertTrue(e.getMessage().contains("Login attempt failed because the specified password is incorrect."))
        }

        //login with the new password
        def upreq = UsernamePasswordRequests.builder().setUsernameOrEmail(username).setPassword("newPassword123!").build()
        account = app.authenticateAccount(upreq).getAccount()

        assertEquals account.getEmail(), email
    }

    /**
     * @since 1.0.RC5
     */
    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testPasswordImport() {

        def app = createTempApp()

        Account account = client.instantiate(Account)
                .setGivenName('John')
                .setSurname('DeleteMe')
                .setEmail("deletejohn@test.com")
                .setPassword('$2y$12$QjSH496pcT5CEbzjD/vtVeH03tfHKFy36d4J0Ltp3lRtee9HDxY3K')

        def created = app.createAccount(Accounts.newCreateRequestFor(account)
                .setRegistrationWorkflowEnabled(false)
                .setPasswordFormat(PasswordFormat.MCF)
                .build())
        deleteOnTeardown(created)

        //verify it was created:
        def found = app.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase("deletejohn@test.com"))).single()
        assertEquals(created.href, found.href)

        def upreq = UsernamePasswordRequests.builder().setUsernameOrEmail("deletejohn@test.com").setPassword("rasmuslerdorf").build()
        found = app.authenticateAccount(upreq).getAccount()
        assertEquals(created.href, found.href)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testPasswordImportErrors() {

        def app = createTempApp()

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DeleteMe'
        account.email = "deletejohn@test.com"
        account.password = '$INVALID$04$RZPSLGUz3dRdm7aRfxOeYuKeueSPW2YaTpRkszAA31wcPpyg6zkGy'

        try {
            app.createAccount(Accounts.newCreateRequestFor(account).setPasswordFormat(PasswordFormat.MCF).build())
            fail("Should have thrown")
        } catch (ResourceException e) {
            assertEquals e.getCode(), 2006
            assertTrue e.getDeveloperMessage().contains("is in an invalid format")
        }
    }

    /* @since 1.0.RC7 */

    @Test
    void testCreateAndRefreshTokenForAppAccount() {

        def app = createTempApp()

        def email = uniquify('testCreateToken+') + '@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Change&45+me1!'

        def created = app.createAccount(account)
        assertNotNull created.href
        deleteOnTeardown(created)

        OAuthPasswordGrantRequestAuthentication createRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setLogin(email).setPassword("Change&45+me1!").build();
        def result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(createRequest)

        assertNotNull result
        assertNotNull result.accessTokenString
        assertNotNull result.accessTokenHref
        assertEquals result.getAccessToken().getAccount().getEmail(), email
        assertEquals result.getAccessToken().getApplication().getHref(), app.href
        assertEquals(((Map) result.getAccessToken().getExpandedJwt().get("claims")).get("iss"), app.getHref())
        assertNotNull(((Map) result.getAccessToken().getExpandedJwt().get("claims")).get("rti"))
        assertEquals(((Map) result.getAccessToken().getExpandedJwt().get("header")).get("alg"), SignatureAlgorithm.HS256.toString())
        assertNotNull(result.getAccessToken().getExpandedJwt().get("signature"))

        OAuthRefreshTokenRequestAuthentication request = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder().setRefreshToken(result.getRefreshTokenString()).build();
        result = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(request)

        assertNotNull result
        assertTrue result.accessTokenString.size() > 1
        assertTrue result.refreshTokenString.size() > 1
        assertFalse(result.accessTokenString.equals(result.refreshTokenString))
        assertTrue(result.getAccessToken().getHref().contains("/accessTokens/"))
        assertTrue(result.getRefreshToken().getHref().contains("/refreshTokens/"))
        assertEquals result.getRefreshToken().getAccount().getEmail(), email
        assertEquals result.getRefreshToken().getApplication().getHref(), app.href
        assertEquals(((Map) result.getRefreshToken().getExpandedJwt().get("claims")).get("sub"), account.getHref())
        assertNull(((Map) result.getRefreshToken().getExpandedJwt().get("claims")).get("rti"))
        assertEquals(((Map) result.getRefreshToken().getExpandedJwt().get("header")).get("alg"), SignatureAlgorithm.HS256.toString())
        assertNotNull(result.getRefreshToken().getExpandedJwt().get("signature"))
    }

    /* @since 1.1.0 */
    @Test
    void testCreateClientCredentialsTokenForAppAccount() {

        def app = createTempApp()

        def account = createTestAccount(app)

        def apiKey = account.createApiKey()

        OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST.builder().setApiKeyId(apiKey.id).setApiKeySecret(apiKey.secret).build();
        def result = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(request)

        assertNotNull result.getAccessTokenHref()
        assertEquals result.getAccessToken().getHref(), result.getAccessTokenHref()
        assertEquals(result.getAccessToken().getAccount().getHref(), account.getHref())
        assertEquals(result.getAccessToken().getApplication().getHref(), app.getHref())
        assertTrue Strings.hasText(result.getAccessTokenString())
        //according to https://tools.ietf.org/html/rfc6749#section-4.4.3 no refresh token must be obtained as a result of client_credentials
        assertNull result.getRefreshToken()

        assertEquals result.getTokenType(), "Bearer"
        assertEquals result.getExpiresIn(), 3600
    }

    /* @since 1.0.RC7 */

    @Test
    void testRetrieveAndUpdateOAuthPolicy() {
        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy
        assertEquals oauthPolicy.getApplication().getHref(), app.href
        assertNotNull oauthPolicy.getTokenEndpoint()

        oauthPolicy.setAccessTokenTtl("P8D")
        oauthPolicy.setRefreshTokenTtl("P2D")
        oauthPolicy.save()

        oauthPolicy = app.getOAuthPolicy()
        assertEquals oauthPolicy.getAccessTokenTtl(), "P8D"
        assertEquals oauthPolicy.getRefreshTokenTtl(), "P2D"
        assertEquals oauthPolicy.getApplication().getHref(), app.href
    }

    /* @since 1.1.0 */
    @Test
    void testRetrieveAndUpdateAccountLinkingPolicy() {
        def app = createTempApp()

        AccountLinkingPolicy accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name() as String, 'DISABLED'
        assertFalse(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertNotNull accountLinkingPolicy.getAutomaticProvisioning()
        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name() as String, 'DISABLED'
        assertFalse(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED)
        accountLinkingPolicy.setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED)
        accountLinkingPolicy.setMatchingProperty("Email") // case shouldn't matter
        accountLinkingPolicy.save()

        accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNotNull accountLinkingPolicy.getMatchingProperty()
        assertEquals accountLinkingPolicy.getMatchingProperty(), 'email'
    }

    /* @since 1.1.0 */
    @Test
    void testRetrieveAndUpdateAccountLinkingPolicyPartially() {
        def app = createTempApp()

        AccountLinkingPolicy accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy

        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name() as String, 'DISABLED'
        assertFalse(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertNotNull accountLinkingPolicy.getAutomaticProvisioning()
        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name() as String, 'DISABLED'
        assertFalse(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setStatus(AccountLinkingStatus.ENABLED).save() // partially update status
        accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy

        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'DISABLED'
        assertFalse(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setAutomaticProvisioning(AutomaticProvisioningStatus.ENABLED).save() // partially update automatic provisioning
        accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())

        assertNull accountLinkingPolicy.getMatchingProperty()

        accountLinkingPolicy.setMatchingProperty("EMAIL") // partially update matchingProperty
        accountLinkingPolicy.save()

        accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNotNull accountLinkingPolicy.getMatchingProperty()
        assertEquals accountLinkingPolicy.getMatchingProperty(), 'email'

        accountLinkingPolicy.setMatchingProperty(null) // set matchingProperty to null
        accountLinkingPolicy.save()

        accountLinkingPolicy = app.getAccountLinkingPolicy()
        assertNotNull accountLinkingPolicy
        assertNotNull accountLinkingPolicy.getStatus()
        assertEquals accountLinkingPolicy.getStatus().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAccountLinkingEnabled())

        assertEquals accountLinkingPolicy.getAutomaticProvisioning().name(), 'ENABLED'
        assertTrue(app.getAccountLinkingPolicy().isAutomaticProvisioningEnabled())
        assertNull accountLinkingPolicy.getMatchingProperty()
    }


    /* @since 1.0.RC7 */

    @Test
    void testAuthenticateAndDeleteTokenForAppAccount() {

        def app = createTempApp()
        def account = createTestAccount(app)

        OAuthPasswordGrantRequestAuthentication grantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setLogin(account.email).setPassword("Changeme1!").build();
        def grantResult = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(grantRequest)

        // Authenticate token against Stormpath
        OAuthBearerRequestAuthentication authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(grantResult.getAccessTokenString()).build()
        def authResultRemote = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(authRequest)

        assertEquals authResultRemote.getApplication().getHref(), app.href
        assertEquals authResultRemote.getAccount().getHref(), account.href

        // Authenticate locally
        OAuthBearerRequestAuthenticationResult authResultLocal = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest)

        assertEquals authResultRemote.getHref(), authResultLocal.getHref()
        assertEquals authResultRemote.getAccount().getHref(), authResultLocal.getAccount().getHref()
        assertEquals authResultRemote.getApplication().getHref(), authResultLocal.getApplication().getHref()
        assertEquals authResultRemote.getJwt(), authResultLocal.getJwt()

        //Let's check the local access token actually exists in the backend
        assertNotNull client.getResource(authResultLocal.getHref(), AccessToken.class)

        grantResult.getAccessToken().delete()

        try {
            //try to authenticate deleted token
            Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(authRequest)
            fail("Should have thrown due to unexistent token")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.contains("Token does not exist. This can occur if the token has been manually deleted, or if the token has expired and removed by Stormpath.")
        }

        // Deleted tokens are still valid when local validation is used
        assertNotNull Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest)
    }

    /* @since 1.0.RC8.3 */

    @Test
    void testAttemptAuthenticationWithRefreshToken() {
        def app = createTempApp()
        def account = createTestAccount(app)

        OAuthPasswordGrantRequestAuthentication grantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
                .setLogin(account.email)
                .setPassword("Changeme1!")
                .build()

        def grantResult = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
                .forApplication(app)
                .authenticate(grantRequest)

        // Authenticate token against Stormpath using refresh token <--- INVALID
        OAuthBearerRequestAuthentication authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder()
                .setJwt(grantResult.getRefreshTokenString())
                .build()

        try {
            Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(authRequest)
            fail("Should have thrown")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.equals("JWT failed validation; it cannot be trusted.")
        }
    }

    /* @since 1.0.RC8.3 */

    @Test
    void testAttemptAuthenticationWithRefreshTokenWithLocalValidation() {
        def app = createTempApp()
        def account = createTestAccount(app)

        OAuthPasswordGrantRequestAuthentication grantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
                .setLogin(account.email)
                .setPassword("Changeme1!")
                .build()

        def grantResult = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
                .forApplication(app)
                .authenticate(grantRequest)

        // Authenticate token against Stormpath using refresh token <--- INVALID
        OAuthBearerRequestAuthentication authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder()
                .setJwt(grantResult.getRefreshTokenString())
                .build()

        try {
            Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest)
            fail("Should have thrown")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.equals("JWT failed validation; it cannot be trusted.")
        }
    }

    /* @since 1.0.RC7 */

    @Test
    void testInvalidTokenViaLocalValidation() {

        def app = createTempApp()
        def account = createTestAccount(app)

        OAuthPasswordGrantRequestAuthentication grantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setLogin(account.email).setPassword("Changeme1!").build();
        def grantResult = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(grantRequest)

        String jwt = grantResult.getAccessTokenString();
        Character charToChange = jwt.charAt(jwt.indexOf(".") + 5)
        Character replaceWith = charToChange.equals('X') ? 'Z' : 'X'
        String tamperedJwt = jwt.replace(charToChange, replaceWith)

        OAuthBearerRequestAuthentication authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(tamperedJwt).build()

        try {
            //try to authenticate a tampered token
            Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest)
            fail("Should have thrown")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.equals("JWT failed validation; it cannot be trusted.")
        }

        def app2 = createTempApp()
        authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(jwt).build()
        try {
            //try to use a valid token with another application
            Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app2).withLocalValidation().authenticate(authRequest)
            fail("Should have thrown")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.equals("JWT failed validation; it cannot be trusted.")
        }
    }

    /** @since 1.0.0 */
    @Test
    void testCreateTokenWithWrongAccountStore() {

        def app = createTempApp()

        def email = uniquify('testCreateToken+') + '@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Change&45+me1!'

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ApplicationIT.testCreateTokenWithWrongAccountStore")
        dir = client.currentTenant.createDirectory(dir);
        app.addAccountStore(dir)
        deleteOnTeardown(dir)

        try {
            OAuthPasswordGrantRequestAuthentication createRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setAccountStore(dir).setLogin(email).setPassword("Change&45+me1!").build()
            Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(createRequest)
            throw new Exception("Should have thrown. Expected Error code: 7104.");
        } catch (ResourceException e) {
            assertEquals(e.getCode(), 7104)
        }
    }

    /** @since 1.0.RC8 **/
    @Test
    void testDefaultSAMLPolicyAndProvider() {

        def app = createTempApp()

        SamlPolicy samlPolicy = app.getSamlPolicy();
        assertTrue samlPolicy.getHref().contains("/samlPolicies/")
        assertTrue samlPolicy.getSamlServiceProvider().getHref().contains("/samlServiceProviders/")
        SamlServiceProvider samlServiceProvider = samlPolicy.getSamlServiceProvider()
        assertTrue samlServiceProvider.getHref().contains("/samlServiceProviders/")
        assertTrue samlServiceProvider.getSsoInitiationEndpoint().getHref().contains("/saml/sso/idpRedirect")

    }

    /** since 1.0.RC9 */
    @Test
    void testNewSamlIdpUrlBuilder() {

        def app = createTempApp()
        def samlIdpUrlBuilder = app.newSamlIdpUrlBuilder()
        def callbackUri = "https://my.awesome.app/saml_callback"

        def urlString = samlIdpUrlBuilder.setCallbackUri(callbackUri).build()
        def jwtBeg = urlString.indexOf("accessToken=") + "accessToken=".length()
        def jwt = urlString.substring(jwtBeg)

        Jws<Claims> claims = Jwts.parser().setSigningKey(client.apiKey.secret.bytes).parseClaimsJws(jwt)
        String jwtCallbackUri = claims.getBody().get("cb_uri")

        assertEquals jwtCallbackUri, callbackUri
    }

    /** since 1.0.RC9 */
    // This just tests that the application can create a SamlCallbackHandler
    // DefaultSamlCallbackHandler should have its own tests
    @Test
    void testNewSamlCallbackHandler() {

        // setup result jwt
        def jwt = Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, client.apiKey.id)
            .setAudience(client.apiKey.id)
            .setExpiration(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)))
            .setIssuer("my issuer")
            .claim(IdSiteClaims.RESPONSE_ID, "my response id")
            .claim(IdSiteClaims.STATUS, SamlResultStatus.LOGOUT)
            .claim(IdSiteClaims.IS_NEW_SUBJECT, false)
            .signWith(SignatureAlgorithm.HS256, client.apiKey.secret.getBytes("UTF-8"))
        .compact()

        def req = createMock(HttpServletRequest)
        expect(req.getMethod()).andReturn(HttpMethod.GET.name()).times(2)
        expect(req.getParameter(IdSiteClaims.JWT_RESPONSE)).andReturn(jwt)

        replay req

        def app = createTempApp()
        def samlCallbackHandler = app.newSamlCallbackHandler(req)

        def result = samlCallbackHandler.accountResult

        assertFalse result.newAccount
    }
}
