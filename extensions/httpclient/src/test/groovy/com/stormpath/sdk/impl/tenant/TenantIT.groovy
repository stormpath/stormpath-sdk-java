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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GithubProvider
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.LinkedInProvider
import com.stormpath.sdk.tenant.Tenant
import com.stormpath.sdk.tenant.TenantOptions
import com.stormpath.sdk.tenant.Tenants
import org.testng.annotations.Test

import static com.stormpath.sdk.application.Applications.newCreateRequestFor
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class TenantIT extends ClientIT {

    //TODO - enable this test after assertions are made with the Cache statistics API.
    // This currently just prints output and isn't a valid test for asserting caching.
    @Test(enabled = false)
    void testCaching() {

        //create a bunch of apps:
        def tenant = client.currentTenant

        /*
        120.times { i ->
            Application app = client.instantiate(Application)
            app.name = "Testing Application ${i+1} " + UUID.randomUUID().toString()

            Application created =
                tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            resourcesToDelete.add(created);
        }*/

        int count = 0;
        def hrefs = []
        def list = tenant.getApplications()
        for( Application app : list) {
            hrefs << app.href
            count++
        }
        println "Applications seen: $count"

        hrefs.each { href ->
            client.getResource(href, Application)
        }

        count = 0
        hrefs = []
        list = tenant.getDirectories()
        for( Directory directory : list) {
            hrefs << directory.href
            count++
        }
        println "Directories seen: $count"

        hrefs.each { href ->
            client.getResource(href, Directory)
        }

        tenant = client.getResource(tenant.getHref(), Tenant)

        list = tenant.getApplications()
        for(Application app : list) {
            println "Application $app.name"
        }
        list = tenant.getDirectories()
        for(Directory dir : list) {
            println "Directory $dir.name"
        }

        def s = client.dataStore.cacheManager.toString()
        println '"cacheManager": ' + s;

        Thread.sleep(20)
    }

    /**
     * @since 1.0.RC4.3-SNAPSHOT
     */
    @Test(enabled = false) //ignoring because of sporadic Travis failures
    void testCurrentTenantWithOptions(){

        TenantOptions options = Tenants.options().withDirectories().withApplications()
        def tenant = client.getCurrentTenant(options)
        def apps = tenant.applications.size
        def dirs = tenant.directories.size

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCurrentTenantWithOptions Dir")
        dir = tenant.createDirectory(dir);
        deleteOnTeardown(dir)

        assertNotNull dir.href

        Application app = client.instantiate(Application)
        app.setName(uniquify("Java SDK: TenantIT.testCurrentTenantWithOptions App"))
        app = tenant.createApplication(newCreateRequestFor(app).build())
        deleteOnTeardown(app)

        assertNotNull app.href

        tenant = client.getCurrentTenant(options)

        // this also validates the workaround that avoids the incorrect load of expanded resources from the cache
        // https://github.com/stormpath/stormpath-sdk-java/issues/164
        assertEquals dirs + 1, tenant.directories.size
        assertEquals apps + 1, tenant.applications.size
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithoutProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithoutProvider")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "stormpath")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithGoogleProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithGoogleProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectUri("https://www.myAppURL:8090/index.jsp")
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "google")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(GoogleProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((GoogleProvider)provider).getClientId(), clientId)
        assertEquals(((GoogleProvider)provider).getClientSecret(), clientSecret)
        assertEquals(((GoogleProvider)provider).getRedirectUri(), "https://www.myAppURL:8090/index.jsp")
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithFacebookProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithFacebookProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.FACEBOOK.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "facebook")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(FacebookProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((FacebookProvider)provider).getClientId(), clientId)
        assertEquals(((FacebookProvider)provider).getClientSecret(), clientSecret)
    }

    //@since 1.0.RC3
    @Test
    void testCreateDirWithLinkedInProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithLinkedInProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.LINKEDIN.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "linkedin")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(LinkedInProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((LinkedInProvider)provider).getClientId(), clientId)
        assertEquals(((LinkedInProvider)provider).getClientSecret(), clientSecret)
    }

    //@since 1.0.0
    @Test
    void testGetAccounts() {
        def tenant = client.currentTenant
        def count = 0
        def accounts = tenant.getAccounts()
        for(Account acct : accounts) {
            count++
            //Let's iterate 3 times only, enough to know the collection was received while avoiding unnecessary traffic
            if(count > 2) break
        }
    }

    //@since 1.0.RC3
    @Test
    void testGetAccountsWithCriteria() {
        def uniqueEmail = uniquify("myUnique") + "@email.com"

        def criteria = Accounts.criteria().add(Accounts.email().eqIgnoreCase(uniqueEmail))

        def tenant = client.currentTenant

        def originalQty = 0
        def accounts = tenant.getAccounts(criteria)
        for(Account acct : accounts) {
            originalQty++
        }

        def app = createTempApp()
        Account account = client.instantiate(Account)
        account.email = uniqueEmail
        account.username = uniquify("username")
        account.password = uniquify("paS5w&")
        account.givenName = uniquify("givenName")
        account.surname = uniquify("surname")
        account = app.createAccount(account)
        deleteOnTeardown(account)

        def newQty = 0
        accounts = tenant.getAccounts(criteria)
        for(Account acct : accounts) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetAccountsWithMap() {
        def uniqueEmail = uniquify("myUnique") + "@email.com"

        def queryParams = ['email': uniqueEmail]

        def tenant = client.currentTenant

        def originalQty = 0
        def accounts = tenant.getAccounts(queryParams);
        for(Account acct : accounts) {
            originalQty++
        }

        def app = createTempApp()
        Account account = client.instantiate(Account)
        account.email = uniqueEmail
        account.username = uniquify("username")
        account.password = uniquify("paS5w&")
        account.givenName = uniquify("givenName")
        account.surname = uniquify("surname")
        account = app.createAccount(account)
        deleteOnTeardown(account)

        def newQty = 0
        accounts = tenant.getAccounts(queryParams)
        for(Account acct : accounts) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetGroups() {
        def tenant = client.currentTenant
        def count = 0
        def groups = tenant.getGroups()
        for(Group group : groups) {
            count++
            //Let's iterate 3 times only, enough to know the collection was received while avoiding unnecessary traffic
            if(count > 2) break
        }
    }

    //@since 1.0.RC3
    @Test
    void testGetGroupsWithCriteria() {
        def uniqueName = uniquify("uniqueGroupName")

        def criteria = Groups.criteria().add(Groups.name().eqIgnoreCase(uniqueName))

        def tenant = client.currentTenant

        def originalQty = 0
        def groups = tenant.getGroups(criteria)
        for(Group grp : groups) {
            originalQty++
        }

        def app = createTempApp()
        Group group = client.instantiate(Group)
        group.name = uniqueName
        group = app.createGroup(group)
        deleteOnTeardown(group)

        def newQty = 0
        groups = tenant.getGroups(criteria)
        for(Group grp : groups) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetGroupsWithMap() {
        def uniqueName = uniquify("uniqueGroupName")

        def queryParams = ['name': uniqueName]

        def tenant = client.currentTenant

        def originalQty = 0
        def groups = tenant.getGroups(queryParams);
        for(Group grp : groups) {
            originalQty++
        }

        def app = createTempApp()
        Group group = client.instantiate(Group)
        group.name = uniqueName
        group = app.createGroup(group)
        deleteOnTeardown(group)

        def newQty = 0
        groups = tenant.getGroups(queryParams)
        for(Group grp : groups) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.0
    @Test
    void testCreateDirWithGithubProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithGithubProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GITHUB.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "github")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(GithubProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((GithubProvider)provider).getClientId(), clientId)
        assertEquals(((GithubProvider)provider).getClientSecret(), clientSecret)
    }

}
